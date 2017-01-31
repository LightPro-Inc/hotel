package com.hotel.domains.impl;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.Guest;
import com.hotel.domains.api.GuestMetadata;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.Queryable;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.securities.api.Person;
import com.securities.api.Sex;
import com.securities.impl.PersonImpl;

public class GuestImpl implements Guest {

	private final transient Base base;
	private final transient UUID id;
	private final transient GuestMetadata dm;
	private final transient DomainStore ds;
	private final transient Person identity;
	
	public GuestImpl(final Base base, final UUID id){
		this.base = base;
		this.id = id;
		this.dm = GuestMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
		this.identity = new PersonImpl(base, id);
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent() {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public String firstName() throws IOException {
		return this.identity.firstName();
	}

	@Override
	public String lastName() throws IOException {
		return this.identity.lastName();
	}

	@Override
	public String fullName() throws IOException {
		return this.identity.fullName();
	}

	@Override
	public Sex sex() throws IOException {
		return this.identity.sex();
	}

	@Override
	public Queryable<Booking, UUID> bookings() throws IOException {
		return new BookingsOfGuestImpl(this.base, this.id);
	}

	@Override
	public String address() throws IOException {
		return identity.address();
	}

	@Override
	public Date birthDate() throws IOException {
		return identity.birthDate();
	}

	@Override
	public String tel1() throws IOException {
		return identity.tel1();
	}

	@Override
	public String tel2() throws IOException {
		return identity.tel2();
	}

	@Override
	public String email() throws IOException {
		return identity.email();
	}

	@Override
	public String photo() throws IOException {
		return identity.photo();
	}

	@Override
	public void update(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException {
		this.identity.update(firstName, lastName, sex, address, birthDate, tel1, tel2, email, photo);	
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
