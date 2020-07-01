package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.MaidDayJobMetadata;
import com.hotel.domains.api.MaidDayJobStatus;
import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;

public final class MaidDayJobDb extends GuidKeyEntityDb<MaidDayJob, MaidDayJobMetadata> implements MaidDayJob {
	
	private final Hotel module;
	
	public MaidDayJobDb(final Base base, final UUID id, final Hotel module){
		super(base, id, "Plannification introuvable !");
		this.module = module;
	}

	@Override
	public LocalDate day() throws IOException {
		java.sql.Date date = ds.get(dm.dayKey());
		return date.toLocalDate();
	}

	@Override
	public Maid maid() throws IOException {
		UUID maidId = ds.get(dm.maidIdKey());
		return new MaidDb(base, maidId, module);
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
