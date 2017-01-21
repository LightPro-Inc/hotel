package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.GuestMetadata;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.RoomMetadata;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.securities.api.PersonMetadata;
import com.securities.impl.PersonImpl;

public class BookingsImpl implements Bookings {

	private transient final Base base;
	private final transient BookingMetadata dm;
	private final transient DomainsStore ds;
	
	public BookingsImpl(final Base base){
		this.base = base;
		this.dm = BookingMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public Booking book(UUID guestid, UUID roomid, Date start, Date end, double nightPriceApplied) throws IOException {
		
		if (start == null) {
            throw new IllegalArgumentException("Invalid start date : it can't be empty!");
        }
		
		if (end == null) {
            throw new IllegalArgumentException("Invalid end date : it can't be empty !");
        }
		
		final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24; 
    	long delta = end.getTime() - start.getTime();
    	long numberOfDays = delta / (MILLISECONDS_PER_DAY);
    	
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), new java.sql.Timestamp(start.getTime()));	
		params.put(dm.endDateKey(), new java.sql.Timestamp(end.getTime()));
		params.put(dm.statusKey(), BookingStatus.NEW.toString());
		params.put(dm.nightPriceAppliedKey(), nightPriceApplied);
		params.put(dm.vatRateAppliedKey(), 0.18);
		params.put(dm.paidAmountKey(), 0);
		params.put(dm.guestIdKey(), guestid);
		params.put(dm.roomIdKey(), roomid);
		params.put(dm.ttcTotalBookingAmountKey(), numberOfDays * nightPriceApplied);
		params.put(dm.vatBookingAmountKey(), 0.18 * numberOfDays * nightPriceApplied);
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
        return build(id);
	}

	@Override
	public List<Booking> between(Date start, Date end) throws IOException {
		List<Booking> values = new ArrayList<Booking>();
		
		String statement = String.format("SELECT %s FROM %s WHERE %s >= ? AND %s <= ? ORDER BY %s", dm.keyName(), dm.domainName(), dm.startDateKey(), dm.endDateKey(), dm.startDateKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(new java.sql.Timestamp(start.getTime()));
		params.add(new java.sql.Timestamp(end.getTime()));
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(build(domainStore.key())); 
		}		
		
		return values;		
	}

	@Override
	public Booking findSingle(UUID id) throws IOException {
		return new BookingImpl(this.base, id);
	}

	@Override
	public Guests guests() throws IOException {
		return new GuestsImpl(this.base);
	}

	@Override
	public List<Booking> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Booking> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Booking> find(int page, int pageSize, String filter) throws IOException {
		
		HorodateMetadata hm = HorodateImpl.dm();
		RoomMetadata rmDm = RoomMetadata.create();
		GuestMetadata gtDm = GuestMetadata.create();
		PersonMetadata persDm = PersonImpl.dm();
		
		String statement = String.format("SELECT bk.%s FROM %s bk " +
				   						 "JOIN %s rm ON rm.%s = bk.%s " + 
			   							 "JOIN %s gt ON gt.%s = bk.%s " +
			   							 "JOIN %s pers ON pers.%s = gt.%s " +
			   							 "WHERE rm.%s ILIKE ? OR concat(pers.%s, ' ', pers.%s) ILIKE ? ORDER BY bk.%s DESC LIMIT ? OFFSET ?", 
			   							 dm.keyName(), dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 gtDm.domainName(), gtDm.keyName(), dm.guestIdKey(),
			   							 persDm.domainName(), persDm.keyName(), gtDm.keyName(),
			   							 rmDm.numberKey(), persDm.lastNameKey(), persDm.firstNameKey(), 
			   							 hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		return ds.findDs(statement, params).stream()
					  					   .map(m -> build(m.key()))
				  					   	   .collect(Collectors.toList());
	}

	@Override
	public int totalCount(String filter) throws IOException {
		
		RoomMetadata rmDm = RoomMetadata.create();
		GuestMetadata gtDm = GuestMetadata.create();
		PersonMetadata persDm = PersonImpl.dm();
		
		String statement = String.format("SELECT COUNT(bk.%s) FROM %s bk " +
										 "JOIN %s rm ON rm.%s = bk.%s " + 
										 "JOIN %s gt ON gt.%s = bk.%s " +
										 "JOIN %s pers ON pers.%s = gt.%s " +
										 "WHERE rm.%s ILIKE ? OR concat(pers.%s, ' ', pers.%s) ILIKE ?", 
										 dm.keyName(), dm.domainName(), 
										 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
										 gtDm.domainName(), gtDm.keyName(), dm.guestIdKey(),
										 persDm.domainName(), persDm.keyName(), gtDm.keyName(),
										 rmDm.numberKey(), persDm.lastNameKey(), persDm.firstNameKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Booking build(Object id) {
		return new BookingImpl(base, id);
	}

	@Override
	public boolean contains(Booking item) throws IOException {
		return ds.exists(item.id());
	}
}
