package com.lightpro.hotel.vm;

import java.io.IOException;
import java.util.UUID;

import com.hotel.domains.api.RoomCategory;

final public class RoomCategoryVm {
	
	public final UUID id;
	public final String name;
	public final Integer capacity;
	public final Double nightPrice;
	public final long numberOfRooms;
	
	public RoomCategoryVm() {
        throw new UnsupportedOperationException("#RoomCategoryVm()");
    }
	
	public RoomCategoryVm(final RoomCategory origin){
		try {
			this.id = origin.id();
			this.name = origin.name();
	        this.capacity = origin.capacity();
	        this.nightPrice = origin.nightPrice();
	        this.numberOfRooms = origin.rooms().count();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
