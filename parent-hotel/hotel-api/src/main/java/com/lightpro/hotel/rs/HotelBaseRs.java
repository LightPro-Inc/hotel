package com.lightpro.hotel.rs;

import com.hotel.domains.api.Hotel;
import com.hotel.domains.impl.HotelImpl;
import com.infrastructure.core.BaseRs;

public class HotelBaseRs extends BaseRs {
	protected Hotel hotel(){
		return new HotelImpl(base);
	}
}
