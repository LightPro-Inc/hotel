package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidMetadata;
import com.hotel.domains.api.Maids;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
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
	
	public MaidsImpl(final Base base){
		this.base = base;		
		this.dm = MaidMetadata.create();
		this.ds = base.domainsStore(dm);
		persons = new PersonsImpl(base);
	}
	
	@Override
	public List<Maid> all() throws IOException {
		List<Maid> values = new ArrayList<Maid>();
		
		List<DomainStore> results = ds.getAll();
		for (DomainStore domainStore : results) {
			values.add(build(domainStore.key())); 
		}		
		
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
	public Maid build(Object id) {
		return new MaidImpl(base, id);
	}

	@Override
	public boolean contains(Maid item) throws IOException {
		return ds.exists(item.id());
	}

	@Override
	public List<Maid> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Maid> find(int page, int pageSize, String filter) throws IOException {
		List<Maid> values = new ArrayList<Maid>();
		
		PersonMetadata personDm = PersonImpl.dm();
		String statement = String.format("SELECT %s FROM %s WHERE %s IN (SELECT %s FROM %s WHERE concat(%s,' ', %s) ILIKE ?  OR concat(%s, ' ', %s) ILIKE ?) ORDER BY %s DESC LIMIT ? OFFSET ?", dm.keyName(), dm.domainName(), dm.keyName(), personDm.keyName(), personDm.domainName(), personDm.firstNameKey(), personDm.lastNameKey(), personDm.lastNameKey(), personDm.firstNameKey(), HorodateImpl.dm().dateCreatedKey());
		
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
		PersonMetadata personDm = PersonImpl.dm();
		String statement = String.format("SELECT COUNT(%s) FROM %s WHERE %s IN (SELECT %s FROM %s WHERE concat(%s,' ', %s) ILIKE ?  OR concat(%s, ' ', %s) ILIKE ?)", dm.keyName(), dm.domainName(), dm.keyName(), personDm.keyName(), personDm.domainName(), personDm.firstNameKey(), personDm.lastNameKey(), personDm.lastNameKey(), personDm.firstNameKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Maid add(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException {
		Person person = persons.add(firstName, lastName, sex, address, birthDate, tel1, tel2, email, photo);		
		ds.set(person.id(), new HashMap<String, Object>());
		
		return build(person.id());
	}

	@Override
	public void delete(Maid item) throws IOException {
		ds.delete(item.id());
		persons.delete(item);
	}

	@Override
	public Maid get(UUID id) throws IOException {
		Maid item = build(id);
		
		if(!item.isPresent())
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
