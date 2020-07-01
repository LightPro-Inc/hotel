package com.hotel.domains.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomStatus;
import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderStatus;
import com.securities.api.Contact;
import com.securities.api.ContactNature;
import com.securities.impl.SimpleFormular;

public final class BookingDb extends GuidKeyEntityDb<Booking, BookingMetadata> implements Booking {
	
	private final Hotel module;
	
	public BookingDb(final Base base, final UUID id, final Hotel module){
		super(base, id, "Réservation introuvable !");
		this.module = module;
	}

	@Override
	public Contact guest() throws IOException {
		UUID guestid = ds.get(dm.guestIdKey());
		return module().contacts().get(guestid);
	}

	@Override
	public Room room() throws IOException {
		UUID roomid = ds.get(dm.roomIdKey());
		return new RoomDb(this.base, roomid, module);
	}

	@Override
	public LocalDateTime start() throws IOException {
		Timestamp ts = ds.get(dm.startDateKey());
		return ts.toLocalDateTime();
	}

	@Override
	public LocalDateTime end() throws IOException {
		Timestamp ts = ds.get(dm.endDateKey());
		return ts.toLocalDateTime();
	}

	@Override
	public BookingStatus status() throws IOException {
		
		BookingStatus status = getStatus();
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);		
		LocalDateTime start = start().truncatedTo(ChronoUnit.DAYS);
		LocalDateTime end = end().truncatedTo(ChronoUnit.DAYS);
		
		switch(status){
			case NEW:				
				LocalDateTime in2Days = today.plusDays(1);
				LocalDateTime dateCreated = horodate().dateCreated().truncatedTo(ChronoUnit.DAYS);
				
				if(start.isEqual(today) && today.isEqual(dateCreated)){
					status = BookingStatus.CONFIRMED;
					changeStatus(status);
				}else{
					if(start.isBefore(in2Days)){
						status = BookingStatus.EXPIRED;
						changeStatus(status);					
					}	
				}
												
				break;
			case CONFIRMED:
				LocalDateTime arrivalDeadline = today.plusHours(18);

                if (start.isBefore(today) || (start.isEqual(today) && now.isAfter(arrivalDeadline))) {
                	// Doit arriver avant 18 heures
                    status = BookingStatus.LATE_ARRIVAL;
                    changeStatus(status);
                }
                break;
			case ARRIVED:
				LocalDateTime checkoutDeadline = today.plusHours(10);

                if (end.isBefore(today) || (end.isEqual(today) && now.isAfter(checkoutDeadline))) { 
                	// doit être parti avant 10 heures
                    status = BookingStatus.LATE_CHECKOUT;
                    changeStatus(status);
                }
                break;			             
		default:			
			break;
		}
		
		return status;		
	}

	@Override
	public double nightPriceApplied() throws IOException {
		if(order().isNone())
			return 0;
		
		OrderProduct orderProduct = order().products().build(id);
		return orderProduct.unitPrice();
	}

	@Override
	public double htTotalBookingAmount() throws IOException {
		if(order().isNone())
			return 0;
		
		return order().saleAmount().totalAmountHt();
	}

	@Override
	public double taxBookingAmount() throws IOException {
		if(order().isNone())
			return 0;
		
		return order().saleAmount().totalTaxAmount();
	}

	@Override
	public double ttcTotalBookingAmount() throws IOException {
		if(order().isNone())
			return 0;
		
		return order().saleAmount().totalAmountTtc();
	}

	@Override
	public double paidAmount() throws IOException {
		return order().realPaidAmount();
	}

	@Override
	public double remainingAmount() throws IOException {
		return ttcTotalBookingAmount() - paidAmount();
	}

	@Override
	public void move(LocalDateTime newStart, LocalDateTime newEnd, UUID newRoomId) throws IOException {
		
		if(getStatus() == BookingStatus.CHECKEDOUT)
			throw new IllegalArgumentException("Cette réservation ne peut être déplacée !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), Timestamp.valueOf(newStart));	
		params.put(dm.endDateKey(), Timestamp.valueOf(newEnd));
		params.put(dm.roomIdKey(), newRoomId);
		params.put(dm.statusKey(), BookingStatus.NEW.id());
		
		ds.set(params);	
	}
	
	@Override
	public void resize(LocalDateTime newStart, LocalDateTime newEnd) throws IOException {
		
		if(getStatus() == BookingStatus.CHECKEDOUT)
			throw new IllegalArgumentException("Cette réservation ne peut être modifiée !");
		
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), Timestamp.valueOf(newStart));	
		params.put(dm.endDateKey(), Timestamp.valueOf(newEnd));
		params.put(dm.statusKey(), BookingStatus.NEW.id());
		
		ds.set(params);	
		
		module().salesInterface().updateOrderOf(this);
	}

	@Override
	public void modifyNightPrice(double newNightPricce) throws IOException {

	}

	private BookingStatus getStatus() throws IOException {
		int statusId = ds.get(dm.statusKey());
		return BookingStatus.get(statusId); 
	}
	
	private void changeStatus(BookingStatus newStatus) throws IOException {
		ds.set(dm.statusKey(), newStatus.id());
	}

	@Override
	public void confirm() throws IOException {
		
		if(!canConfirm())
			throw new IllegalArgumentException("Vous ne pouvez pas confirmer cette réservation !");
		
		changeStatus(BookingStatus.CONFIRMED);
		
		if(order().status() == PurchaseOrderStatus.CREATED)
			order().markSold(LocalDate.now(), false);
	}

	@Override
	public void cancel() throws IOException {
		
		if(!canCancel())
			throw new IllegalArgumentException("Vous ne pouvez pas annuler cette réservation !");
		
		module().orders().delete(order());
		base.domainsStore(dm).delete(this.id);
	}

	@Override
	public void checkIn() throws IOException {
		
		if(!canCheckIn())
			throw new IllegalArgumentException("Vous ne pouvez pas loger le hôte !");
		
		changeStatus(BookingStatus.ARRIVED);
		room().changeStatus(RoomStatus.CLEANUP);		
	}

	@Override
	public void checkOut() throws IOException {
		
		if(!canCheckOut())
			throw new IllegalArgumentException("Vous ne pouvez pas mettre terme au séjour !");
		
		changeStatus(BookingStatus.CHECKEDOUT);
		room().changeStatus(RoomStatus.DIRTY);
	}

	@Override
	public void identifyGuest(Contact contact) throws IOException {
		
		if(contact.isNone())
			throw new IllegalArgumentException("Personne hôte inexistante !");
		
		if(contact.nature() != ContactNature.PERSON)
			throw new IllegalArgumentException("Vous devez indiquer une personne physique !");
		
		Guests guests = module().guests();
		if(!guests.contains(contact))
			guests.add(contact);
		
		ds.set(dm.guestIdKey(), contact.id());	
	}
	
	private Hotel module() throws IOException {
		return room().category().module();
	}

	@Override
	public boolean canConfirm() throws IOException {
		BookingStatus status = getStatus();
		return (status == BookingStatus.NEW);
	}

	@Override
	public boolean canCancel() throws IOException {
		BookingStatus status = getStatus();
		return (status == BookingStatus.NEW || status == BookingStatus.CONFIRMED || status == BookingStatus.LATE_ARRIVAL || status == BookingStatus.EXPIRED);
	}

	@Override
	public boolean canCheckIn() throws IOException {
		BookingStatus status = getStatus();
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);		
		LocalDateTime start = start().truncatedTo(ChronoUnit.DAYS); 
		LocalDateTime arrivalDeadline = today.plusHours(18);

		boolean bookingIsForToday = (status == BookingStatus.NEW || status == BookingStatus.CONFIRMED) && (start.isEqual(today) && now.isBefore(arrivalDeadline));
		return ( bookingIsForToday || status == BookingStatus.LATE_ARRIVAL);
	}

	@Override
	public boolean canCheckOut() throws IOException {
		BookingStatus status = getStatus();
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);		
		LocalDateTime end = end().truncatedTo(ChronoUnit.DAYS);
		LocalDateTime checkoutDeadline = today.plusHours(10);

		boolean bookingFinishToday = status == BookingStatus.ARRIVED && (end.isEqual(today) && now.isBefore(checkoutDeadline));
        
		return (bookingFinishToday || status == BookingStatus.LATE_CHECKOUT);
	}

	@Override
	public String naturePiece() throws IOException {
		return ds.get(dm.naturePieceKey());
	}

	@Override
	public String numeroPiece() throws IOException {
		return ds.get(dm.numeroPieceKey());
	}

	@Override
	public LocalDate deliveredDatePiece() throws IOException {
		java.sql.Date date = ds.get(dm.deliveredDatePieceKey());
		
		if(date == null)
			return null;
		
		return date.toLocalDate();
	}

	@Override
	public String editionPlacePiece() throws IOException {
		return ds.get(dm.editionPlacePieceKey());
	}

	@Override
	public String editedByPiece() throws IOException {
		return ds.get(dm.editedByPieceKey());
	}

	@Override
	public int numberOfChildren() throws IOException {
		return ds.get(dm.numberOfChildrenKey());
	}

	@Override
	public int numberOfAdults() throws IOException {
		return ds.get(dm.numberOfAdultsKey());
	}

	@Override
	public String exactDestination() throws IOException {
		return ds.get(dm.exactDestinationKey());
	}

	@Override
	public void pieceInfos(String naturePiece, String numeroPiece, LocalDate deliveredDatePiece, String editionPlacePiece, String editedByPiece) throws IOException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.naturePieceKey(), naturePiece);
		params.put(dm.numeroPieceKey(), numeroPiece);	
		params.put(dm.deliveredDatePieceKey(), deliveredDatePiece == null ? null : java.sql.Date.valueOf(deliveredDatePiece));
		params.put(dm.editionPlacePieceKey(), editionPlacePiece);
		params.put(dm.editedByPieceKey(), editedByPiece);
		
		ds.set(params);	
	}

	@Override
	public void otherInfos(int numberOfChildren, int numberOfAdults, String exactDestination) throws IOException {
				
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.numberOfChildrenKey(), numberOfChildren);
		params.put(dm.numberOfAdultsKey(), numberOfAdults);	
		params.put(dm.exactDestinationKey(), exactDestination);;
		
		ds.set(params);
	}

	@Override
	public int numberOfPeople() throws IOException {
		return numberOfAdults() + numberOfChildren() + 1; // + 1 : l'hôte
	}

	@Override
	public Contact customer() throws IOException {
		UUID customerId = ds.get(dm.customerIdKey());
		return module().contacts().get(customerId);
	}

	@Override
	public void identifyCustomer(Contact contact) throws IOException {
		
		if(contact.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un client !");
		
		ds.set(dm.customerIdKey(), contact.id());	
		
		order().changeCustomer(contact);
	}

	@Override
	public PurchaseOrder order() throws IOException {
		UUID orderId = ds.get(dm.orderIdKey());
		return module().orders().build(orderId);
	}

	@Override
	public Contact seller() throws IOException {
		UUID sellerId = ds.get(dm.sellerIdKey());
		return module().contacts().get(sellerId);
	}

	@Override
	public PurchaseOrder generateOrder() throws IOException {
		return module().salesInterface().generateOrder(this);
	}

	@Override
	public void linkToOrder(PurchaseOrder order) throws IOException {
		module().salesInterface().linkOrderToBooking(order, this);
	}

	@Override
	public int numberOfDays() throws IOException {
		return java.time.Period.between(start().toLocalDate(), end().toLocalDate()).getDays();
	}

	@Override
	public double netCommercialTotalBookingAmount() throws IOException {
		if(order().isNone())
			return 0;
		
		return order().saleAmount().netCommercial();
	}

	@Override
	public double paidRate() throws IOException {
		
		double paidRate;
		if(order().isNone())
			paidRate = 0.0;
		
		paidRate = (paidAmount() / ttcTotalBookingAmount()) * 100;
		
		return new SimpleFormular("{rate}", 2, false).withParam("{rate}", paidRate).result();
	}
}
