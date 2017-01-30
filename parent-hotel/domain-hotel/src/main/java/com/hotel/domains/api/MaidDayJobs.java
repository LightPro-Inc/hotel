package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MaidDayJobs {
	MaidDayJob plan(LocalDate date, Maid maid) throws IOException;
	List<MaidDayJob> between(LocalDate start, LocalDate end) throws IOException;
	MaidDayJob get(LocalDate date, Maid maid) throws IOException;
	MaidDayJob get(UUID id) throws IOException;
	MaidDayJob build(UUID id);
	
	void delete(MaidDayJob item) throws IOException;
}
