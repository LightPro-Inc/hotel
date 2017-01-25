package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.MaidDayJobMetadata;
import com.hotel.domains.api.MaidDayJobStatus;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;

public class MaidDayJobImpl implements MaidDayJob {

	private final transient Base base;
	private final transient Object id;
	private final transient MaidDayJobMetadata dm;
	private final transient DomainStore ds;
	
	public MaidDayJobImpl(final Base base, final Object id){
		this.base = base;
		this.id = id;
		this.dm = MaidDayJobMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return UUIDConvert.fromObject(this.id);
	}

	@Override
	public boolean isPresent() throws IOException {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public LocalDate day() throws IOException {
		java.sql.Date date = ds.get(dm.dayKey());
		return date.toLocalDate();
	}

	@Override
	public Maid maid() throws IOException {
		UUID maidId = ds.get(dm.maidIdKey());
		return new MaidImpl(base, maidId);
	}

	@Override
	public MaidDayJobStatus status() throws IOException {
		String statusStr = ds.get(dm.statusKey());
		MaidDayJobStatus status = MaidDayJobStatus.valueOf(statusStr);
		
		if(status == MaidDayJobStatus.PLANNED && day().isBefore(LocalDate.now()))
		{
			markAbsent();
			status = MaidDayJobStatus.MAID_ABSENT;
		}
		
		return status;
	}

	@Override
	public void markPresent() throws IOException {
		ds.set(dm.statusKey(), MaidDayJobStatus.MAID_PRESENT.name());
	}

	@Override
	public void markAbsent() throws IOException {
		ds.set(dm.statusKey(), MaidDayJobStatus.MAID_ABSENT.name());
	}
}