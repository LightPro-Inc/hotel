package com.lightpro.hotel.vm;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hotel.domains.api.RoomCategory;

final public class RoomCategoryVm {
	
	private final transient RoomCategory roomCategory;
	
	public RoomCategoryVm() {
        throw new UnsupportedOperationException("#RoomCategoryVm()");
    }
	
	public RoomCategoryVm(final RoomCategory roomCategory){
		this.roomCategory = roomCategory;
	}
	
	@JsonGetter
	public UUID getId() {
		return this.roomCategory.id();
	}
	
	@JsonGetter
	public String getName() throws IOException {
		return this.roomCategory.name();
	}
	
	@JsonGetter
	public Integer getCapacity() throws IOException {
		return this.roomCategory.capacity();
	}
	
	@JsonGetter
	public Double getNightPrice() throws IOException {
		return this.roomCategory.nightPrice();
	}
	
	@JsonGetter
	public int getNumberOfRooms() throws IOException {
		return this.roomCategory.rooms().size();
	}
}
