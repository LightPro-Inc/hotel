package com.hotel.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;

/**
 * 
 * @author oob
 *
 */
public interface Rooms extends AdvancedQueryable<Room, UUID> {
	Room add(String number, String floor) throws IOException;
	Room get(String number) throws IOException;
	
	Rooms of(RoomCategory category) throws IOException;
	Rooms withNumber(String number) throws IOException;
	Rooms withStatus(RoomStatus status) throws IOException;
}
