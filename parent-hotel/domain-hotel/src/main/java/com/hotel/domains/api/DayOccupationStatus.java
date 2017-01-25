package com.hotel.domains.api;

public enum DayOccupationStatus {
	NONE("Non d�fini",  "white"),
	CUSTOMER_ON_CHECKOUT("Client sur le d�part",  "green"),
	CUSTOMER_RECOUCHE("Client en recouche",  "red"),
	CUSTOMER_ARRIVED("Client arriv�", "black"), 	
	CUSTOMER_WAITED("Client attendu", "narrow");
	
	private final String name;
	private final String color;
	
	DayOccupationStatus(final String name, final String color){
		this.name = name;
		this.color = color;
	}
	
	public String toString(){
		return this.name;
	}
	
	public String color(){
		return this.color;
	}
}
