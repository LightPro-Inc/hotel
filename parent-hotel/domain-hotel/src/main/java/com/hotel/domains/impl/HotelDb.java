package com.hotel.domains.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.ConstRoomFloor;
import com.hotel.domains.api.DayOccupations;
import com.hotel.domains.api.GuestMetadata;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.MaidDayJobMetadata;
import com.hotel.domains.api.MaidDayJobs;
import com.hotel.domains.api.MaidMetadata;
import com.hotel.domains.api.MaidStatus;
import com.hotel.domains.api.Maids;
import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.RoomMetadata;
import com.hotel.domains.api.RoomStatus;
import com.hotel.domains.api.Rooms;
import com.hotel.domains.api.SalesInterface;
import com.infrastructure.core.DomainMetadata;
import com.infrastructure.core.EntityBase;
import com.infrastructure.core.impl.PeriodNone;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Sellers;
import com.sales.domains.impl.SalesDb;
import com.securities.api.Company;
import com.securities.api.Contacts;
import com.securities.api.Feature;
import com.securities.api.FeatureSubscribed;
import com.securities.api.Features;
import com.securities.api.Indicators;
import com.securities.api.Log;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.impl.ContactNone;

public final class HotelDb extends EntityBase<Hotel, UUID> implements Hotel {

	private transient Base base;
	private transient Module origin;
	
	public HotelDb(Base base, final Module module){
		super(module.id());
		this.base = base;
		this.origin = module;
	}

	private Sales sales() throws IOException {
		Module module =  company().modulesInstalled().get(ModuleType.SALES);
		return new SalesDb(base, module);
	}
	
	@Override
	public Company company() throws IOException {
		return origin.company();
	}

	@Override
	public RoomCategories roomCategories() throws IOException {
		return new RoomCategoriesDb(this.base, this);
	}

	@Override
	public Rooms rooms() throws IOException {
		return new RoomsDb(base, this, new RoomCategoryNone(), StringUtils.EMPTY, RoomStatus.NONE);
	}

	@Override
	public List<RoomFloor> roomFloors() throws IOException {
		return ConstRoomFloor.allFloors();
	}

	@Override
	public Bookings bookings() throws IOException {
		return new BookingsDb(base, this, new RoomNone(), new PeriodNone(), null, new GuestNone(), new ContactNone());
	}

	@Override
	public Maids maids() throws IOException {
		return new MaidsDb(base, this, MaidStatus.NONE);
	}

	@Override
	public DayOccupations dayOccupations() throws IOException {
		return new DayOccupationsImpl(bookings());
	}

	@Override
	public MaidDayJobs maidDayJobs() throws IOException {
		return new MaidDayJobsDb(base, this, new MaidNone(), new PeriodNone());
	}

	@Override
	public String description() throws IOException {
		return origin.description();
	}

	@Override
	public Module install() throws IOException {
				
		// 1 - vérification des prérequis
		if(!company().modulesInstalled().contains(ModuleType.SALES))
			throw new IllegalArgumentException("Le module Ventes doit être installé avant de continuer l'action !");
		
		Module module = origin.install();
		
		// 2 - création catégorie Hôtel dans module Ventes
		salesInterface().getHotelCategory();
		
		return new HotelDb(base, module);
	}
	
	@Override
	public boolean isInstalled() {
		return origin.isInstalled();
	}

	@Override
	public boolean isSubscribed() {
		return origin.isSubscribed();
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public int order() throws IOException {
		return origin.order();
	}

	@Override
	public String shortName() throws IOException {
		return origin.shortName();
	}

	@Override
	public ModuleType type() throws IOException {
		return origin.type();
	}

	@Override
	public Module uninstall() throws IOException {
		
		UUID salesId = sales().id();
		
		PurchaseOrderMetadata poDm = PurchaseOrderMetadata.create();
		base.executeUpdate(String.format("UPDATE %s SET %s=%s WHERE %s=?", poDm.domainName(), poDm.modulePdvIdKey(), poDm.moduleIdKey(), poDm.moduleIdKey()), Arrays.asList(salesId));
		
		InvoiceMetadata invDm = InvoiceMetadata.create();
		base.executeUpdate(String.format("UPDATE %s SET %s=%s WHERE %s=?", invDm.domainName(), invDm.modulePdvIdKey(), invDm.moduleIdKey(), invDm.moduleIdKey()), Arrays.asList(salesId));
		
		PaymentMetadata payDm = PaymentMetadata.create();
		base.executeUpdate(String.format("UPDATE %s SET %s=%s WHERE %s=?", payDm.domainName(), payDm.modulePdvIdKey(), payDm.moduleIdKey(), payDm.moduleIdKey()), Arrays.asList(salesId));
		
		List<DomainMetadata> domains = 
				Arrays.asList(
					MaidDayJobMetadata.create(),
					MaidMetadata.create(),										
					BookingMetadata.create(),
					GuestMetadata.create(),
					RoomMetadata.create(),
					RoomCategoryMetadata.create()
				);		
		
		for (DomainMetadata domainMetadata : domains) {
			base.deleteAll(domainMetadata); 
		}
		
		// finaliser
		Module module = origin.uninstall();
		return new HotelDb(base, module);
	}

	@Override
	public void activate(boolean active) throws IOException {
		origin.activate(active);
	}
	
	@Override
	public boolean isActive() {
		return origin.isActive();
	}

	@Override
	public Features featuresProposed() throws IOException {
		return origin.featuresProposed();
	}

	@Override
	public Features featuresAvailable() throws IOException {
		return origin.featuresAvailable();
	}

	@Override
	public Features featuresSubscribed() throws IOException {
		return origin.featuresSubscribed();
	}

	@Override
	public Indicators indicators() throws IOException {
		return origin.indicators();
	}

	@Override
	public Module subscribe() throws IOException {
		return origin.subscribe();
	}

	@Override
	public FeatureSubscribed subscribeTo(Feature feature) throws IOException {
		return origin.subscribeTo(feature);
	}

	@Override
	public Module unsubscribe() throws IOException {
		return origin.unsubscribe();
	}

	@Override
	public void unsubscribeTo(Feature feature) throws IOException {
		origin.unsubscribeTo(feature);
	}

	@Override
	public Guests guests() throws IOException {
		return new GuestsDb(base, this);
	}

	@Override
	public Contacts contacts() throws IOException {
		return company().moduleAdmin().contacts();
	}

	@Override
	public SalesInterface salesInterface() throws IOException {
		return new SalesInterfaceImpl(base, this, sales());
	}

	@Override
	public PurchaseOrders orders() throws IOException {
		Sales sales = sales();
		ModulePdv pdv = sales.modulePdvs().get(id());
		return sales.purchases().of(pdv);
	}

	@Override
	public Sellers sellers() throws IOException {
		return sales().sellers();
	}

	@Override
	public Log log() throws IOException {
		return origin.log();
	}
}
