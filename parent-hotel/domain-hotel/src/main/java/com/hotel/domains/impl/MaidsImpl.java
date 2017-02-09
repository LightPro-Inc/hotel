package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidMetadata;
import com.hotel.domains.api.Maids;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.securities.api.Company;
import com.securities.api.Person;
import com.securities.api.PersonMetadata;
import com.securities.api.Persons;
import com.securities.api.Sex;
import com.securities.impl.PersonImpl;
import com.securities.impl.PersonsImpl;

public class MaidsImpl implements Maids {

	private final transient Base base;
	private final transient MaidMetadata dm;
	private final transient DomainsStore ds;
	private final transient Persons persons;
	private final transient Company company;
	
	public MaidsImpl(final Base base, final Company company){
		this.base = base;		
		this.dm = MaidMetadata.create();
		this.ds = base.domainsStore(dm);
		this.company = company;
		persons = new PersonsImpl(base, company);
	}
	
	@Override
	public List<Maid> all() throws IOException {
		List<Maid> values = find(0, 0, "");
		
		return values.stream()
				     .sorted((e1, e2) -> {
						try {
							return e1.lastName().compareTo(e2.lastName());
						} catch (IOException e) {
							e.printStackTrace();
						}
						return 0;
					})
				     .collect(Collectors.toList());
	}

	@Override
	public Maid build(UUID id) {
		return new MaidImpl(base, id);
	}

	@Override
	public boolean contains(Maid item) {
		return item.isPresent() && persons.contains(item);
	}

	@Override
	public List<Maid> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Maid> find(int page, int pageSize, String filter) throws IOException {
		List<Maid> values = new ArrayList<Maid>();
		
		PersonMetadata personDm = PersonImpl.dm();
		String statement = String.format("SELECT md.%s FROM %s md "
				+ "JOIN %s pers ON pers.%s=md.%s "
				+ "WHERE (concat(pers.%s,' ', pers.%s) ILIKE ?  OR concat(pers.%s, ' ', pers.%s) ILIKE ?) AND pers.%s=? "
				+ "ORDER BY md.%s DESC LIMIT ? OFFSET ?", 
				dm.keyName(), dm.domainName(), 
				personDm.domainName(), personDm.keyName(), dm.keyName(),
				personDm.firstNameKey(), personDm.lastNameKey(), personDm.lastNameKey(), personDm.firstNameKey(), personDm.companyIdKey(),
				HorodateImpl.dm().dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(company.id());
		
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
		PersonMetadata personDm = PersonImpl.dm();
		String statement = String.format("SELECT COUNT(md.%s) FROM %s md "
				+ "JOIN %s pers ON pers.%s=md.%s "
				+ "WHERE (concat(pers.%s,' ', pers.%s) ILIKE ?  OR concat(pers.%s, ' ', pers.%s) ILIKE ?) AND pers.%s=? ",
				dm.keyName(), dm.domainName(), 
				personDm.domainName(), personDm.keyName(), dm.keyName(),
				personDm.firstNameKey(), personDm.lastNameKey(), personDm.lastNameKey(), personDm.firstNameKey(), personDm.companyIdKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(company.id());
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Maid add(String firstName, String lastName, Sex sex, String address, LocalDate birthDate, String tel1, String tel2, String email, String photo) throws IOException {
		Person person = persons.add(firstName, lastName, sex, address, birthDate, tel1, tel2, email, photo);		
		ds.set(person.id(), new HashMap<String, Object>());
		
		return build(person.id());
	}

	@Override
	public void delete(Maid item) throws IOException {
		if(contains(item)){
			ds.delete(item.id());
			persons.delete(item);
		}
	}

	@Override
	public Maid get(UUID id) throws IOException {
		Maid item = build(id);
		
		if(!contains(item))
			throw new NotFoundException("Le client n'a pas été trouvé !");
		
		return item;
	}

	@Override
	public List<Maid> actives() throws IOException {
		return all().stream()
				    .filter(m -> {
						try {
							return m.active();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return false;
					})
				    .collect(Collectors.toList());
	}
}
