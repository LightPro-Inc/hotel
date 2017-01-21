package com.hotel.domains.api;

import java.io.IOException;

import com.securities.api.Person;

public interface Maid extends Person {
	boolean active() throws IOException;
	void activate(boolean active) throws IOException;
}
