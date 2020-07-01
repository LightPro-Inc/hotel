package com.lightpro.hotel.cmd;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingResized {
	private final LocalDateTime newStart;
	private final LocalDateTime newEnd;
	
	public BookingResized(){
		throw new UnsupportedOperationException("#BookingResized()");
	}
	
	@JsonCreator
	public BookingResized(@JsonProperty("newStart") final LocalDateTime newStart,
						  @JsonProperty("newEnd") final LocalDateTime newEnd){
		
		this.newStart = newStart;
		this.newEnd = newEnd;
	}
	
	public LocalDateTime newStart(){
		return this.newStart;
	}
	
	public LocalDateTime newEnd(){
		return this.newEnd;
	}
}
