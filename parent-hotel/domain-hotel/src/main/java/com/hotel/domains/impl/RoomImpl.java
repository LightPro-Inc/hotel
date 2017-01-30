package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.ConstRoomFloor;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.RoomMetadata;
import com.hotel.domains.api.RoomStatus;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;

public class RoomImpl implements Room {

	private final transient Base base;
	private final transient UUID id;
	private final transient RoomMetadata dm;
	private final transient DomainStore ds;
	
	public RoomImpl(final Base base, final UUID id){
		this.base = base;
		this.id = id;
		this.dm = RoomMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent() {
		try {
			return base.domainsStore(dm).exists(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public String number() throws IOException {
		return ds.get(dm.numberKey());		
	}

	@Override
	public RoomStatus status() throws IOException {
		
		String statusStr = ds.get(dm.statusKey());
		return RoomStatus.valueOf(statusStr);
	}

	@Override
	public RoomFloor floor() throws IOException {
		String floorId = ds.get(dm.floorKey());
		return new ConstRoomFloor(floorId);
	}

	@Override
	public RoomCategory category() throws IOException {		
		UUID roomcategoryid = ds.get(dm.roomcategoryIdKey());
		return new RoomCategoryImpl(this.base, roomcategoryid);
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
			
			ds.set(dm.statusKey(), status.name());	
		}
	}

	@Override
	public boolean isFree() throws IOException {
		LocalDate day = LocalDate.now();
		
		BookingMetadata bkDm = BookingMetadata.create();
		String statement = String.format("SELECT %s FROM %s "
											+ "WHERE %s::date <= ? AND %s::date >= ? AND %s=?",
											bkDm.keyName(), bkDm.domainName(),
											bkDm.startDateKey(), bkDm.endDateKey(), bkDm.roomIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(day));
		params.add(java.sql.Date.valueOf(day));
		params.add(this.id);
		
		Optional<DomainStore> bkDs = base.domainsStore(bkDm).getFirstDs(statement, params);
		if(bkDs.isPresent())
		{
			Booking bk = new BookingImpl(base, UUIDConvert.fromObject(bkDs.get().key()));
			if(bk.status() == BookingStatus.CHECKEDOUT || bk.status() == BookingStatus.CANCELLED)
				return true;
			else
				return false;
		}else
			return true;
	}

	@Override
	public boolean isOccupied() throws IOException {
		LocalDate day = LocalDate.now();
		
		BookingMetadata bkDm = BookingMetadata.create();
		String statement = String.format("SELECT %s FROM %s "
											+ "WHERE %s::date <= ? AND %s::date >= ? AND %s=?",
											bkDm.keyName(), bkDm.domainName(),
											bkDm.startDateKey(), bkDm.endDateKey(), bkDm.roomIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(day));
		params.add(java.sql.Date.valueOf(day));
		params.add(this.id);
		
		Optional<DomainStore> bkDs = base.domainsStore(bkDm).getFirstDs(statement, params);
		if(bkDs.isPresent())
		{
			Booking bk = new BookingImpl(base, UUIDConvert.fromObject(bkDs.get().key()));
			if(bk.status() == BookingStatus.ARRIVED || bk.status() == BookingStatus.LATE_CHECKOUT)
				return true;
			else
				return false;
		}else
			return false;
	}

	@Override
	public boolean isReserved() throws IOException {
		LocalDate day = LocalDate.now();
		
		BookingMetadata bkDm = BookingMetadata.create();
		String statement = String.format("SELECT %s FROM %s "
											+ "WHERE %s::date <= ? AND %s::date >= ? AND %s=?",
											bkDm.keyName(), bkDm.domainName(),
											bkDm.startDateKey(), bkDm.endDateKey(), bkDm.roomIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(day));
		params.add(java.sql.Date.valueOf(day));
		params.add(this.id);
		
		Optional<DomainStore> bkDs = base.domainsStore(bkDm).getFirstDs(statement, params);
		if(bkDs.isPresent())
		{
			Booking bk = new BookingImpl(base, UUIDConvert.fromObject(bkDs.get().key()));
			if(bk.status() == BookingStatus.CONFIRMED || bk.status() == BookingStatus.LATE_ARRIVAL || bk.status() == BookingStatus.EXPIRED)
				return true;
			else
				return false;
		}else
			return false;
	}
	
	@Override
	public boolean isEqual(Room item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(Room item) {
		return !isEqual(item);
	}

}
