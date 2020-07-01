package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomCategory;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;

public final class RoomCategoriesDb extends GuidKeyAdvancedQueryableDb<RoomCategory, RoomCategoryMetadata> implements RoomCategories {

	private final transient Hotel module;
	
	public RoomCategoriesDb(final Base base, final Hotel module){
		super(base, "Catégorie de chambre introuvable !");
		this.module = module;
	}
	
	@Override
	public RoomCategory add(String name, int capacity, double nightPrice) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Vous devez définir le libellé de la catégorie !");
        }
		
		if (capacity == 0) {
            throw new IllegalArgumentException("La capacité d'accueil doit être supérieure à 0 !");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.capacityKey(), capacity);
		params.put(dm.nightPriceKey(), nightPrice);
		params.put(dm.moduleIdKey(), module.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		RoomCategory category = build(id); 
		module.salesInterface().syncProduct(category);
        
		return category;    
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		String statement = String.format("%s cat "
				+ "WHERE cat.%s ILIKE ? AND cat.%s=?",
				dm.domainName(), 
				dm.nameKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		HorodateMetadata horodateDm = HorodateMetadata.create();
		String orderClause = String.format("ORDER BY cat.%s DESC", horodateDm.dateCreatedKey());
		
		String keyResult = String.format("cat.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);

	}

	@Override
	protected RoomCategory newOne(UUID id) {
		return new RoomCategoryDb(base, id, module);
	}

	@Override
	public RoomCategory none() {
		return new RoomCategoryNone();
	}
}
