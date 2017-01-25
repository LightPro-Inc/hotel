package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface MaidDayJob extends Recordable<UUID> {
	LocalDate day() throws IOException;
	Maid maid() throws IOException;
	MaidDayJobStatus status() throws IOException;
	
	void markPresent() throws IOException;
	void markAbsent() throws IOException;
}
