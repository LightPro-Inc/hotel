package com.hotel.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;

public final class RoomCategoryDb extends GuidKeyEntityDb<RoomCategory, RoomCategoryMetadata> implements RoomCategory {
	
	private final Hotel module;
	
	public RoomCategoryDb(final Base base, final UUID id, final Hotel module){
		super(base, id, "Catégorie de chambre introuvable !");	
		this.module = module;
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
		
		module().salesInterface().syncProduct(this);
	}

	@Override
	public Rooms rooms() throws IOException {
		return module().rooms().of(this);
	}

	@Override
	public Hotel module() throws IOException {
		return module;
	}
}
