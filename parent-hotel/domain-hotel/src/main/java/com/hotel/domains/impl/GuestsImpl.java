package com.hotel.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.hotel.domains.api.Guest;
import com.hotel.domains.api.GuestMetadata;
import com.hotel.domains.api.Guests;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.securities.api.Person;
import com.securities.api.PersonMetadata;
import com.securities.api.Persons;
import com.securities.api.Sex;
import com.securities.impl.PersonImpl;
import com.securities.impl.PersonsImpl;

public class GuestsImpl implements Guests {

	private transient final Base base;
	private final transient GuestMetadata dm;
	private final transient DomainsStore ds;
	private final transient Persons persons;
	
	public GuestsImpl(final Base base){
		this.base = base;
		this.dm = GuestMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		persons = new PersonsImpl(base);
	}
	
	@Override
	public Guest add(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException {
		
		Person person = persons.add(firstName, lastName, sex, address, birthDate, tel1, tel2, email, photo);		
		ds.set(person.id(), new HashMap<String, Object>());
		
		return build(person.id());
	}

	@Override
	public List<Guest> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Guest> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Guest> find(int page, int pageSize, String filter) throws IOException {
		
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
		
		
		return ds.findDs(statement, params).stream()
										   .map(m -> build(m.key()))
										   .collect(Collectors.toList());		
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
	public Guest build(Object id) {
		return new GuestImpl(base, id);
	}

	@Override
	public boolean contains(Guest item) throws IOException {
		return ds.exists(item.id());
	}

	@Override
	public Guest transform(Person item) throws IOException {
		if(!item.isPresent())
			throw new IllegalArgumentException("La personne n'a pas été trouvée !");
		
		ds.set(item.id(), new HashMap<String, Object>());
		
		return build(item.id());
	}

	@Override
	public Guest get(Object id) throws IOException {
		Guest item = build(id);
		
		if(!item.isPresent())
			throw new IllegalArgumentException("Le hôte n'a pas été trouvé !");
		
		return item;
	}
}
