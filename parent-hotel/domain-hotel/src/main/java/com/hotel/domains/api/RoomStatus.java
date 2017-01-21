package com.hotel.domains.api;

public enum RoomStatus {
	
	READY("Disponible"),
	DIRTY("Sale"),
	CLEANUP("Nettoyée"),
	OUTOFSERVICE("Hors service");
	
	private final String name;
	
	RoomStatus(final String name){
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
}
