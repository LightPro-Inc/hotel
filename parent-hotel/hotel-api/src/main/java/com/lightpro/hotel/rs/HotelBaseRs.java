package com.lightpro.hotel.rs;

import java.io.IOException;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.impl.HotelImpl;
import com.securities.api.BaseRs;
import com.securities.api.Module;
import com.securities.api.ModuleType;

public class HotelBaseRs extends BaseRs {
	protected Hotel hotel() throws IOException {
		Module module = currentCompany().modules().get(ModuleType.HOTEL);
		return new HotelImpl(base(), module.id());
	}
}
