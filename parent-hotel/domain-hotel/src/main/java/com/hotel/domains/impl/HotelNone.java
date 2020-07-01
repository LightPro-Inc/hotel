package com.hotel.domains.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.DayOccupations;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.MaidDayJobs;
import com.hotel.domains.api.Maids;
import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomFloor;
import com.hotel.domains.api.Rooms;
import com.hotel.domains.api.SalesInterface;
import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Sellers;
import com.securities.api.Company;
import com.securities.api.Contacts;
import com.securities.api.Feature;
import com.securities.api.FeatureSubscribed;
import com.securities.api.Features;
import com.securities.api.Indicators;
import com.securities.api.Log;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.impl.CompanyNone;

public final class HotelNone extends GuidKeyEntityNone<Hotel> implements Hotel {

	@Override
	public void activate(boolean arg0) throws IOException {

	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public Features featuresProposed() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Module install() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isInstalled() {
		return false;
	}

	@Override
	public boolean isSubscribed() {
		return false;
	}

	@Override
	public String name() throws IOException {
		return null;
	}

	@Override
	public int order() throws IOException {
		return 0;
	}

	@Override
	public String shortName() throws IOException {
		return null;
	}

	@Override
	public ModuleType type() throws IOException {
		return ModuleType.NONE;
	}

	@Override
	public Module uninstall() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Company company() throws IOException {
		return new CompanyNone();
	}

	@Override
	public RoomCategories roomCategories() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Rooms rooms() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public List<RoomFloor> roomFloors() throws IOException {
		return Arrays.asList();
	}

	@Override
	public Bookings bookings() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public DayOccupations dayOccupations() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Maids maids() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public MaidDayJobs maidDayJobs() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Features featuresAvailable() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Features featuresSubscribed() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Indicators indicators() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Module subscribe() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public FeatureSubscribed subscribeTo(Feature arg0) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Module unsubscribe() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void unsubscribeTo(Feature arg0) throws IOException {
		
	}

	@Override
	public Guests guests() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Contacts contacts() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public SalesInterface salesInterface() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public PurchaseOrders orders() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Sellers sellers() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Log log() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
