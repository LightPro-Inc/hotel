package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.formular.Formular;
import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.SalesInterface;
import com.infrastructure.core.UseCode;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.Products;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.Remise;
import com.sales.domains.api.Sales;
import com.sales.domains.impl.ProductNone;
import com.securities.api.MesureUnit;
import com.securities.api.MesureUnitType;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;

public final class SalesInterfaceImpl implements SalesInterface {

	private final transient Hotel hotel;
	private final transient Sales sales;
	private final transient Base base;
	private final transient DateTimeFormatter dateFormatter;
	
	public SalesInterfaceImpl(final Base base, final Hotel hotel, final Sales sales){
		this.base = base;
		this.hotel = hotel;
		this.sales = sales;
		this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	}

	@Override
	public void syncProduct(RoomCategory roomCategory) throws IOException {
		productOf(roomCategory);
	}
	
	private Product productOf(RoomCategory roomCategory) throws IOException {
		ProductCategory productCategory = getHotelCategory();
		
		Products products = sales.products().of(productCategory);
		
		Product product = sales.products().build(roomCategory.id());
		if(product.isNone()){
			product = products.add(roomCategory.id(), roomCategory.name(), StringUtils.EMPTY, StringUtils.EMPTY, productCategory, StringUtils.EMPTY, mesureUnit(), "Nuitée(s)", 1);
			product.pricing().update(roomCategory.nightPrice(), PricingMode.FIX, 0, NumberValueType.AMOUNT);
		}else{
			product.update(roomCategory.name(), StringUtils.EMPTY, StringUtils.EMPTY, productCategory, StringUtils.EMPTY, mesureUnit(), "Chambre", 1);
			
			double sumtTva = 0;
			for (Tax tax : product.taxes().all()) {
				sumtTva += tax.decimalValue();
			}
			
			double hTtax = 1 / (1 + sumtTva);
			Formular formular = sales.company().currency()
					                           .calculator().withExpression("{base} * {httax}")
					                           .withParam("{base}", roomCategory.nightPrice())
					                           .withParam("{httax}", hTtax);
			
			double htNightPrice = formular.result();
			product.pricing().update(htNightPrice, PricingMode.FIX, 0, NumberValueType.AMOUNT);
		}	
		
		return product;
	}
	 
	@Override
	public ProductCategory getHotelCategory() throws IOException {
		ProductCategory productCategory = sales.productCategories().build(hotel.id());
		if(productCategory.isNone()) {
			// ajouter la categorie
			productCategory = sales.productCategories().add(hotel.id(), ProductCategoryType.SERVICE, "Hôtel", "", UseCode.USER);
		}
		
		return productCategory;
	}
	
	private MesureUnit mesureUnit() throws IOException{
		
		MesureUnit mesureUnit = sales.mesureUnits().build(hotel.id());		
		if(mesureUnit.isNone()) {
			mesureUnit = sales.mesureUnits().add(hotel.id(), "Nuitée(s)", "Nuitée(s)", MesureUnitType.TIME);
		}
		
		return mesureUnit;
	}

	@Override
	public PurchaseOrder generateOrder(Booking booking) throws IOException {
		
		if(booking.isNone())
			throw new IllegalArgumentException("Générer un bon de commande : vous devez spécifier la réservation !");
		
		if(!booking.order().isNone())
			throw new IllegalArgumentException("Un bon de commande à déjà été généré pour cette réservation !");
		
		// créer la commande
		PurchaseOrder order = hotel.orders().add(LocalDate.now(), LocalDate.now(), PaymentConditionStatus.DIRECT_PAYMENT, StringUtils.EMPTY, "Location de chambre", StringUtils.EMPTY, booking.customer(), booking.seller(), 0);
		Product product = productOf(booking.room().category());
		
		String name = String.format("Réservation chambre N° %s (%s - %s)", booking.room().number(), booking.start().format(dateFormatter), booking.end().format(dateFormatter));		
		order.products().add(booking.id(), product.category(), product, name, StringUtils.EMPTY, booking.numberOfDays(), product.pricing().fixPrice(), product.pricing().remise(), product.taxes().all(), new ProductNone());
		
		// lier à la réservation
		BookingMetadata dm = BookingMetadata.create();
		DomainStore ds = base.domainsStore(dm).createDs(booking.id());
		ds.set(dm.orderIdKey(), order.id());
		
		return order;
	}

	@Override
	public void linkOrderToBooking(PurchaseOrder order, Booking booking) throws IOException {
		
		PurchaseOrder actualOrder = booking.order();
		
		if(!actualOrder.isNone()) {
			
			 if(actualOrder.status() != PurchaseOrderStatus.CREATED)
				 throw new IllegalArgumentException("Le bon de commande a déjà été validé !");
			 
			 // recopier la ligne de l'article dans le nouveau bon de commande
			 OrderProduct orderProduct = actualOrder.products().first();
			 
			 ProductCategory category = orderProduct.product().category();
			 Product product = orderProduct.product();
			 String name = orderProduct.name();
			 String description = orderProduct.description();
			 double quantity = orderProduct.quantity();
			 double unitPrice = orderProduct.unitPrice();
			 Remise remise = orderProduct.remise();			 
			 List<Tax> taxes = orderProduct.taxes().all().stream().map(m -> m).collect(Collectors.toList());
			 
			// supprimer le bon de commande existant
			 hotel.orders().delete(actualOrder);

			 // 
			 order.products().add(booking.id(), category, product, name, description, quantity, unitPrice, remise, taxes, new ProductNone());			 
		}
			
		// lier à la réservation
		BookingMetadata dm = BookingMetadata.create();
		DomainStore ds = base.domainsStore(dm).createDs(booking.id());
		ds.set(dm.orderIdKey(), order.id());
	}

	@Override
	public PurchaseOrder updateOrderOf(Booking booking) throws IOException {
		
		PurchaseOrder order = booking.order();
		if(order.isNone())
			return generateOrder(booking);
		
		OrderProduct orderProduct = order.products().build(booking.id());
		if(orderProduct.isNone())
			throw new IllegalArgumentException("Le bon de commande est erroné !");
		
		 Product product = productOf(booking.room().category());
		 String name = String.format("Réservation chambre N° %s (%s - %s)", booking.room().number(), booking.start().format(dateFormatter), booking.end().format(dateFormatter));
		 String description = orderProduct.description();
		 int quantity = booking.numberOfDays();
		 double unitPrice = product.pricing().fixPrice();
		 Remise remise = product.pricing().remise();			 
		 List<Tax> taxes = product.taxes().all();
		 
		 // supprimer la ligne d'article existante
		 order.products().delete(orderProduct);

		 // 
		 order.products().add(booking.id(), product.category(), product, name, description, quantity, unitPrice, remise, taxes, new ProductNone());
		 
		 return order;
	}
}
