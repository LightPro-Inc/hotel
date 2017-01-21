package com.lightpro.hotel.cmd;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingResized {
	private final Date newStart;
	private final Date newEnd;
	
	public BookingResized(){
		throw new UnsupportedOperationException("#BookingResized()");
	}
	
	@JsonCreator
	public BookingResized(@JsonProperty("newStart") final Date newStart,
						  @JsonProperty("newEnd") final Date newEnd){
		
		this.newStart = newStart;
		this.newEnd = newEnd;
	}
	
	public Date newStart(){
		return this.newStart;
	}
	
	public Date newEnd(){
		return this.newEnd;
	}
}
