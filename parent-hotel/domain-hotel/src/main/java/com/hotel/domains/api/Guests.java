package com.hotel.domains.api;

import java.io.IOException;

import com.infrastructure.core.GuidKeyAdvancedQueryable;
import com.securities.api.Contact;

public interface Guests extends GuidKeyAdvancedQueryable<Guest> {
	Guest add(Contact item) throws IOException;
	boolean contains(Contact item) throws IOException;
}
