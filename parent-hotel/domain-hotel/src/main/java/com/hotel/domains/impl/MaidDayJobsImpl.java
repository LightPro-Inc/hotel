package com.hotel.domains.impl;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.MaidDayJobMetadata;
import com.hotel.domains.api.MaidDayJobStatus;
import com.hotel.domains.api.MaidDayJobs;
import com.hotel.domains.api.MaidMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.securities.api.Company;
import com.securities.api.PersonMetadata;
import com.securities.impl.PersonImpl;

public class MaidDayJobsImpl implements MaidDayJobs {

	private transient final Base base;
	private final transient MaidDayJobMetadata dm;
	private final transient DomainsStore ds;
	private final transient Company company;
	
	public MaidDayJobsImpl(final Base base, final Company company){
		this.base = base;
		this.dm = MaidDayJobMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.company = company;
	}
	
	@Override
	public MaidDayJob plan(LocalDate date, Maid maid) throws IOException {
		
		if (date == null) {
            throw new IllegalArgumentException("Date invalide : vous devez saisir une valeur !");
        }
		
		if (!maid.isPresent()) {
            throw new IllegalArgumentException("Employé invalide : vous devez en choisir un !");
        }
    	
		boolean alreadyExists = false;
		
		try {
			get(date, maid);
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
	public List<MaidDayJob> between(LocalDate start, LocalDate end) throws IOException {

		MaidMetadata dmMd = MaidMetadata.create();
		PersonMetadata dmPers = PersonImpl.dm();
		String statement = String.format( "SELECT mdj.%s FROM %s mdj "
										+ "JOIN %s md ON md.%s=mdj.%s "
										+ "left JOIN %s pers ON pers.%s=md.%s "
										+ "WHERE (mdj.%s >= ? AND mdj.%s <= ?) AND pers.%s=? "
										+ "ORDER BY mdj.%s ASC", 
										dm.keyName(), dm.domainName(), 
										dmMd.domainName(), dmMd.keyName(), dm.maidIdKey(),
										dmPers.domainName(), dmPers.keyName(), dmMd.keyName(),
										dm.dayKey(), dm.dayKey(), dmPers.companyIdKey(),
										dm.dayKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(company.id());
		
		return ds.find(statement, params)
				 .stream()
				 .map(m -> build(UUIDConvert.fromObject(m)))
				 .collect(Collectors.toList());	
	}

	@Override
	public MaidDayJob build(UUID id) {
		return new MaidDayJobImpl(base, id);
	}

	@Override
	public MaidDayJob get(LocalDate date, Maid maid) throws IOException {
		
		Optional<MaidDayJob> result = between(date, date).stream().
									 filter(m -> {
										try {
											return m.maid().isEqual(maid);
										} catch (IOException e) {
											e.printStackTrace();
										}
										return false;
									}).findFirst();
		
		if(result.isPresent())
			return result.get();
		else
			throw new IllegalArgumentException("Aucune planification de l'employé pour ce jour !"); 
	}

	@Override
	public MaidDayJob get(UUID id) throws IOException {
		MaidDayJob item = build(id);
		
		if(!item.isPresent())
			throw new NotFoundException("Le jour de travail n'a pas été trouvé !");
		
		return item;
	}

	@Override
	public void delete(MaidDayJob item) throws IOException {
		ds.delete(item.id());
	}
}
