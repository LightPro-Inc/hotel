package com.lightpro.hotel.cmd;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingPeriod {

	private final LocalDate start;
	private final LocalDate end;
	
	public BookingPeriod(){
		throw new UnsupportedOperationException("#BookingPeriod()");
	}
	
	@JsonCreator
	public BookingPeriod(@JsonProperty("start") final LocalDate start, @JsonProperty("end") final LocalDate end){
		
		this.start = start;
		this.end = end;
	}
	
	public LocalDate start(){
		return this.start;
	}
	
	public LocalDate end(){
		return this.end;
	}
}
