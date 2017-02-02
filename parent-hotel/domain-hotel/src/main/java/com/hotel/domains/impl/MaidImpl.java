package com.hotel.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidMetadata;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.securities.api.Person;
import com.securities.api.Sex;
import com.securities.impl.PersonImpl;

public class MaidImpl implements Maid {

	private final transient Base base;
	private final transient Person person;
	private final transient MaidMetadata dm;
	private final transient DomainStore ds;	
	
	public MaidImpl(final Base base, final UUID id){
		this.base = base;
		this.dm = MaidMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
		this.person = new PersonImpl(this.base, id);
	}
	
	@Override
	public String firstName() throws IOException {
		return person.firstName();
	}

	@Override
	public String fullName() throws IOException {
		return person.fullName();
	}

	@Override
	public String lastName() throws IOException {
		return person.lastName();
	}

	@Override
	public Sex sex() throws IOException {
		return person.sex();
	}

	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return person.id();
	}

	@Override
	public boolean isPresent() {
		return person.isPresent();
	}

	@Override
	public String address() throws IOException {
		return person.address();
	}

	@Override
	public LocalDate birthDate() throws IOException {
		return person.birthDate();
	}

	@Override
	public String tel1() throws IOException {
		return person.tel1();
	}

	@Override
	public String tel2() throws IOException {
		return person.tel2();
	}

	@Override
	public String email() throws IOException {
		return person.email();
	}

	@Override
	public String photo() throws IOException {
		return person.photo();
	}

	@Override
	public void update(String firstName, String lastName, Sex sex, String address, LocalDate birthDate, String tel1, String tel2, String email, String photo) throws IOException {
		this.person.update(firstName, lastName, sex, address, birthDate, tel1, tel2, email, photo);	
	}

	@Override
	public boolean active() throws IOException {
		return ds.get(dm.activeKey());
	}

	@Override
	public void activate(boolean active) throws IOException {
		ds.set(dm.activeKey(), active);		
	}
	
	@Override
	public boolean isEqual(Person item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(Person item) {
		return !isEqual(item);
	}

}
