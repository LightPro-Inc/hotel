package com.hotel.domains.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Queryable;

/**
 * 
 * @author oob
 *
 */
public interface Rooms extends Queryable<Room> {
	Room add(String number, String floor) throws IOException;
	void delete(UUID id) throws IOException;;
	int size() throws IOException;
	Room findSingle(UUID id) throws IOException;
	Room findSingle(String number) throws IOException;
	List<Room> availables() throws IOException;
}
