package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.GuestMetadata;
import com.hotel.domains.api.RoomMetadata;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.Queryable;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.securities.api.PersonMetadata;
import com.securities.impl.PersonImpl;

public class BookingsOfGuestImpl implements Queryable<Booking> {

	private transient final Base base;
	private final transient BookingMetadata dm;
	private final transient DomainsStore ds;
	private final transient Object guestId;
	
	public BookingsOfGuestImpl(final Base base, Object guestId){
		this.base = base;
		this.dm = BookingMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.guestId = guestId;
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
			   							 "WHERE gt.%s=? AND (rm.%s ILIKE ? OR concat(pers.%s, ' ', pers.%s) ILIKE ?) ORDER BY bk.%s DESC LIMIT ? OFFSET ?", 
			   							 dm.keyName(), dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 gtDm.domainName(), gtDm.keyName(), dm.guestIdKey(),
			   							 persDm.domainName(), persDm.keyName(), gtDm.keyName(),
			   							 gtDm.keyName(), rmDm.numberKey(), persDm.lastNameKey(), persDm.firstNameKey(), 
			   							 hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add(this.guestId);
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
										 "WHERE gt.%s=? AND (rm.%s ILIKE ? OR concat(pers.%s, ' ', pers.%s) ILIKE ?) ORDER BY bk.%s DESC LIMIT ? OFFSET ?", 
										 dm.keyName(), dm.domainName(), 
										 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
										 gtDm.domainName(), gtDm.keyName(), dm.guestIdKey(),
										 persDm.domainName(), persDm.keyName(), gtDm.keyName(),
										 gtDm.keyName(), rmDm.numberKey(), persDm.lastNameKey(), persDm.firstNameKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add(this.guestId);
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
