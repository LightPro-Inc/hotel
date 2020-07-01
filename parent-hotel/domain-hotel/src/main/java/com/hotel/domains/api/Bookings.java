package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.infrastructure.core.GuidKeyAdvancedQueryable;
import com.securities.api.Contact;

public interface Bookings extends GuidKeyAdvancedQueryable<Booking> {
	
	Booking book(Contact customer, Contact guest, LocalDateTime start, LocalDateTime end, Contact seller) throws IOException;
		
	double monthOccupationRate(LocalDate date) throws IOException;
	double weekWorkDayOccupationRate(LocalDate date) throws IOException;
	double weekendOccupationRate(LocalDate date) throws IOException;
	
	Bookings at(LocalDate date) throws IOException;
	Bookings between(LocalDate start, LocalDate end) throws IOException;
	Bookings of(Room room) throws IOException;
	Bookings ofGuest(Guest guest) throws IOException;
	Bookings ofCustomer(Contact customer) throws IOException;
}
