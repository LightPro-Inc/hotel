package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

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
	
	public AllRoomsImpl(final Base base){
		this.base = base;
		this.dm = RoomMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
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
		String statement = String.format("SELECT %s FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s ILIKE ?) OR %s LIKE ? ORDER BY %s DESC LIMIT ? OFFSET ?", dm.keyName(), dm.domainName(), dm.roomcategoryIdKey(), rcmDm.keyName(), rcmDm.domainName(), rcmDm.nameKey(), dm.numberKey(), hm.dateCreatedKey());
		
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
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(build(domainStore.key())); 
		}		
		
		return values;		
	}

	@Override
	public int totalCount(String filter) throws IOException {
		RoomCategoryMetadata rcmDm = RoomCategoryMetadata.create();
		String statement = String.format("SELECT COUNT(%s) FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s ILIKE ?) OR %s LIKE ? ", dm.keyName(), dm.domainName(), dm.roomcategoryIdKey(), rcmDm.keyName(), rcmDm.domainName(), rcmDm.nameKey(), dm.numberKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());			
	}

	@Override
	public Room add(String number, String floor) throws IOException {
		throw new UnsupportedOperationException("#add()");
	}

	@Override
	public void delete(Room item) throws IOException {
		ds.delete(item.id());		
	}

	@Override
	public int size() throws IOException {
		return totalCount("");
	}

	@Override
	public Room get(String number) throws IOException {
		
		return ds.getAllByKey(dm.numberKey(), number)
				 .stream()
		 		 .map(m -> new RoomImpl(base, m.key()))
		 		 .findFirst()
		 		 .get();		
	}

	@Override
	public Room get(Object id) throws IOException {
		Room item = build(id);
		
		if(!item.isPresent())
			throw new NotFoundException("La chambre n'a pas été trouvée !");
		
		return item;
	}

	@Override
	public Room build(Object id) {
		return new RoomImpl(base, id);
	}

	@Override
	public boolean contains(Room item) throws IOException {
		return ds.exists(item.id());
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
