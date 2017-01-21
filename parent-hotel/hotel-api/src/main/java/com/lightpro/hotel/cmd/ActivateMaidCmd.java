package com.lightpro.hotel.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivateMaidCmd {
	private final boolean active;
	
	public ActivateMaidCmd(){
		throw new UnsupportedOperationException("#ActivateMaidCmd()");
	}
	
	@JsonCreator
	public ActivateMaidCmd(@JsonProperty("active") final boolean active){
		
		this.active = active;
	}
	
	public boolean active(){
		return this.active;
	}
}
