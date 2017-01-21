package com.lightpro.hotel.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotel.domains.api.BookingStatus;

public class BookingStatusEdit {

	private final BookingStatus status;
	
	public BookingStatusEdit(){
		throw new UnsupportedOperationException("#BookingStatusEdit()");
	}
	
	@JsonCreator
	public BookingStatusEdit(@JsonProperty("status") final BookingStatus status) {
		
		this.status = status;
	}
	
	public BookingStatus status(){
		return this.status;
	}
}
