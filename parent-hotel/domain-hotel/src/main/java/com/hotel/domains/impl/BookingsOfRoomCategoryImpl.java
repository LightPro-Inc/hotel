package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomMetadata;
import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;

public class BookingsOfRoomCategoryImpl implements AdvancedQueryable<Booking, UUID> {

	private transient final Base base;
	private final transient BookingMetadata dm;
	private final transient DomainsStore ds;
	private final transient RoomCategory roomCategory;
	
	public BookingsOfRoomCategoryImpl(Base base, UUID roomCategoryId){
		this.base = base;
		this.dm = BookingMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.roomCategory = new RoomCategoryImpl(base, roomCategoryId);
	}
	
	@Override
	public List<Booking> all() throws IOException {
		return find("");
	}

	@Override
	public List<Booking> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Booking> find(int page, int pageSize, String filter) throws IOException {
		
		HorodateMetadata hm = HorodateImpl.dm();
		RoomMetadata rmDm = RoomMetadata.create();
		
		String statement = String.format("SELECT bk.%s FROM %s bk " +
				   						 "JOIN %s rm ON rm.%s = bk.%s " + 
			   							 "WHERE rm.%s=? AND (rm.%s ILIKE ?) ORDER BY bk.%s DESC LIMIT ? OFFSET ?", 
			   							 dm.keyName(), dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 rmDm.roomcategoryIdKey(), rmDm.numberKey(), hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add(this.roomCategory.id());
		params.add("%" + filter + "%");
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		return ds.findDs(statement, params).stream()
					  					   .map(m -> build(UUIDConvert.fromObject(m.key())))
				  					   	   .collect(Collectors.toList());
	}

	@Override
	public int totalCount(String filter) throws IOException {
		RoomMetadata rmDm = RoomMetadata.create();
		
		String statement = String.format("SELECT COUNT(bk.%s) FROM %s bk " +
				   						 "JOIN %s rm ON rm.%s = bk.%s " + 
			   							 "WHERE rm.%s=? AND (rm.%s ILIKE ?)", 
			   							 dm.keyName(), dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 rmDm.roomcategoryIdKey(), rmDm.numberKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add(this.roomCategory.id());
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());		
	}

	@Override
	public Booking build(UUID id) {
		return new BookingImpl(base, id);
	}

	@Override
	public boolean contains(Booking item) {
		try {
			return ds.exists(item.id());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Booking get(UUID id) throws IOException {
		Booking item = build(id);
		
		if(!item.isPresent() && item.room().category().isNotEqual(roomCategory))
			throw new IllegalArgumentException("La réservation n'a pas été trouvée !");
		
		return item;
	}
}
