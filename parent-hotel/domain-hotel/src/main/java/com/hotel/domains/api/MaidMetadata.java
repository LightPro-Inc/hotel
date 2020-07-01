package com.hotel.domains.api;

import com.infrastructure.core.DomainMetadata;

public class MaidMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public MaidMetadata() {
		this.domainName = "hotel.maids";
		this.keyName = "id";
	}
	
	public MaidMetadata(final String domainName, final String keyName){
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
	
	public String activeKey() {
		return "active";
	}
	
	public String moduleIdKey() {
		return "moduleid";
	}
	
	public static MaidMetadata create(){
		return new MaidMetadata();
	}
}
