package com.hotel.domains.api;

import com.infrastructure.core.DomainMetadata;

public class RoomMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public RoomMetadata() {
		this.domainName = "hotel.rooms";
		this.keyName = "id";
	}
	
	public RoomMetadata(final String domainName, final String keyName){
		this.domainName = domainName;
		this.keyName = keyName;
	}
	
	@Override
	public String domainName() {
		return this.domainName;
	}

	@Override
	public String keyName() {
		return this.keyName;
	}
	
	public String numberKey() {
		return "number";
	}
	
	public String floorKey() {
		return "floor";
	}
	
	public String roomcategoryIdKey() {
		return "roomcategoryid";
	}
	
	public String statusKey() {
		return "status";
	}
	
	public static RoomMetadata create(){
		return new RoomMetadata();
	}
}
