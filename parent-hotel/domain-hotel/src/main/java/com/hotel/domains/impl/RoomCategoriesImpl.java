package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;

public class RoomCategoriesImpl implements RoomCategories {

	private transient final Base base;
	private final transient RoomCategoryMetadata dm;
	private final transient DomainsStore ds;
	
	public RoomCategoriesImpl(final Base base){
		this.base = base;
		this.dm = RoomCategoryMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public RoomCategory add(String name, int capacity, double nightPrice) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (capacity == 0) {
            throw new IllegalArgumentException("Invalid capacity : capacity must be upper than zero !");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.capacityKey(), capacity);
		params.put(dm.nightPriceKey(), nightPrice);
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
        return build(id);        
	}

	@Override
	public List<RoomCategory> all() throws IOException {
		return find(0, 0, "");
	}
	
	@Override
	public List<RoomCategory> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public int totalCount(String filter) throws IOException {
		
		String statement = String.format("SELECT COUNT(%s) FROM %s " +
										 "WHERE %s ILIKE ? ",
										 dm.keyName(), dm.domainName(), 
										 dm.nameKey());

		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());			
	}

	@Override
	public RoomCategory get(Object id)  throws IOException {
		RoomCategory item = build(id);
		
		if(!item.isPresent())
			throw new IllegalArgumentException("La catégorie de chambre n'a pas été trouvée !");
		
		return item;
	}

	@Override
	public List<RoomCategory> find(int page, int pageSize, String filter) throws IOException {
		
		HorodateMetadata hm = HorodateImpl.dm();		
		String statement = String.format("SELECT %s FROM %s " +
			   							 "WHERE %s ILIKE ? "
			   							 + "ORDER BY %s DESC LIMIT ? OFFSET ?", 
			   							 dm.keyName(), dm.domainName(), 
			   							 dm.nameKey(),
			   							 hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
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
	public void delete(RoomCategory item) throws IOException {
		ds.delete(item.id());
	}

	@Override
	public RoomCategory build(Object id) {
		return new RoomCategoryImpl(base, id);
	}

	@Override
	public boolean contains(RoomCategory item) throws IOException {
		return ds.exists(item.id());
	}
}
