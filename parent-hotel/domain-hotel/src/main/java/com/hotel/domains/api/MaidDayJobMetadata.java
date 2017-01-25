package com.hotel.domains.api;

import com.infrastructure.core.DomainMetadata;

public class MaidDayJobMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public MaidDayJobMetadata() {
		this.domainName = "hotel.maid_dayjobs";
		this.keyName = "id";
	}
	
	public MaidDayJobMetadata(final String domainName, final String keyName){
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
	
	public String dayKey() {
		return "day";
	}
	
	public String maidIdKey() {
		return "maidid";
	}
	
	public String statusKey() {
		return "status";
	}
	
	public static MaidDayJobMetadata create(){
		return new MaidDayJobMetadata();
	}
}
