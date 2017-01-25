package com.lightpro.hotel.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hotel.domains.api.Booking;
import com.hotel.domains.api.Guest;

public class BookingVm {
	private final transient Booking booking;
	
	public BookingVm() {
        throw new UnsupportedOperationException("#BookingVm()");
    }
	
	public BookingVm(final Booking booking){
		this.booking = booking;
	}
	
	@JsonGetter
	public UUID getId() {
		return this.booking.id();
	}
	
	@JsonGetter
	public UUID getGuestId() throws IOException{
		return this.booking.guest().id();
	}
	
	@JsonGetter
	public String getGuest() throws IOException{
		Guest guest = this.booking.guest();
		return guest.isPresent() ? guest.fullName() : "Non identifie";
	}
	
	@JsonGetter
	public UUID getRoomId() throws IOException {
		return this.booking.room().id();
	}
	
	@JsonGetter
	public String getRoom() throws IOException {
		return this.booking.room().number();
	}
	
	@JsonGetter
	public LocalDateTime getStart() throws IOException{
		return this.booking.start();
	}
	
	@JsonGetter
	public LocalDateTime getEnd() throws IOException{
		return this.booking.end();
	}
	
	@JsonGetter
	public String getStatus() throws IOException{
		return this.booking.status().toString();
	}
	
	@JsonGetter
	public String getStatusId() throws IOException{
		return this.booking.status().name();
	}
	
	@JsonGetter
	public String getStatusColor() throws IOException{
		return this.booking.status().color();
	}
	
	@JsonGetter
	public double getNightPriceApplied() throws IOException{
		return this.booking.nightPriceApplied();
	}
	
	@JsonGetter
	public double getTtcTotalBookingAmount() throws IOException{
		return this.booking.ttcTotalBookingAmount();
	}

	@JsonGetter
	public double getPaidAmount() throws IOException{
		return this.booking.paidAmount();
	}
	
	@JsonGetter
	public double getRemainingAmount() throws IOException{
		return this.booking.remainingAmount();
	}
	
	@JsonGetter
	public boolean getCanConfirm() throws IOException{
		return this.booking.canConfirm();
	}
	
	@JsonGetter
	public boolean getCanCancel() throws IOException{
		return this.booking.canCancel();
	}
	
	@JsonGetter
	public boolean getCanCheckIn() throws IOException{
		return this.booking.canCheckIn();
	}
	
	@JsonGetter
	public boolean getCanCheckOut() throws IOException{
		return this.booking.canCheckOut();
	}

	@JsonGetter
	public String getNaturePiece() throws IOException{
		return this.booking.naturePiece();
	}
	
	@JsonGetter
	public String getNumeroPiece() throws IOException{
		return this.booking.numeroPiece();
	}
	
	@JsonGetter
	public LocalDate getDeliveredDatePiece() throws IOException{
		return this.booking.deliveredDatePiece();
	}
	
	@JsonGetter
	public String getEditionPlacePiece() throws IOException{
		return this.booking.editionPlacePiece();
	}
	
	@JsonGetter
	public String getEditedByPiece() throws IOException{
		return this.booking.editedByPiece();
	}
	
	@JsonGetter
	public int numberOfChildren() throws IOException{
		return this.booking.numberOfChildren();
	}
	
	@JsonGetter
	public int getNumberOfAdults() throws IOException{
		return this.booking.numberOfAdults();
	}
	
	@JsonGetter
	public String getExactDestination() throws IOException{
		return this.booking.exactDestination();
	}
}
