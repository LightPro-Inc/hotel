package com.hotel.domains.api;

import java.io.IOException;
import java.util.List;

import com.securities.api.Company;

public interface Hotel {
	Company company() throws IOException;		
	RoomCategories roomCategories() throws IOException;
	Rooms allRooms() throws IOException;	
	List<RoomFloor> allRoomFloors() throws IOException;
	Bookings bookings() throws IOException;
	Maids maids() throws IOException;
}
