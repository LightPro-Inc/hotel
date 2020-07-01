package com.hotel.domains.impl;

import java.io.IOException;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJobs;
import com.infrastructure.core.GuidKeyEntityNone;
import com.securities.api.Company;
import com.securities.api.Contact;
import com.securities.api.ContactNature;
import com.securities.impl.ContactNone;

public final class MaidNone extends GuidKeyEntityNone<Maid> implements Maid {

	private final transient Contact origin;
	
	public MaidNone(){
		this.origin = new ContactNone(); 
	}
	
	@Override
	public Company company() throws IOException {
		return origin.company();
	}

	@Override
	public String fax() throws IOException {	
		return origin.fax();
	}

	@Override
	public String locationAddress() throws IOException {
		return origin.locationAddress();
	}

	@Override
	public String mail() throws IOException {
		return origin.mail();
	}

	@Override
	public String mobile() throws IOException {
		return origin.mobile();
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public ContactNature nature() throws IOException {
		return origin.nature();
	}

	@Override
	public String phone() throws IOException {
		return origin.phone();
	}

	@Override
	public String photo() throws IOException {
		return origin.photo();
	}

	@Override
	public String poBox() throws IOException {
		return origin.poBox();
	}

	@Override
	public void updateAddresses(String locationAddress, String phone, String mobile, String fax, String mail, String poBox, String webSite) throws IOException {
		origin.updateAddresses(locationAddress, phone, mobile, fax, mail, poBox, webSite);
	}

	@Override
	public String webSite() throws IOException {
		return origin.webSite();
	}

	@Override
	public Hotel hotel() throws IOException {
		return new HotelNone();
	}

	@Override
	public boolean active() throws IOException {
		return false;
	}

	@Override
	public void activate(boolean active) throws IOException {

	}

	@Override
	public MaidDayJobs daysJob() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}
}
