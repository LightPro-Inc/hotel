package com.hotel.domains.api;

public enum DayOccupationStatus {
	NONE("Non défini",  "white"),
	CUSTOMER_ON_CHECKOUT("Client sur le départ",  "green"),
	CUSTOMER_RECOUCHE("Client en recouche",  "red"),
	CUSTOMER_ARRIVED("Client arrivé", "black"), 	
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
