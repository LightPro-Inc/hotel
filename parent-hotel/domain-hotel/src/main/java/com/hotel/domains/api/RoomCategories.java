package com.hotel.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Queryable;

public interface RoomCategories extends Queryable<RoomCategory> {
	RoomCategory add(String name, int capacity, double nightPrice) throws IOException;
	void delete(UUID id) throws IOException;
	RoomCategory findSingle(UUID id) throws IOException;			
}
