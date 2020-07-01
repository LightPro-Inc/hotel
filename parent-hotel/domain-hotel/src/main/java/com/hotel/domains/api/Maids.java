package com.hotel.domains.api;

import java.io.IOException;

import com.infrastructure.core.GuidKeyAdvancedQueryable;
import com.securities.api.Contact;

public interface Maids extends GuidKeyAdvancedQueryable<Maid> {
	Maid add(Contact contact) throws IOException;
	Maids withStatus(MaidStatus status) throws IOException;
	boolean contains(Contact contact) throws IOException;
}
