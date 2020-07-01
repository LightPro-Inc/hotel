package com.hotel.domains.api;

public enum BookingStatus {
	NONE(0, "Non défini", "white"),
	UNEXPECTED_STATE(1, "Etat non attendu",  "black"),
	NEW(2, "Nouveau", "orange"), 	
	CONFIRMED(3, "Confirmée", "green"),
	LATE_ARRIVAL(4, "Arrivée tardive", "#f41616"),
	ARRIVED(5, "Arrivé", "#1691f4"),
	LATE_CHECKOUT(6, "Départ tardif", "#f41616"),
	CHECKEDOUT(7, "Parti", "gray"),
	EXPIRED(8, "Expirée (pas confirmée à temps)", "red"),
	CANCELLED(9, "Annulée", "#CECECE");
	
	private final int id;
	private final String name;
	private final String color;
	
	BookingStatus(final int id, final String name, final String color){
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public static BookingStatus get(int id){
		
		BookingStatus value = BookingStatus.NONE;
		for (BookingStatus item : BookingStatus.values()) {
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
