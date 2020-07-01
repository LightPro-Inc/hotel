package com.hotel.domains.api;

public enum RoomStatus {
	
	NONE(0, "Non défini"),
	READY(1, "Disponible"),
	DIRTY(2, "Sale"),
	CLEANUP(3, "Nettoyée"),
	OUTOFSERVICE(4, "Hors service");
	
	private final int id;
	private final String name;
	
	RoomStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static RoomStatus get(int id){
		
		RoomStatus value = RoomStatus.NONE;
		for (RoomStatus item : RoomStatus.values()) {
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
}
