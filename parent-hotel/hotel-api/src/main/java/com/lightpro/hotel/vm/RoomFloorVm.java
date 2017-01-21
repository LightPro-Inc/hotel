package com.lightpro.hotel.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hotel.domains.api.RoomFloor;

public class RoomFloorVm {
	
	private final transient RoomFloor roomFloor;
	
	public RoomFloorVm(){
		throw new UnsupportedOperationException("#RoomFloorVm()");
	}
	
	public RoomFloorVm(RoomFloor roomFloor){
		this.roomFloor = roomFloor;
	}
	
	@JsonGetter
	public String getId(){
		return this.roomFloor.id();
	}
	
	@JsonGetter
	public String getName() throws IOException {
		return this.roomFloor.name();
	}
}
