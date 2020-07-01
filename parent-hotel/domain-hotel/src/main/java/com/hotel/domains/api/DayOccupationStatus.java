package com.hotel.domains.api;

public enum DayOccupationStatus {
	NONE(0, "Non défini",  "white"),
	CUSTOMER_ON_CHECKOUT(1, "Client sur le départ",  "green"),
	CUSTOMER_RECOUCHE(2, "Client en recouche",  "red"),
	CUSTOMER_ARRIVED(3, "Client arrivé", "black"), 	
	CUSTOMER_WAITED(4, "Client attendu", "narrow");
	
	private final int id;
	private final String name;
	private final String color;
	
	DayOccupationStatus(final int id, final String name, final String color){
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public static DayOccupationStatus get(int id){
		
		DayOccupationStatus value = DayOccupationStatus.NONE;
		for (DayOccupationStatus item : DayOccupationStatus.values()) {
			if(item.id() == id)
				value = item;
		}
		
		return value;
	}

	public int id(){
		return id;
	}
	
	public String toString(){
		return this.name;
	}
	
	public String color(){
		return this.color;
	}
}
