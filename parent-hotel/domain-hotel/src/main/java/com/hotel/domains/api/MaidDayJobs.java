package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyAdvancedQueryable;

public interface MaidDayJobs extends GuidKeyAdvancedQueryable<MaidDayJob> {
	MaidDayJob add(LocalDate date) throws IOException;
	MaidDayJobs between(LocalDate start, LocalDate end) throws IOException;
	MaidDayJob get(LocalDate date) throws IOException;
	
	MaidDayJobs of(Maid maid) throws IOException;
}
