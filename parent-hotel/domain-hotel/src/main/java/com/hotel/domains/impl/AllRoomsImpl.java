package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.RoomMetadata;
import com.hotel.domains.api.RoomStatus;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;

public class AllRoomsImpl implements Rooms {

	private transient final Base base;
	private final transient RoomMetadata dm;
	private final transient DomainsStore ds;
	private final transient Hotel module;
	
	public AllRoomsImpl(final Base base, final Hotel module){
		this.base = base;
		this.dm = RoomMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.module = module;
	}
	
	@Override
	public List<Room> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Room> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Room> find(int page, int pageSize, String filter) throws IOException {
		List<Room> values = new ArrayList<Room>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		RoomCategoryMetadata rcmDm = RoomCategoryMetadata.create();
		String statement = String.format("SELECT ro.%s FROM %s ro "
				+ "JOIN %s ca ON ca.%s=ro.%s "
				+ "WHERE (ca.%s ILIKE ? OR ro.%s LIKE ?) AND ca.%s=? "
				+ "ORDER BY ro.%s DESC LIMIT ? OFFSET ?", 
				dm.keyName(), dm.domainName(), 
				rcmDm.domainName(), rcmDm.keyName(), dm.roomcategoryIdKey(), 
				rcmDm.nameKey(), dm.numberKey(), rcmDm.moduleIdKey(),
				hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");				
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}		
		
		return values;		
	}

	@Override
	public int totalCount(String filter) throws IOException {
		RoomCategoryMetadata rcmDm = RoomCategoryMetadata.create();
		String statement = String.format("SELECT COUNT(ro.%s) FROM %s ro "
				+ "JOIN %s ca ON ca.%s=ro.%s "
				+ "WHERE (ca.%s ILIKE ? OR ro.%s LIKE ?) AND ca.%s=? ",
				dm.keyName(), dm.domainName(), 
				rcmDm.domainName(), rcmDm.keyName(), dm.roomcategoryIdKey(), 
				rcmDm.nameKey(), dm.numberKey(), rcmDm.moduleIdKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");		
		params.add("%" + filter + "%");			
		params.add(module.id());
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());			
	}

	@Override
	public Room add(String number, String floor) throws IOException {
		throw new UnsupportedOperationException("#add()");
	}

	@Override
	public void delete(Room item) throws IOException {
		if(contains(item))
			ds.delete(item.id());		
	}

	@Override
	public int size() throws IOException {
		return totalCount("");
	}

	@Override
	public Room get(String number) throws IOException {
		
		Optional<RoomImpl> roomOpt =  ds.getAllByKey(dm.numberKey(), number)
				 .stream()
		 		 .map(m -> new RoomImpl(base, UUIDConvert.fromObject(m.key())))
		 		 .findFirst();	
		
		if(roomOpt.isPresent() && contains(roomOpt.get()))
			return roomOpt.get();
		else
			throw new IllegalArgumentException("La chambre n'a pas été trouvée !");
	}

	@Override
	public Room get(UUID id) throws IOException {
		Room item = build(id);
		
		if(!contains(item))
			throw new NotFoundException("La chambre n'a pas été trouvée !");
		
		return item;
	}

	@Override
	public Room build(UUID id) {
		return new RoomImpl(base, id);
	}

	@Override
	public boolean contains(Room item) {
		try {
			return ds.exists(item.id()) && item.category().module().isEqual(module);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Room> availables() throws IOException {
		return all()
			    .stream()
			    .filter(m -> {
					try {
						return m.status() != RoomStatus.OUTOFSERVICE;
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;					
				})
			    .collect(Collectors.toList());
	}
}
