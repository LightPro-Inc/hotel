package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidMetadata;
import com.hotel.domains.api.MaidStatus;
import com.hotel.domains.api.Maids;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.securities.api.Contact;
import com.securities.api.ContactMetadata;
import com.securities.api.ContactNature;

public final class MaidsDb extends GuidKeyAdvancedQueryableDb<Maid, MaidMetadata> implements Maids {

	private final transient Hotel module;
	private final transient MaidStatus status;
	
	public MaidsDb(final Base base, final Hotel module, final MaidStatus status){
		super(base, "Personne de chambre introuvable !");
		this.module = module;
		this.status = status;
	}

	@Override
	public Maids withStatus(MaidStatus status) throws IOException {
		return new MaidsDb(base, module, status);
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
		
		if(status == MaidStatus.ACTIVE)
			statement = String.format("%s AND md.%s IS TRUE", statement, dm.activeKey());
		
		if(status == MaidStatus.UNACTIVE)
			statement = String.format("%s AND md.%s IS FALSE", statement, dm.activeKey());
		
		HorodateMetadata horodateDm = HorodateMetadata.create();
		String orderClause = String.format("ORDER BY md.%s DESC", horodateDm.dateCreatedKey());
		
		String keyResult = String.format("md.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);

	}

	@Override
	protected Maid newOne(UUID id) {
		return new MaidDb(base, id, module);
	}

	@Override
	public Maid none() {
		return new MaidNone();
	}

	@Override
	public Maid add(Contact contact) throws IOException {
		
		if(contact.isNone())
			throw new IllegalArgumentException("Vous devez indiquer une personne !");
		
		if(contact.nature() != ContactNature.PERSON)
			throw new IllegalArgumentException("Vous devez indiquer une personne physique !");
		
		if(contains(contact))
			throw new IllegalArgumentException("Cette personne a déjà été ajoutée !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.moduleIdKey(), module.id());
		ds.set(contact.id(), params);
		
		return build(contact.id());
	}

	@Override
	public boolean contains(Contact contact) throws IOException {
		Maid maid = build(contact.id());
		
		if(maid.isNone())
			return false;
		
		return contains(maid);
	}
}
