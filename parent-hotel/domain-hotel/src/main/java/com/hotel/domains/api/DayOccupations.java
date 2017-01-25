package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface DayOccupations {
	List<DayOccupation> of(LocalDate date) throws IOException;	
}
