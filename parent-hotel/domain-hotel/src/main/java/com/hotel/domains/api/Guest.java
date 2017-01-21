package com.hotel.domains.api;

import java.io.IOException;

import com.infrastructure.core.Queryable;
import com.securities.api.Person;

public interface Guest extends Person {
	Queryable<Booking> bookings() throws IOException;
}
