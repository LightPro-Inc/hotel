package com.lightpro.hotel.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hotel.domains.api.MaidDayJob;

public class MaidDayJobVm {
	private final transient MaidDayJob origin;
	
	public MaidDayJobVm() {
        throw new UnsupportedOperationException("#MaidDayJobVm()");
    }
	
	public MaidDayJobVm(final MaidDayJob origin){
		this.origin = origin;
	}
	
	@JsonGetter
	public UUID getId() {
		return this.origin.id();
	}
	
	@JsonGetter
	public UUID getMaidId() throws IOException{
		return this.origin.maid().id();
	}
	
	@JsonGetter
	public String getMaid() throws IOException{
		return this.origin.maid().fullName();
	}
	
	@JsonGetter
	public LocalDate getDay() throws IOException {
		return this.origin.day();
	}	
	
	@JsonGetter
	public String getStatus() throws IOException {
		return this.origin.status().toString();
	}
	
	@JsonGetter
	public String getStatusId() throws IOException {
		return this.origin.status().name();
	}
	
	@JsonGetter
	public String getStatusColor() throws IOException {
		return this.origin.status().color();
	}
	
	@JsonGetter
	public boolean getIsFuture() throws IOException {
		return this.origin.day().isAfter(LocalDate.now());
	}
}
