package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.DayOccupation;
import com.hotel.domains.api.DayOccupations;

public class DayOccupationsImpl implements DayOccupations {

	private transient final Bookings bookings;
	
	public DayOccupationsImpl(Bookings bookings){
		this.bookings = bookings;
	}
	
	@Override
	public List<DayOccupation> of(LocalDate date) throws IOException {
		List<Booking> bks = bookings.at(date).all();
		
		return bks.stream()
				  .map(m -> build(m, date))
				  .collect(Collectors.toList());
	}
	
	private DayOccupation build(Booking booking, LocalDate date){
		return new DayOccupationImpl(booking, date);
	}
}
