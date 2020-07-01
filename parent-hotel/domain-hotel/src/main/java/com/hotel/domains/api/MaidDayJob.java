package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Nonable;

public interface MaidDayJob extends Nonable {
	UUID id();
	LocalDate day() throws IOException;
	Maid maid() throws IOException;
	MaidDayJobStatus status() throws IOException;
	
	void markPresent() throws IOException;
	void markAbsent() throws IOException;
}
