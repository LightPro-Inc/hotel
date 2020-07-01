package com.lightpro.hotel.vm;

import java.io.IOException;
import java.util.UUID;

import com.hotel.domains.api.Room;

public final class RoomVm {
	
	public final UUID id;
	public final String category;
	public final UUID categoryId;
	public final Integer capacity;
	public final Double nightPrice;;
	public final String number;
	public final String floor;
	public final String floorId;
	public final String statusId;
	public final String status;
	public final boolean isOccupied;
	
	public RoomVm() {
        throw new UnsupportedOperationException("#RoomVm()");
    }
	
	public RoomVm(final Room origin){
		try {
			this.id = origin.id();
			this.category = origin.category().name();
	        this.categoryId = origin.category().id();
	        this.capacity = origin.category().capacity();
	        this.nightPrice = origin.category().nightPrice();
	        this.number = origin.number();
	        this.floor = origin.floor().name();
	        this.floorId = origin.floor().id();
	        this.statusId = origin.status().name();
	        this.status = origin.status().toString();
	        this.isOccupied = origin.isOccupied();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
