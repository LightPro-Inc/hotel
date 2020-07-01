package com.lightpro.hotel.vm;

import java.io.IOException;
import java.util.UUID;

import com.hotel.domains.api.Maid;

public final class MaidVm {
	
	public final UUID id;
	public final String name;
	public final String locationAddress;
	public final String phone;
	public final String mobile;
	public final String fax;
	public final String mail;
	public final String poBox;
	public final String webSite;
	public final String photo;
	public final int natureId;
	public final String nature;	
	public boolean active;
	
	public MaidVm() {
        throw new UnsupportedOperationException("#MaidVm()");
    }
	
	public MaidVm(final Maid contact) {
		try {
			this.id = contact.id();
	        this.name = contact.name();
	        this.locationAddress = contact.locationAddress();
	        this.phone = contact.phone();
	        this.mobile = contact.mobile();
	        this.fax = contact.fax();
	        this.mail = contact.mail();
	        this.poBox = contact.poBox();
	        this.webSite = contact.webSite();
	        this.photo = contact.photo();
	        this.natureId = contact.nature().id();
	        this.nature = contact.nature().toString();
	        this.active = contact.active();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	        
    }
}
