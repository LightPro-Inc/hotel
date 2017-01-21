package com.lightpro.hotel.vm;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hotel.domains.api.Room;

public class RoomVm {
	private final transient Room room;
	
	public RoomVm() {
        throw new UnsupportedOperationException("#RoomVm()");
    }
	
	public RoomVm(final Room room){
		this.room = room;
	}
	
	@JsonGetter
	public UUID getId() {
		return this.room.id();
	}
	
	@JsonGetter
	public String getCategory() throws IOException {
		return this.room.category().name();
	}
	
	@JsonGetter
	public UUID getCategoryId() throws IOException {
		return this.room.category().id();
	}
	
	@JsonGetter
	public Integer getCapacity() throws IOException {
		return this.room.category().capacity();
	}
	
	@JsonGetter
	public Double getNightPrice() throws IOException {
		return this.room.category().nightPrice();
	}
	
	@JsonGetter
	public String getNumber() throws IOException {
		return this.room.number();
	}
	
	@JsonGetter
	public String getFloor() throws IOException {
		return this.room.floor().name();
	}
	
	@JsonGetter
	public String getFloorId() throws IOException {
		return this.room.floor().id();
	}
	
	@JsonGetter
	public String getStatusId() throws IOException {
		return this.room.status().name();
	}
	
	@JsonGetter
	public String getStatus() throws IOException {
		return this.room.status().toString();
	}
	
	@JsonGetter
	public boolean getIsOccupied() throws IOException {
		return this.room.isOccupied();
	}
}
