package com.hotel.domains.api;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Queryable;

public interface Bookings extends Queryable<Booking> {
	Booking book(UUID guestid, UUID roomid, Date start, Date end, double nightPriceApplied) throws IOException;
	List<Booking> between(Date start, Date end) throws IOException;
	Booking findSingle(UUID id) throws IOException;
	Guests guests() throws IOException;
}
