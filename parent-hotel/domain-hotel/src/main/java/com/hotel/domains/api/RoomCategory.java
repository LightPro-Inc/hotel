package com.hotel.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Nonable;

public interface RoomCategory extends Nonable {
	UUID id();
	String name() throws IOException;
	int capacity() throws IOException;
	double nightPrice() throws IOException;	
	void update(String name, int capacity, double nightPrice) throws IOException;
	Rooms rooms() throws IOException;
	Hotel module() throws IOException;
}
