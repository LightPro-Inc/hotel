package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.ConstRoomFloor;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.RoomMetadata;
import com.hotel.domains.api.RoomStatus;
import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.securities.api.Contact;

public final class RoomDb extends GuidKeyEntityDb<Room, RoomMetadata> implements Room {
	
	private final Hotel module;
	
	public RoomDb(final Base base, final UUID id, final Hotel module){
		super(base, id, "Chambre introuvable !");
		this.module = module;
	}
	
	@Override
	public String number() throws IOException {
		return ds.get(dm.numberKey());		
	}

	@Override
	public RoomStatus status() throws IOException {		
		int statusId = ds.get(dm.statusKey());
		return RoomStatus.get(statusId);
	}

	@Override
	public RoomFloor floor() throws IOException {
		String floorId = ds.get(dm.floorKey());
		return new ConstRoomFloor(floorId);
	}

	@Override
	public RoomCategory category() throws IOException {		
		UUID roomcategoryid = ds.get(dm.roomcategoryIdKey());
		return new RoomCategoryDb(this.base, roomcategoryid, module);
	}

	@Override
	public void update(String number, String floor) throws IOException {
		
		if (StringUtils.isBlank(number)) {
            throw new IllegalArgumentException("Invalid number : it can't be empty!");
        }
		
		if (StringUtils.isBlank(floor)) {
            throw new IllegalArgumentException("Invalid floor : it can't be empty!");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.numberKey(), number);	
		params.put(dm.floorKey(), floor);
		
		ds.set(params);			
	}

	@Override
	public void changeStatus(RoomStatus status) throws IOException {
		
		if(status == RoomStatus.CLEANUP && isFree()){
			ds.set(dm.statusKey(), RoomStatus.READY.name());	
		}else{
			if(status == RoomStatus.OUTOFSERVICE && isOccupied())
				throw new IllegalArgumentException("La chambre est actuellement occupée !");
			
			ds.set(dm.statusKey(), status.id());	
		}
	}

	@Override
	public boolean isFree() throws IOException {
		return isFree(LocalDate.now());
	}

	@Override
	public boolean isOccupied() throws IOException {
		return isOccupied(LocalDate.now());
	}

	@Override
	public boolean isReserved() throws IOException {
		return isReserved(LocalDate.now());
	}

	@Override
	public Booking book(Contact customer, Contact guest, LocalDateTime start, LocalDateTime end, Contact seller) throws IOException {
		return bookings().book(customer, guest, start, end, seller);
	}

	@Override
	public Bookings bookings() throws IOException {
		return category().module().bookings().of(this);
	}

	@Override
	public boolean isFree(LocalDate date) throws IOException {
		boolean isFree = false;

		Bookings bookings = bookings().at(date);
		if(bookings.count() > 0){
			for (Booking bk : bookings.all()) {
				if(bk.status() == BookingStatus.CHECKEDOUT || bk.status() == BookingStatus.CANCELLED)
				{
					isFree = true;
					break;
				}
			}
		}else{
			isFree = true;
		}
		
		return isFree;
	}

	@Override
	public boolean isOccupied(LocalDate date) throws IOException {
		boolean isOccupied = false;

		Bookings bookings = bookings().at(date);
		if(bookings.count() > 0){
			for (Booking bk : bookings.all()) {
				if(bk.status() == BookingStatus.ARRIVED || bk.status() == BookingStatus.LATE_CHECKOUT)
				{
					isOccupied = true;
					break;
				}
			}
		}	
		
		return isOccupied;
	}

	@Override
	public boolean isReserved(LocalDate date) throws IOException {
		boolean isReserved = false;

		Bookings bookings = bookings().at(date);
		if(bookings.count() > 0){
			for (Booking bk : bookings.all()) {
				if(bk.status() == BookingStatus.CONFIRMED || bk.status() == BookingStatus.LATE_ARRIVAL || bk.status() == BookingStatus.EXPIRED)
				{
					isReserved = true;
					break;
				}
			}
		}	
		
		return isReserved;
	}
}
