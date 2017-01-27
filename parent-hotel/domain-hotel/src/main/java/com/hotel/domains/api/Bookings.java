package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;

public interface Bookings extends AdvancedQueryable<Booking>, Updatable<Booking> {
	Booking book(UUID guestid, UUID roomid, LocalDateTime start, LocalDateTime end, double nightPriceApplied) throws IOException;
	List<Booking> at(LocalDate date) throws IOException;
	List<Booking> between(LocalDate start, LocalDate end) throws IOException;	
	Guests guests() throws IOException;
	
	double monthOccupationRate(LocalDate date) throws IOException;
	double weekWorkDayOccupationRate(LocalDate date) throws IOException;
	double weekendOccupationRate(LocalDate date) throws IOException;
}
