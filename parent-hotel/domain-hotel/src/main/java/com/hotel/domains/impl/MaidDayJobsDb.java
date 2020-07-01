package com.hotel.domains.impl;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.MaidDayJobMetadata;
import com.hotel.domains.api.MaidDayJobStatus;
import com.hotel.domains.api.MaidDayJobs;
import com.hotel.domains.api.MaidMetadata;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.Period;
import com.infrastructure.core.impl.PeriodBase;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.securities.api.ContactMetadata;

public final class MaidDayJobsDb extends GuidKeyAdvancedQueryableDb<MaidDayJob, MaidDayJobMetadata> implements MaidDayJobs {

	private final transient Hotel module;
	private final transient Maid maid;
	private final transient Period period;
	
	public MaidDayJobsDb(final Base base, final Hotel module, final Maid maid, final Period period){
		super(base, "Plannification introuvable !");
		this.module = module;
		this.maid = maid;
		this.period = period;
	}
	
	@Override
	public MaidDayJob add(LocalDate date) throws IOException {
		
		if (date == null) {
            throw new IllegalArgumentException("Vous devez saisir une valeur !");
        }
		
		if (maid.isNone()) {
            throw new IllegalArgumentException("Vous devez indiquer un employé !");
        }
    	
		boolean alreadyExists = false;
		
		try {
			get(date);
			alreadyExists = true;			
		} catch (Exception e) {
			
		}
		
		if(alreadyExists)
			throw new IllegalArgumentException("Plannification invalide : l'employé a déjà été planifié pour ce jour !");
		
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.dayKey(), Date.valueOf(date));	
		params.put(dm.statusKey(), MaidDayJobStatus.PLANNED.name());
		params.put(dm.maidIdKey(), maid.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
        return build(id);
	}

	@Override
	public MaidDayJobs between(LocalDate start, LocalDate end) throws IOException {
		return new MaidDayJobsDb(base, module, maid, new PeriodBase(start, end));
	}

	@Override
	public MaidDayJob get(LocalDate date) throws IOException {
	
		MaidDayJobs results = between(date, date);
		if(results.isEmpty())
			throw new IllegalArgumentException("Aucune planification de l'employé pour ce jour !");			
		else
			return results.first();
	}

	@Override
	protected MaidDayJob newOne(UUID id) {
		return new MaidDayJobDb(base, id, module);
	}

	@Override
	public MaidDayJob none() {
		return new MaidDayJobNone();
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		MaidMetadata dmMd = MaidMetadata.create();
		ContactMetadata persDm = ContactMetadata.create();
		String statement = String.format("%s mdj "
										+ "JOIN %s md ON md.%s=mdj.%s "
										+ "LEFT JOIN view_contacts vctc ON vctc.%s=md.%s "
										+ "WHERE (vctc.name1 ILIKE ?  OR vctc.name2 ILIKE ?) AND md.%s=? ",
										dm.domainName(), 
										dmMd.domainName(), dmMd.keyName(), dm.maidIdKey(),
										persDm.keyName(), dmMd.keyName(),
										dmMd.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(period.isDefined()){
			statement = String.format("%s AND (mdj.%s >= ? AND mdj.%s <= ?)", statement, dm.dayKey(), dm.dayKey());
			params.add(java.sql.Date.valueOf(period.start()));
			params.add(java.sql.Date.valueOf(period.end()));
		}
		
		if(!maid.isNone()) {
			statement = String.format("%s AND md.%s=?", statement, dmMd.keyName());
			params.add(maid.id());
		}
		
		String orderClause = String.format("ORDER BY mdj.%s ASC", dm.dayKey());
		
		String keyResult = String.format("mdj.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	public MaidDayJobs of(Maid maid) throws IOException {
		return new MaidDayJobsDb(base, module, maid, period);
	}
}
