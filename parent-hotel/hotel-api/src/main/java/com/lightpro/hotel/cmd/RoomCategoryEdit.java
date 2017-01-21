package com.lightpro.hotel.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomCategoryEdit {
	
	private final String name;
	private final int capacity;
	private final double nightPrice;
	
	public RoomCategoryEdit(){
		throw new UnsupportedOperationException("#RegisterRoomCategoryCmd()");
	}
	
	@JsonCreator
	public RoomCategoryEdit(@JsonProperty("name") final String name, 
						  @JsonProperty("capacity") final int capacity,
						  @JsonProperty("nightPrice") final double nightPrice){
		
		this.name = name;
		this.capacity = capacity;
		this.nightPrice = nightPrice;
	}
	
	public String name(){
		return this.name;
	}
	
	public int capacity(){
		return this.capacity;
	}
	
	public double nightPrice(){
		return this.nightPrice;
	}
}
