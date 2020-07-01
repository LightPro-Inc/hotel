package com.hotel.domains.impl;

import java.io.IOException;

import com.hotel.domains.api.RoomFloor;
import com.infrastructure.core.EntityNone;

public final class RoomFloorNone extends EntityNone<RoomFloor, String> implements RoomFloor {

	@Override
	public String name() throws IOException {
		return "Aucun niveau";
	}
}
