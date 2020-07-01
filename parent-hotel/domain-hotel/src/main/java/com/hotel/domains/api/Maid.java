package com.hotel.domains.api;

import java.io.IOException;

import com.securities.api.Contact;

public interface Maid extends Contact {
	Hotel hotel() throws IOException;
	boolean active() throws IOException;
	void activate(boolean active) throws IOException;
	MaidDayJobs daysJob() throws IOException;
}
