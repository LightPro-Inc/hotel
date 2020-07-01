package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Guest;
import com.hotel.domains.api.Room;
import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.impl.PurchaseOrderNone;
import com.securities.api.Contact;
import com.securities.impl.ContactNone;

public final class BookingNone extends GuidKeyEntityNone<Booking> implements Booking {

	@Override
	public Guest guest() throws IOException {
		return new GuestNone();
	}

	@Override
	public Room room() throws IOException {
		return new RoomNone();
	}

	@Override
	public LocalDateTime start() throws IOException {
		return null;
	}

	@Override
	public LocalDateTime end() throws IOException {
		return null;
	}

	@Override
	public BookingStatus status() throws IOException {
		return BookingStatus.NONE;
	}

	@Override
	public double nightPriceApplied() throws IOException {		
		return 0;
	}

	@Override
	public double htTotalBookingAmount() throws IOException {		
		return 0;
	}

	@Override
	public double ttcTotalBookingAmount() throws IOException {		
		return 0;
	}

	@Override
	public double paidAmount() throws IOException {		
		return 0;
	}

	@Override
	public double remainingAmount() throws IOException {		
		return 0;
	}

	@Override
	public String naturePiece() throws IOException {	
		return null;
	}

	@Override
	public String numeroPiece() throws IOException {		
		return null;
	}

	@Override
	public LocalDate deliveredDatePiece() throws IOException {		
		return null;
	}

	@Override
	public String editionPlacePiece() throws IOException {		
		return null;
	}

	@Override
	public String editedByPiece() throws IOException {		
		return null;
	}

	@Override
	public int numberOfChildren() throws IOException {		
		return 0;
	}

	@Override
	public int numberOfAdults() throws IOException {		
		return 0;
	}

	@Override
	public int numberOfPeople() throws IOException {		
		return 0;
	}

	@Override
	public String exactDestination() throws IOException {		
		return null;
	}

	@Override
	public void pieceInfos(String naturePiece, String numeroPiece, LocalDate deliveredDatePiece,
			String editionPlacePiece, String editedByPiece) throws IOException {

	}

	@Override
	public void otherInfos(int numberOfChildren, int numberOfAdults, String exactDestination) throws IOException {

	}

	@Override
	public void identifyGuest(Contact contact) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void move(LocalDateTime newStart, LocalDateTime newEnd, UUID newRoomId) throws IOException {

	}

	@Override
	public void resize(LocalDateTime newStart, LocalDateTime newEnd) throws IOException {

	}

	@Override
	public void modifyNightPrice(double newNightPricce) throws IOException {

	}

	@Override
	public void confirm() throws IOException {

	}

	@Override
	public void cancel() throws IOException {

	}

	@Override
	public void checkIn() throws IOException {

	}

	@Override
	public void checkOut() throws IOException {

	}

	@Override
	public boolean canConfirm() throws IOException {
		return false;
	}

	@Override
	public boolean canCancel() throws IOException {
		return false;
	}

	@Override
	public boolean canCheckIn() throws IOException {
		return false;
	}

	@Override
	public boolean canCheckOut() throws IOException {
		return false;
	}

	@Override
	public Contact customer() throws IOException {
		return new ContactNone();
	}

	@Override
	public void identifyCustomer(Contact contact) throws IOException {
		
	}

	@Override
	public PurchaseOrder order() throws IOException {
		return new PurchaseOrderNone();
	}

	@Override
	public Contact seller() throws IOException {
		return new ContactNone();
	}

	@Override
	public PurchaseOrder generateOrder() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void linkToOrder(PurchaseOrder order) throws IOException {
		
	}

	@Override
	public int numberOfDays() throws IOException {
		return 0;
	}

	@Override
	public double netCommercialTotalBookingAmount() throws IOException {
		return 0;
	}

	@Override
	public double taxBookingAmount() throws IOException {
		return 0;
	}

	@Override
	public double paidRate() throws IOException {
		return 0;
	}
}
