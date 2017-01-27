package com.hotel.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface Room extends Recordable<UUID, Room> {
	
	String number() throws IOException;
	RoomStatus status() throws IOException;
	RoomFloor floor() throws IOException;
	RoomCategory category() throws IOException;
	void update(String number, String floor) throws IOException;
	void changeStatus(RoomStatus status) throws IOException;
	
	boolean isFree() throws IOException;
	boolean isOccupied() throws IOException;
	boolean isReserved() throws IOException;
}
