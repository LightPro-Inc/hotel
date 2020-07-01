package com.hotel.domains.api;

import java.io.IOException;

public interface DayOccupation {
	Room room() throws IOException;
	Booking booking() throws IOException;
	DayOccupationStatus status() throws IOException;
}
