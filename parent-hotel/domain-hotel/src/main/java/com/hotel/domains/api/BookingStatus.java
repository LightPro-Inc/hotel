package com.hotel.domains.api;

public enum BookingStatus {
	UNEXPECTED_STATE("Etat non attendu",  "black"),
	NEW("Nouveau", "orange"), 	
	CONFIRMED("Confirmée", "green"),
	LATE_ARRIVAL("Arrivée tardive", "#f41616"),
	ARRIVED("Arrivé", "#1691f4"),
	LATE_CHECKOUT("Départ tardif", "#f41616"),
	CHECKEDOUT("Parti", "gray"),
	EXPIRED("Expirée (pas confirmée à temps)", "red"),
	CANCELLED("Annulée", "#CECECE");
	
	private final String name;
	private final String color;
	
	BookingStatus(final String name, final String color){
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
