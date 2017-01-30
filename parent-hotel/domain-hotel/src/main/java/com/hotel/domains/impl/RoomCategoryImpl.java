package com.hotel.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.Queryable;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;

public class RoomCategoryImpl implements RoomCategory {

	private final transient Base base;
	private final transient UUID id;
	private final transient RoomCategoryMetadata dm;
	private final transient DomainStore ds;
	
	public RoomCategoryImpl(final Base base, final UUID id){
		this.base = base;
		this.id = id;
		this.dm = RoomCategoryMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent() {
		try {
			return base.domainsStore(dm).exists(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public int capacity() throws IOException {
		return ds.get(dm.capacityKey());
	}

	@Override
	public double nightPrice() throws IOException {
		return ds.get(dm.nightPriceKey());
	}

	@Override
	public void update(String name, int capacity, double nightPrice)  throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (capacity == 0) {
            throw new IllegalArgumentException("Invalid capacity : capacity must be upper than zero !");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.capacityKey(), capacity);
		params.put(dm.nightPriceKey(), nightPrice);
		
		ds.set(params);
	}

	@Override
	public Rooms rooms() throws IOException {
		return new RoomsImpl(this.base, this.id);
	}

	@Override
	public Queryable<Booking, UUID> bookings() throws IOException {
		return new BookingsOfRoomCategoryImpl(this.base, this.id);
	}
	
	@Override
	public boolean isEqual(RoomCategory item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(RoomCategory item) {
		return !isEqual(item);
	}

}
