package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Guest;
import com.hotel.domains.api.GuestMetadata;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.Hotel;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.securities.api.Contact;
import com.securities.api.ContactMetadata;

public final class GuestsDb extends GuidKeyAdvancedQueryableDb<Guest, GuestMetadata> implements Guests {

	private final transient Hotel module;
	
	public GuestsDb(final Base base, final Hotel module){
		super(base, "Hôte introuvable !");	
		this.module = module;
	}

	@Override
	public Guest add(Contact item) throws IOException {
		
		if(item.isNone())
			throw new IllegalArgumentException("Vous devez indiquer une personne !");
		
		if(module.guests().contains(item))
			throw new IllegalArgumentException("L'hôte a déjà été ajouté !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.moduleIdKey(), module.id());
		ds.set(item.id(), params);
		
		return build(item.id());
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		ContactMetadata persDm = ContactMetadata.create();
		String statement = String.format("%s md "
				+ "JOIN view_contacts vctc ON vctc.%s=md.%s "
				+ "WHERE (vctc.name1 ILIKE ?  OR vctc.name2 ILIKE ?) AND md.%s=? ",
				dm.domainName(),
				persDm.keyName(), dm.keyName(),
				dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		HorodateMetadata horodateDm = HorodateMetadata.create();
		String orderClause = String.format("ORDER BY md.%s DESC", horodateDm.dateCreatedKey());
		
		String keyResult = String.format("md.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	protected Guest newOne(UUID id) {
		return new GuestDb(base, id, module);
	}

	@Override
	public Guest none() {
		return new GuestNone();
	}

	@Override
	public boolean contains(Contact item) throws IOException {
		Guest guest = build(item.id());
		
		if(guest.isNone())
			return false;
		
		return contains(guest);
	}
}
