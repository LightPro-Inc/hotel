package com.hotel.domains.api;

import com.infrastructure.core.DomainMetadata;

public class RoomCategoryMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public RoomCategoryMetadata() {
		this.domainName = "hotel.roomcategories";
		this.keyName = "id";
	}
	
	public RoomCategoryMetadata(final String domainName, final String keyName){
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
	
	public String nameKey() {
		return "name";
	}
	
	public String capacityKey() {
		return "capacity";
	}
	
	public String nightPriceKey() {
		return "nightprice";
	}
	
	public static RoomCategoryMetadata create(){
		return new RoomCategoryMetadata();
	}
}
