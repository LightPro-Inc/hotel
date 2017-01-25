package com.lightpro.hotel.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hotel.domains.api.DayOccupation;
import com.hotel.domains.api.Guest;

public class DayOccupationVm {
	private final transient DayOccupation origin;
	
	public DayOccupationVm() {
        throw new UnsupportedOperationException("#DayOccupationVm()");
    }
	
	public DayOccupationVm(final DayOccupation origin){
		this.origin = origin;
	}
	
	@JsonGetter
	public String getRoom() throws IOException{
		return origin.room().number();
	}
	
	@JsonGetter
	public int getRoomCapacity() throws IOException{
		return origin.room().category().capacity();
	}
	
	@JsonGetter
	public UUID getRoomId() throws IOException {
		return origin.room().id();
	}
	
	@JsonGetter
	public int getNumberOfPeople() throws IOException {
		return origin.booking().numberOfPeople();
	}
	
	@JsonGetter
	public String getGuest() throws IOException {
		Guest guest = origin.booking().guest();
		return guest.isPresent() ? guest.fullName() : "";
	}
	
	@JsonGetter
	public UUID getGuestId() throws IOException {
		Guest guest = origin.booking().guest();
		return guest.isPresent() ? guest.id() : null;
	}
	
	@JsonGetter
	public LocalDate getCheckoutDay() throws IOException {
		return origin.booking().end().toLocalDate();
	}
	
	@JsonGetter
	public String getStatus() throws IOException {
		return origin.status().toString();
	}
	
	@JsonGetter
	public String getStatusId() throws IOException {
		return origin.status().name();
	}
}
