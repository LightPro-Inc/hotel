package com.lightpro.hotel.cmd;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DayOccupationCmd {
	
	private final LocalDate day;
	
	public DayOccupationCmd(){
		throw new UnsupportedOperationException("#DayOccupationCmd()");
	}
	
	@JsonCreator
	public DayOccupationCmd(@JsonProperty("day") final LocalDate day){		
		this.day = day;
	}
	
	public LocalDate day(){
		return this.day;
	}
}
