package com.hotel.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.infrastructure.core.Recordable;

public interface RoomCategory extends Recordable<UUID, RoomCategory> {
	String name() throws IOException;
	int capacity() throws IOException;
	double nightPrice() throws IOException;	
	void update(String name, int capacity, double nightPrice) throws IOException;
	Rooms rooms() throws IOException;
	
	Queryable<Booking, UUID> bookings() throws IOException;
}
