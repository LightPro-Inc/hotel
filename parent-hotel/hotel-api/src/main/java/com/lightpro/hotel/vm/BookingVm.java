package com.lightpro.hotel.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.hotel.domains.api.Booking;

public final class BookingVm {
	
	public final UUID id;
	public final UUID guestId;
	public final String guest;
	public final UUID customerId;
	public final String customer;
	public final UUID orderId;
	public final String order;
	public final UUID roomId;
	public final String room;
	public final LocalDateTime start;
	public final LocalDateTime end;
	public final String status;
	public final String statusId;
	public final String statusColor;
	public final double nightPriceApplied;
	public final double ttcTotalBookingAmount;
	public final double paidAmount;
	public final double remainingAmount;
	public final boolean canConfirm;
	public final boolean canCancel;
	public final boolean canCheckIn;
	public final boolean canCheckOut;
	public final String naturePiece;
	public final String numeroPiece;
	public final LocalDate deliveredDatePiece;
	public final String editionPlacePiece;
	public final String editedByPiece;
	public final int numberOfChildren;
	public final int numberOfAdults;
	public final String exactDestination;
	public final double paidRate;
	
	public BookingVm() {
        throw new UnsupportedOperationException("#BookingVm()");
    }
	
	public BookingVm(final Booking origin){
		try {
			this.id = origin.id();
			this.guestId = origin.guest().id();
			this.guest = origin.guest().name();
			this.customerId = origin.customer().id();
			this.customer = origin.customer().name();
			this.orderId = origin.order().id();
			this.order = origin.order().reference();
			this.roomId = origin.room().id();
			this.room = origin.room().number();
			this.start = origin.start();
			this.end = origin.end();
			this.status = origin.status().toString();
			this.statusId = origin.status().name();
			this.statusColor = origin.status().color();
			this.nightPriceApplied = origin.nightPriceApplied();
			this.ttcTotalBookingAmount = origin.ttcTotalBookingAmount();
			this.paidAmount = origin.paidAmount();
			this.remainingAmount = origin.remainingAmount();
			this.canConfirm = origin.canConfirm();
			this.canCancel = origin.canCancel();
			this.canCheckIn = origin.canCheckIn();
			this.canCheckOut = origin.canCheckOut();
			this.naturePiece = origin.naturePiece();
			this.numeroPiece = origin.numeroPiece();
			this.deliveredDatePiece = origin.deliveredDatePiece();
			this.editionPlacePiece = origin.editionPlacePiece();
			this.editedByPiece = origin.editedByPiece();
			this.numberOfChildren = origin.numberOfChildren();
			this.numberOfAdults = origin.numberOfAdults();
			this.exactDestination = origin.exactDestination();
			this.paidRate = origin.paidRate();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
}
