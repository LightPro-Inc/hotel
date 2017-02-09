package com.hotel.domains.api;

import java.io.IOException;
import java.util.List;

import com.securities.api.Company;
import com.securities.api.Module;

public interface Hotel extends Module {
	Company company() throws IOException;		
	RoomCategories roomCategories() throws IOException;
	Rooms allRooms() throws IOException;	
	List<RoomFloor> allRoomFloors() throws IOException;
	Bookings bookings() throws IOException;
	DayOccupations dayOccupations() throws IOException;
	Maids maids() throws IOException;
	MaidDayJobs maidDayJobs() throws IOException;
}
