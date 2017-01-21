package com.hotel.domains.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.ConstRoomFloor;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Maids;
import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.Rooms;
import com.infrastructure.pgsql.PgBase;
import com.securities.api.Company;
import com.securities.impl.CompanyImpl;

public class HotelImpl implements Hotel {

	private transient PgBase base;
	private transient UUID companyid = UUID.fromString("c155b7df-f18b-47bd-ba49-cb525f7efaa2");
	
	public HotelImpl(PgBase base){
		this.base = base;
	}

	@Override
	public Company company() throws IOException {
		return new CompanyImpl(this.base, this.companyid);
	}

	@Override
	public RoomCategories roomCategories() throws IOException {
		return new RoomCategoriesImpl(this.base);
	}

	@Override
	public Rooms allRooms() throws IOException {
		return new AllRoomsImpl(this.base);
	}

	@Override
	public List<RoomFloor> allRoomFloors() throws IOException {
		return ConstRoomFloor.allFloors();
	}

	@Override
	public Bookings bookings() throws IOException {
		return new BookingsImpl(this.base);
	}

	@Override
	public Maids maids() throws IOException {
		return new MaidsImpl(base);
	}
}
