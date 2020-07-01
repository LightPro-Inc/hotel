package com.lightpro.hotel.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomEdit {
		
	private final transient String number;
	private final transient String floorId;
	private final transient UUID categoryId;
	
	public RoomEdit(){
		throw new UnsupportedOperationException("#RoomEdit()");
	}
	
	@JsonCreator
	public RoomEdit(@JsonProperty("categoryId") UUID categoryId, @JsonProperty("number") String number, @JsonProperty("floorId") String floorId) {
		this.number = number;
		this.floorId = floorId;
		this.categoryId = categoryId;
	}
	
	public String number(){
		return this.number;
	}
	
	public String floorId(){
		return this.floorId;
	}
	
	public UUID categoryId(){
		return this.categoryId;
	}
}
