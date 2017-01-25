package com.hotel.domains.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Guest;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomStatus;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;

public class BookingImpl implements Booking {

	private final transient Base base;
	private final transient Object id;
	private final transient BookingMetadata dm;
	private final transient DomainStore ds;
	
	public BookingImpl(final Base base, final Object id){
		this.base = base;
		this.id = id;
		this.dm = BookingMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return UUIDConvert.fromObject(this.id);
	}

	@Override
	public boolean isPresent() throws IOException {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public Guest guest() throws IOException {
		UUID guestid = ds.get(dm.guestIdKey());
		return new GuestImpl(this.base, guestid);
	}

	@Override
	public Room room() throws IOException {
		UUID roomid = ds.get(dm.roomIdKey());
		return new RoomImpl(this.base, roomid);
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
				LocalDateTime dateCreated = LocalDateTime.ofInstant(horodate().dateCreated().toInstant(), ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
				
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
		return ds.get(dm.nightPriceAppliedKey());
	}

	@Override
	public double vatRateApplied() throws IOException {
		return ds.get(dm.vatRateAppliedKey());
	}

	@Override
	public double htTotalBookingAmount() throws IOException {
		return ttcTotalBookingAmount() - vatBookingAmount();
	}

	@Override
	public double vatBookingAmount() throws IOException {
		return ds.get(dm.vatBookingAmountKey());
	}

	@Override
	public double ttcTotalBookingAmount() throws IOException {
		return ds.get(dm.ttcTotalBookingAmountKey());
	}

	@Override
	public double paidAmount() throws IOException {
		return ds.get(dm.paidAmountKey());
	}

	@Override
	public double remainingAmount() throws IOException {
		return ttcTotalBookingAmount() - paidAmount();
	}

	@Override
	public void move(LocalDateTime newStart, LocalDateTime newEnd, UUID newRoomId) throws IOException {
		
		if(getStatus() == BookingStatus.CHECKEDOUT)
			throw new IllegalArgumentException("Cette réservation ne peut être déplacée !");
		
    	long numberOfDays = ChronoUnit.DAYS.between(newStart, newEnd);
    	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), Timestamp.valueOf(newStart));	
		params.put(dm.endDateKey(), Timestamp.valueOf(newEnd));
		params.put(dm.roomIdKey(), newRoomId);
		params.put(dm.statusKey(), BookingStatus.NEW.name());
		params.put(dm.ttcTotalBookingAmountKey(), numberOfDays * nightPriceApplied());
		params.put(dm.vatBookingAmountKey(), 0.18 * numberOfDays * nightPriceApplied());
		
		ds.set(params);	
	}
	
	@Override
	public void resize(LocalDateTime newStart, LocalDateTime newEnd) throws IOException {
		
		if(getStatus() == BookingStatus.CHECKEDOUT)
			throw new IllegalArgumentException("Cette réservation ne peut être modifiée !");
		
    	long numberOfDays = ChronoUnit.DAYS.between(newStart, newEnd);
    	
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), Timestamp.valueOf(newStart));	
		params.put(dm.endDateKey(), Timestamp.valueOf(newEnd));
		params.put(dm.statusKey(), BookingStatus.NEW.name());
		params.put(dm.ttcTotalBookingAmountKey(), numberOfDays * nightPriceApplied());
		params.put(dm.vatBookingAmountKey(), 0.18 * numberOfDays * nightPriceApplied());
		
		ds.set(params);	
	}

	@Override
	public void modifyNightPrice(double newNightPricce) throws IOException {
		// TODO Auto-generated method stub

	}

	private BookingStatus getStatus() throws IOException {
		String statutStr = ds.get(dm.statusKey());
		return BookingStatus.valueOf(statutStr); 
	}
	
	private void changeStatus(BookingStatus newStatus) throws IOException {
		ds.set(dm.statusKey(), newStatus.name());
	}

	@Override
	public void confirm() throws IOException {
		
		if(!canConfirm())
			throw new IllegalArgumentException("Vous ne pouvez pas confirmer cette réservation !");
		
		changeStatus(BookingStatus.CONFIRMED);
	}

	@Override
	public void cancel() throws IOException {
		
		if(!canCancel())
			throw new IllegalArgumentException("Vous ne pouvez pas annuler cette réservation !");
		
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
	public void identifyGuest(UUID guestId) throws IOException {
		
		if(guest().id() == guestId)
			return;
		
		ds.set(dm.guestIdKey(), guestId);		
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
}
