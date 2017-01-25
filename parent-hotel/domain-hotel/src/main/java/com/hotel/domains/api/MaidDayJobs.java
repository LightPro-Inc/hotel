package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface MaidDayJobs {
	MaidDayJob plan(LocalDate date, Maid maid) throws IOException;
	List<MaidDayJob> between(LocalDate start, LocalDate end) throws IOException;
	MaidDayJob get(LocalDate date, Maid maid) throws IOException;
	MaidDayJob get(Object id) throws IOException;
	MaidDayJob build(Object id);
	
	void delete(MaidDayJob item) throws IOException;
}
