package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.MaidDayJobStatus;
import com.infrastructure.core.GuidKeyEntityNone;

public final class MaidDayJobNone extends GuidKeyEntityNone<MaidDayJob> implements MaidDayJob {

	@Override
	public LocalDate day() throws IOException {
		return null;
	}

	@Override
	public Maid maid() throws IOException {
		return new MaidNone();
	}

	@Override
	public MaidDayJobStatus status() throws IOException {
		return MaidDayJobStatus.NONE;
	}

	@Override
	public void markPresent() throws IOException {

	}

	@Override
	public void markAbsent() throws IOException {

	}
}
