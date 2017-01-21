package com.lightpro.hotel.cmd;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingPeriod {

	private final Date start;
	private final Date end;;
	
	public BookingPeriod(){
		throw new UnsupportedOperationException("#BookingPeriod()");
	}
	
	@JsonCreator
	public BookingPeriod(@JsonProperty("start") final Date start, @JsonProperty("end") final Date end){
		
		this.start = start;
		this.end = end;
	}
	
	public Date start(){
		return this.start;
	}
	
	public Date end(){
		return this.end;
	}
}
