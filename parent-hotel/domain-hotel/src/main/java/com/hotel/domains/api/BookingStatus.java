package com.hotel.domains.api;

public enum BookingStatus {
	UNEXPECTED_STATE("Etat non attendu",  "black"),
	NEW("Nouveau", "orange"), 	
	CONFIRMED("Confirm�e", "green"),
	LATE_ARRIVAL("Arriv�e tardive", "#f41616"),
	ARRIVED("Arriv�", "#1691f4"),
	LATE_CHECKOUT("D�part tardif", "#f41616"),
	CHECKEDOUT("Parti", "gray"),
	EXPIRED("Expir�e (pas confirm�e � temps)", "red"),
	CANCELLED("Annul�e", "#CECECE");
	
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
