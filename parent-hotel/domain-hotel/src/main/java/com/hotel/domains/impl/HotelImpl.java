package com.hotel.domains.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.ConstRoomFloor;
import com.hotel.domains.api.DayOccupations;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.MaidDayJobs;
import com.hotel.domains.api.Maids;
import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.Horodate;
import com.infrastructure.datasource.Base;
import com.securities.api.Company;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.impl.BasisModule;

public class HotelImpl implements Hotel {

	private transient Base base;
	private transient Module origin;
	
	public HotelImpl(Base base, final UUID id){
		this.base = base;
		this.origin = new BasisModule(base, id);
	}

	@Override
	public Company company() throws IOException {
		return this.origin.company();
	}

	@Override
	public RoomCategories roomCategories() throws IOException {
		return new RoomCategoriesImpl(this.base, this);
	}

	@Override
	public Rooms allRooms() throws IOException {
		return new AllRoomsImpl(this.base, this);
	}

	@Override
	public List<RoomFloor> allRoomFloors() throws IOException {
		return ConstRoomFloor.allFloors();
	}

	@Override
	public Bookings bookings() throws IOException {
		return new BookingsImpl(this.base, this);
	}

	@Override
	public Maids maids() throws IOException {
		return new MaidsImpl(base, company());
	}

	@Override
	public DayOccupations dayOccupations() throws IOException {
		return new DayOccupationsImpl(bookings());
	}

	@Override
	public MaidDayJobs maidDayJobs() throws IOException {
		return new MaidDayJobsImpl(base, company());
	}

	@Override
	public String description() throws IOException {
		return origin.description();
	}

	@Override
	public void install() throws IOException {
		origin.install();
	}

	@Override
	public boolean isAvailable() {
		return origin.isAvailable();
	}

	@Override
	public boolean isInstalled() {
		return origin.isInstalled();
	}

	@Override
	public boolean isSubscribed() {
		return origin.isSubscribed();
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public int order() throws IOException {
		return origin.order();
	}

	@Override
	public String shortName() throws IOException {
		return origin.shortName();
	}

	@Override
	public ModuleType type() throws IOException {
		return origin.type();
	}

	@Override
	public void uninstall() throws IOException {
		origin.uninstall();
	}

	@Override
	public Horodate horodate() {
		return origin.horodate();
	}

	@Override
	public UUID id() {
		return origin.id();
	}

	@Override
	public boolean isEqual(Module item) {
		return origin.isEqual(item);
	}

	@Override
	public boolean isNotEqual(Module item) {
		return origin.isNotEqual(item);
	}

	@Override
	public boolean isPresent() {
		return origin.isPresent();
	}

	@Override
	public void activate(boolean active) throws IOException {
		origin.activate(active);
	}

	@Override
	public boolean isActive() {
		return origin.isActive();
	}
}
