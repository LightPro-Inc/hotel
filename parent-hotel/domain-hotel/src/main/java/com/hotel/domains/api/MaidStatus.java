package com.hotel.domains.api;

public enum MaidStatus {
	NONE(0, "Non défini"),
	ACTIVE(1, "Activé"),
	UNACTIVE(2, "Désactivé");
	
	private final int id;
	private final String name;
	
	MaidStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static MaidStatus get(int id){
		
		MaidStatus value = MaidStatus.NONE;
		for (MaidStatus item : MaidStatus.values()) {
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
