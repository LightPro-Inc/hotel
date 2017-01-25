package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.DayOccupation;
import com.hotel.domains.api.DayOccupationStatus;
import com.hotel.domains.api.Room;

public class DayOccupationImpl implements DayOccupation {

	private final transient Booking booking;
	private final transient LocalDate date;
	
	public DayOccupationImpl(final Booking booking, final LocalDate date){
		this.booking = booking;
		this.date = date;
	}
	
	@Override
	public Room room() throws IOException {
		return this.booking.room();
	}

	@Override
	public Booking booking() throws IOException {
		return this.booking;
	}

	@Override
	public DayOccupationStatus status() throws IOException {
		
		DayOccupationStatus status = DayOccupationStatus.NONE;
		
		LocalDate startDay = booking.start().toLocalDate();
		LocalDate endDay = booking.end().toLocalDate();
		
		if(booking.room().isOccupied()){
			if(startDay.isEqual(date)){
				status = DayOccupationStatus.CUSTOMER_ARRIVED;
			}else if(endDay.isEqual(date)){
				status = DayOccupationStatus.CUSTOMER_ON_CHECKOUT;
			}else if(startDay.isBefore(date) && endDay.isAfter(date)){
				status = DayOccupationStatus.CUSTOMER_RECOUCHE;
			}
		}else if(booking.room().isReserved()){
			status = DayOccupationStatus.CUSTOMER_WAITED;
		}
		
		return status;
	}
}
