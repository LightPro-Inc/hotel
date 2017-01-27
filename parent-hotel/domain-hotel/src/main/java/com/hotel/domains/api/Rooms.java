package com.hotel.domains.api;

import java.io.IOException;
import java.util.List;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;

/**
 * 
 * @author oob
 *
 */
public interface Rooms extends AdvancedQueryable<Room>, Updatable<Room> {
	Room add(String number, String floor) throws IOException;
	int size() throws IOException;
	Room get(String number) throws IOException;
	List<Room> availables() throws IOException;
}
