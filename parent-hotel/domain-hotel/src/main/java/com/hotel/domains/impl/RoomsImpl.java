package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomMetadata;
import com.hotel.domains.api.RoomStatus;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;

public class RoomsImpl implements Rooms {

	private transient final Base base;
	private final transient RoomMetadata dm;
	private final transient DomainsStore ds;
	private final transient Object roomcategoryid;
	
	public RoomsImpl(final Base base, Object roomcategoryid){
		this.base = base;
		this.dm = RoomMetadata.create();
		this.ds = this.base.domainsStore(this.dm);
		this.roomcategoryid = roomcategoryid;
	}
	
	@Override
	public int size() throws IOException {
		return totalCount("");
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
		String statement = String.format( "SELECT %s FROM %s "
										+ "WHERE %s=? AND %s ILIKE ? "
										+ "ORDER BY %s DESC LIMIT ? OFFSET ?", 
										dm.keyName(), dm.domainName(), 
										dm.roomcategoryIdKey(), dm.numberKey(), 
										hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add(this.roomcategoryid);
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
		
		String statement = String.format( "SELECT COUNT(%s) FROM %s "
										+ "WHERE %s=? AND %s ILIKE ? ",
										dm.keyName(), dm.domainName(), 
										dm.roomcategoryIdKey(), dm.numberKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add(this.roomcategoryid);
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());				
	}

	@Override
	public Room add(String number, String floor) throws IOException {
		
		if (StringUtils.isBlank(number)) {
            throw new IllegalArgumentException("Invalid number : it can't be empty!");
        }
		
		if (StringUtils.isBlank(floor)) {
            throw new IllegalArgumentException("Invalid floor : floor can't be empty !");
        }		
		
		if(ds.exists(dm.numberKey(), number)){
			throw new IllegalArgumentException("Number already exists !");			
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.numberKey(), number);	
		params.put(dm.floorKey(), floor);
		params.put(dm.statusKey(), BookingStatus.NEW.toString());
		params.put(dm.statusKey(), RoomStatus.READY.name());
		params.put(dm.roomcategoryIdKey(), this.roomcategoryid);
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
        return build(id);        
	}

	@Override
	public void delete(UUID id)  throws IOException {
		Room room = build(id);
		
		if(room.isPresent() && room.category().id().equals(this.roomcategoryid))
			delete(id);		
	}

	
	@Override	
	public Room findSingle(String number) throws IOException {
		List<DomainStore> results= ds.getAllByKey(dm.numberKey(), number);
		
		if(results.isEmpty())
			throw new NotFoundException("La chambre n'a pas été trouvée !");
		
		return build(results.get(0).key());		
	}

	@Override
	public Room findSingle(UUID id) throws IOException {
		Room item = build(id);
		
		if(!item.isPresent() && item.category().id().equals(this.roomcategoryid))
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
