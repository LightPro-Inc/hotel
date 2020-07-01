package com.hotel.domains.api;

public enum MaidStatus {
	NONE(0, "Non d�fini"),
	ACTIVE(1, "Activ�"),
	UNACTIVE(2, "D�sactiv�");
	
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
