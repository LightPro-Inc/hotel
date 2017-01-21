package com.lightpro.hotel.vm;

import com.fasterxml.jackson.annotation.JsonGetter;

public class LocationStat {
	
	private final transient String roomCategory;
	private final transient double amount;
	
	public LocationStat(String roomCategory, double amount) {
		this.roomCategory = roomCategory;
		this.amount = amount;
	}
	
	@JsonGetter
	public String getRoomCategory() {
		return roomCategory;
	}
	
	@JsonGetter
	public double getAmount(){
		return amount;
	}
}
