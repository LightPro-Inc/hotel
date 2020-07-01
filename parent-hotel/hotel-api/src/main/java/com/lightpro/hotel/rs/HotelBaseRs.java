package com.lightpro.hotel.rs;

import java.io.IOException;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.impl.HotelDb;
import com.securities.api.BaseRs;
import com.securities.api.Module;
import com.securities.api.ModuleType;

public class HotelBaseRs extends BaseRs {

	public HotelBaseRs() {
		super(ModuleType.HOTEL);
	}

	protected Hotel hotel() throws IOException {
		return hotel(currentModule);
	}
	
	protected Hotel hotel(Module module) throws IOException {
		return new HotelDb(base, module);
	}
}
