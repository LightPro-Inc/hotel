package com.hotel.domains.api;

public enum MaidDayJobStatus {
	
	PLANNED("Plannif�", "#3a87ad"),
	MAID_ABSENT("Absent", "#f41616"),
	MAID_PRESENT("Pr�sent", "green");
	
	private final String name;
	private final String color;
	
	MaidDayJobStatus(final String name, final String color){
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
