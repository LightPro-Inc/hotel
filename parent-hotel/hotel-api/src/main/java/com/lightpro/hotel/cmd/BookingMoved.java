package com.lightpro.hotel.cmd;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingMoved {

	private final LocalDateTime newStart;
	private final LocalDateTime newEnd;
	private final UUID newRoomId;
	
	public BookingMoved(){
		throw new UnsupportedOperationException("#BookingMoved()");
	}
	
	@JsonCreator
	public BookingMoved(@JsonProperty("newStart") final LocalDateTime newStart,
						@JsonProperty("newEnd") final LocalDateTime newEnd,
						@JsonProperty("newRoomId") final UUID newRoomId){
		
		this.newStart = newStart;
		this.newEnd = newEnd;
		this.newRoomId = newRoomId;
	}
	
	public LocalDateTime newStart(){
		return this.newStart;
	}
	
	public LocalDateTime newEnd(){
		return this.newEnd;
	}
	
	public UUID newRoomId(){
		return this.newRoomId;
	}
}
