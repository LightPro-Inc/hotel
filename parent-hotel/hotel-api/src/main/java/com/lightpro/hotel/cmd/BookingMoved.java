package com.lightpro.hotel.cmd;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingMoved {

	private final Date newStart;
	private final Date newEnd;
	private final UUID newRoomId;
	
	public BookingMoved(){
		throw new UnsupportedOperationException("#BookingMoved()");
	}
	
	@JsonCreator
	public BookingMoved(@JsonProperty("newStart") final Date newStart,
						@JsonProperty("newEnd") final Date newEnd,
						@JsonProperty("newRoomId") final UUID newRoomId){
		
		this.newStart = newStart;
		this.newEnd = newEnd;
		this.newRoomId = newRoomId;
	}
	
	public Date newStart(){
		return this.newStart;
	}
	
	public Date newEnd(){
		return this.newEnd;
	}
	
	public UUID newRoomId(){
		return this.newRoomId;
	}
}
