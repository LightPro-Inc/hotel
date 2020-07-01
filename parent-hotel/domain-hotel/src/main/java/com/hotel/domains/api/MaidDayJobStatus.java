package com.hotel.domains.api;

public enum MaidDayJobStatus {
	
	NONE(0, "Non défini", "white"),
	PLANNED(1, "Plannifé", "#3a87ad"),
	MAID_ABSENT(2, "Absent", "#f41616"),
	MAID_PRESENT(3, "Présent", "green");
	
	private final int id;
	private final String name;
	private final String color;
	
	MaidDayJobStatus(final int id, final String name, final String color){
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public static MaidDayJobStatus get(int id){
		
		MaidDayJobStatus value = MaidDayJobStatus.NONE;
		for (MaidDayJobStatus item : MaidDayJobStatus.values()) {
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
