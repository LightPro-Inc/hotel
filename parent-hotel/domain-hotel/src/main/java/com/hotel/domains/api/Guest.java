package com.hotel.domains.api;

import java.io.IOException;

import com.securities.api.Contact;

public interface Guest extends Contact {
	Hotel hotel() throws IOException;
	Bookings bookings() throws IOException;
}
