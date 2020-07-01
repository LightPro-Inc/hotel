package com.lightpro.hotel.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.domains.api.MaidDayJob;

public final class MaidDayJobVm {
	
	public final UUID id;
	public final UUID maidId;
	public final String maid;
	public final LocalDate day;
	public final String status;
	public final String statusId;
	public final String statusColor;
	public final boolean isFuture;
	
	public MaidDayJobVm() {
        throw new UnsupportedOperationException("#MaidDayJobVm()");
    }
	
	public MaidDayJobVm(final MaidDayJob origin){
		try {
			this.id = origin.id();
	        this.maidId = origin.maid().id();
	        this.maid = origin.maid().name();
	        this.day = origin.day();
	        this.status = origin.status().toString();
	        this.statusId = origin.status().name();
	        this.statusColor = origin.status().color();
	        this.isFuture = origin.day().isAfter(LocalDate.now());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
}
