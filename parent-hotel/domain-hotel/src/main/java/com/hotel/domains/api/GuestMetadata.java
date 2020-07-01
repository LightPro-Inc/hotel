package com.hotel.domains.api;

import com.infrastructure.core.DomainMetadata;

public class GuestMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public GuestMetadata() {
		this.domainName = "hotel.guests";
		this.keyName = "id";
	}
	
	public GuestMetadata(final String domainName, final String keyName){
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
	
	public String moduleIdKey() {
		return "moduleid";
	}
	
	public static GuestMetadata create(){
		return new GuestMetadata();
	}
}
