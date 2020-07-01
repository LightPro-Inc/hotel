package com.hotel.domains.api;

import java.io.IOException;
import java.util.List;

import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Sellers;
import com.securities.api.Company;
import com.securities.api.Contacts;
import com.securities.api.Module;

public interface Hotel extends Module {
	
	Company company() throws IOException;		
	RoomCategories roomCategories() throws IOException;
	Rooms rooms() throws IOException;	
	List<RoomFloor> roomFloors() throws IOException;
	Bookings bookings() throws IOException;
	DayOccupations dayOccupations() throws IOException;
	Maids maids() throws IOException;
	MaidDayJobs maidDayJobs() throws IOException;
	Guests guests() throws IOException;
	Contacts contacts() throws IOException;
	Sellers sellers() throws IOException;
	PurchaseOrders orders() throws IOException;
	
	SalesInterface salesInterface() throws IOException;
}
