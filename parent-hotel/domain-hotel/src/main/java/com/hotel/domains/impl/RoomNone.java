package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.RoomStatus;
import com.infrastructure.core.GuidKeyEntityNone;
import com.securities.api.Contact;

public final class RoomNone extends GuidKeyEntityNone<Room> implements Room {

	@Override
	public String number() throws IOException {
		return "Aucun nombre";
	}

	@Override
	public RoomStatus status() throws IOException {
		return RoomStatus.OUTOFSERVICE;
	}

	@Override
	public RoomFloor floor() throws IOException {
		return new RoomFloorNone();
	}

	@Override
	public RoomCategory category() throws IOException {
		return new RoomCategoryNone();
	}

	@Override
	public void update(String number, String floor) throws IOException {

	}

	@Override
	public void changeStatus(RoomStatus status) throws IOException {

	}

	@Override
	public boolean isFree() throws IOException {
		return false;
	}

	@Override
	public boolean isOccupied() throws IOException {
		return false;
	}

	@Override
	public boolean isReserved() throws IOException {
		return false;
	}

	@Override
	public Bookings bookings() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public boolean isFree(LocalDate date) throws IOException {
		return false;
	}

	@Override
	public boolean isOccupied(LocalDate date) throws IOException {
		return false;
	}

	@Override
	public boolean isReserved(LocalDate date) throws IOException {
		return false;
	}

	@Override
	public Booking book(Contact customer, Contact guest, LocalDateTime start, LocalDateTime end, Contact seller)
			throws IOException {
		return new BookingNone();
	}
}
