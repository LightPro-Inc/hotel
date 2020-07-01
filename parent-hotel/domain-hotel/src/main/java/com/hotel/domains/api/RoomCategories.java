package com.hotel.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;

public interface RoomCategories extends AdvancedQueryable<RoomCategory, UUID> {
	RoomCategory add(String name, int capacity, double nightPrice) throws IOException;			
}
