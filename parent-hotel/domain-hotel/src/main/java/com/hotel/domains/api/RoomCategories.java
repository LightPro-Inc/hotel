package com.hotel.domains.api;

import java.io.IOException;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;

public interface RoomCategories extends AdvancedQueryable<RoomCategory>, Updatable<RoomCategory> {
	RoomCategory add(String name, int capacity, double nightPrice) throws IOException;			
}
