package com.hotel.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.Contact;

public interface Room extends Nonable {
	UUID id();
	String number() throws IOException;
	RoomStatus status() throws IOException;
	RoomFloor floor() throws IOException;
	RoomCategory category() throws IOException;
	void update(String number, String floor) throws IOException;
	void changeStatus(RoomStatus status) throws IOException;
	Bookings bookings() throws IOException;
	boolean isFree() throws IOException;
	boolean isOccupied() throws IOException;
	boolean isReserved() throws IOException;
	boolean isFree(LocalDate date) throws IOException;
	boolean isOccupied(LocalDate date) throws IOException;
	boolean isReserved(LocalDate date) throws IOException;
	
	Booking book(Contact customer, Contact guest, LocalDateTime start, LocalDateTime end, Contact seller) throws IOException;
}
