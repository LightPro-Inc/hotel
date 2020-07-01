package com.lightpro.hotel.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.domains.api.DayOccupation;

public final class DayOccupationVm {
	
	public final String room;
	public final int roomCapacity;
	public final UUID roomId;
	public final int numberOfPeople;
	public final String guest;
	public final UUID guestId;
	public final LocalDate checkoutDay;
	public final String status;
	public final String statusId;
	
	public DayOccupationVm() {
        throw new UnsupportedOperationException("#DayOccupationVm()");
    }
	
	public DayOccupationVm(final DayOccupation origin){
		try {
			this.room = origin.room().number();
	        this.roomCapacity = origin.room().category().capacity();
	        this.roomId = origin.room().id();
	        this.numberOfPeople = origin.booking().numberOfPeople();
	        this.guest = origin.booking().guest().name();
	        this.guestId = origin.booking().guest().id();
	        this.checkoutDay = origin.booking().end().toLocalDate();
	        this.status = origin.status().toString();
	        this.statusId = origin.status().name();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
}
