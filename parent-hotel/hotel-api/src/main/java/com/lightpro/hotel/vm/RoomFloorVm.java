package com.lightpro.hotel.vm;

import java.io.IOException;

import com.hotel.domains.api.RoomFloor;

public final class RoomFloorVm {
	
	public final String id;
	public final String name;
	
	public RoomFloorVm(){
		throw new UnsupportedOperationException("#RoomFloorVm()");
	}
	
	public RoomFloorVm(RoomFloor roomFloor){
		try {
			this.id = roomFloor.id();
			this.name = roomFloor.name();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
}
