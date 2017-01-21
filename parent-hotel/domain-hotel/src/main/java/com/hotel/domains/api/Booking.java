package com.hotel.domains.api;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface Booking extends Recordable<UUID> {
	
	Guest guest() throws IOException;
	Room room() throws IOException;
	Date start() throws IOException;
	Date end() throws IOException;
	BookingStatus status() throws IOException;
	double nightPriceApplied() throws IOException;
	double vatRateApplied() throws IOException;
	double htTotalBookingAmount() throws IOException;
	double vatBookingAmount() throws IOException;
	double ttcTotalBookingAmount() throws IOException;	
	double paidAmount() throws IOException;
	double remainingAmount() throws IOException;
	String naturePiece() throws IOException;
	String numeroPiece() throws IOException;
	Date deliveredDatePiece() throws IOException;
	String editionPlacePiece() throws IOException;
	String editedByPiece() throws IOException;
	int numberOfChildren() throws IOException;
	int numberOfAdults() throws IOException;
	String exactDestination() throws IOException;
	
	void pieceInfos(String naturePiece, String numeroPiece, Date deliveredDatePiece, String editionPlacePiece, String editedByPiece) throws IOException;
	void otherInfos(int numberOfChildren, int numberOfAdults, String exactDestination) throws IOException;
	void identifyGuest(UUID guestId) throws IOException;
	void move(Date newStart, Date newEnd, UUID newRoomId) throws IOException;
	void resize(Date newStart, Date newEnd) throws IOException;
	void modifyNightPrice(double newNightPricce) throws IOException;
	void confirm() throws IOException;
	void cancel() throws IOException;
	void checkIn() throws IOException;
	void checkOut() throws IOException;
	
	boolean canConfirm() throws IOException;
	boolean canCancel() throws IOException;
	boolean canCheckIn() throws IOException;
	boolean canCheckOut() throws IOException;
}
