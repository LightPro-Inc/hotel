package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.RoomMetadata;
import com.hotel.domains.api.RoomStatus;
import com.hotel.domains.api.Rooms;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;

public final class RoomsDb extends GuidKeyAdvancedQueryableDb<Room, RoomMetadata> implements Rooms {

	private final transient RoomCategory category;
	private final transient Hotel module;
	private final transient String number;
	private final transient RoomStatus status;
	
	public RoomsDb(final Base base, final Hotel module, final RoomCategory category, final String number, final RoomStatus status){
		super(base, "Chambre introuvable !");
		this.category = category;
		this.module = module;
		this.number = number;
		this.status = status;
	}

	@Override
	public Room add(String number, String floor) throws IOException {
		
		if(category.isNone())
			throw new IllegalArgumentException("Vous devez spécifier une catégorie de chambre !");
		
		if (StringUtils.isBlank(number))
            throw new IllegalArgumentException("Invalid number : it can't be empty!");
		
		if (StringUtils.isBlank(floor))
            throw new IllegalArgumentException("Invalid floor : floor can't be empty !");	
		
		if(count(number) > 0)
			throw new IllegalArgumentException("Ce numéro est déjà attribué à une chambre !");			
				
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.numberKey(), number);	
		params.put(dm.floorKey(), floor);
		params.put(dm.statusKey(), BookingStatus.NEW.id());
		params.put(dm.statusKey(), RoomStatus.READY.id());
		params.put(dm.roomcategoryIdKey(), category.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
        return build(id);        
	}
	
	@Override	
	public Room get(String number) throws IOException {
		List<Room> results = withNumber(number).all();	
		
		if(results.isEmpty())
			throw new IllegalArgumentException(msgNotFound);
		
		return results.get(0);
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		RoomCategoryMetadata rmcatDm = RoomCategoryMetadata.create();
		String statement = String.format("%s rm "
				+ "JOIN %s cat ON cat.%s=rm.%s "
				+ "WHERE rm.%s ILIKE ? AND cat.%s=?",
				dm.domainName(), 
				rmcatDm.domainName(), rmcatDm.keyName(), dm.roomcategoryIdKey(),
				dm.numberKey(), rmcatDm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(status != RoomStatus.NONE){
			statement = String.format("%s AND rm.%s=?", statement, dm.statusKey());
			params.add(status.id());
		}
		
		if(!category.isNone()){
			statement = String.format("%s AND cat.%s=?", statement, rmcatDm.keyName());
			params.add(category.id());
		}
		
		if(!StringUtils.isBlank(number)){
			statement = String.format("%s AND rm.%s=?", statement, dm.numberKey());
			params.add(number);
		}
		
		HorodateMetadata horodateDm = HorodateMetadata.create();
		String orderClause = String.format("ORDER BY rm.%s DESC", horodateDm.dateCreatedKey());
		
		String keyResult = String.format("rm.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);

	}

	@Override
	protected Room newOne(UUID id) {
		return new RoomDb(base, id, module);
	}

	@Override
	public Room none() {
		return new RoomNone();
	}

	@Override
	public Rooms of(RoomCategory category) throws IOException {
		return new RoomsDb(base, module, category, number, status);
	}

	@Override
	public Rooms withNumber(String number) throws IOException {
		return new RoomsDb(base, module, category, number, status);
	}

	@Override
	public Rooms withStatus(RoomStatus status) throws IOException {
		return new RoomsDb(base, module, category, number, status);
	}
}
