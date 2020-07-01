package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.sales.domains.api.PurchaseOrder;
import com.securities.api.Contact;

public interface Booking extends Nonable {
	UUID id();
	Contact guest() throws IOException;
	Contact customer() throws IOException;
	Contact seller() throws IOException;
	Room room() throws IOException;
	LocalDateTime start() throws IOException;
	LocalDateTime end() throws IOException;
	BookingStatus status() throws IOException;
	double nightPriceApplied() throws IOException;
	double htTotalBookingAmount() throws IOException;
	double netCommercialTotalBookingAmount() throws IOException;
	double taxBookingAmount() throws IOException;
	double ttcTotalBookingAmount() throws IOException;	
	double paidAmount() throws IOException;
	double paidRate() throws IOException;
	double remainingAmount() throws IOException;
	String naturePiece() throws IOException;
	String numeroPiece() throws IOException;
	LocalDate deliveredDatePiece() throws IOException;
	String editionPlacePiece() throws IOException;
	String editedByPiece() throws IOException;
	int numberOfDays() throws IOException;
	int numberOfChildren() throws IOException;
	int numberOfAdults() throws IOException;
	int numberOfPeople() throws IOException;
	String exactDestination() throws IOException;
	PurchaseOrder order() throws IOException;
	
	void pieceInfos(String naturePiece, String numeroPiece, LocalDate deliveredDatePiece, String editionPlacePiece, String editedByPiece) throws IOException;
	void otherInfos(int numberOfChildren, int numberOfAdults, String exactDestination) throws IOException;
	void identifyGuest(Contact contact) throws IOException;
	void identifyCustomer(Contact contact) throws IOException;
	void move(LocalDateTime newStart, LocalDateTime newEnd, UUID newRoomId) throws IOException;
	void resize(LocalDateTime newStart, LocalDateTime newEnd) throws IOException;
	void modifyNightPrice(double newNightPricce) throws IOException;
	void confirm() throws IOException;
	void cancel() throws IOException;
	void checkIn() throws IOException;
	void checkOut() throws IOException;
	PurchaseOrder generateOrder() throws IOException;
	void linkToOrder(PurchaseOrder order) throws IOException;
	
	boolean canConfirm() throws IOException;
	boolean canCancel() throws IOException;
	boolean canCheckIn() throws IOException;
	boolean canCheckOut() throws IOException;
}
