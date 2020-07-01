package com.hotel.domains.impl;

import java.io.IOException;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.GuidKeyEntityNone;

public final class RoomCategoryNone extends GuidKeyEntityNone<RoomCategory> implements RoomCategory {

	@Override
	public String name() throws IOException {
		return null;
	}

	@Override
	public int capacity() throws IOException {
		return 0;
	}

	@Override
	public double nightPrice() throws IOException {
		return 0;
	}

	@Override
	public void update(String name, int capacity, double nightPrice) throws IOException {

	}

	@Override
	public Rooms rooms() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Hotel module() throws IOException {
		return new HotelNone();
	}
}
