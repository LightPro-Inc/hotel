package com.hotel.domains.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.common.utilities.convert.UUIDConvert;
import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.GuestMetadata;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.RoomMetadata;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.securities.api.PersonMetadata;
import com.securities.impl.PersonImpl;

public class BookingsImpl implements Bookings {

	private transient final Base base;
	private final transient BookingMetadata dm;
	private final transient DomainsStore ds;
	private final transient Hotel module;
	
	public BookingsImpl(final Base base, final Hotel module){
		this.base = base;
		this.dm = BookingMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.module = module;
	}
	
	@Override
	public Booking book(UUID guestid, UUID roomid, LocalDateTime start, LocalDateTime end, double nightPriceApplied) throws IOException {
		
		if (start == null) {
            throw new IllegalArgumentException("Invalid start date : it can't be empty!");
        }
		
		if (end == null) {
            throw new IllegalArgumentException("Invalid end date : it can't be empty !");
        }
		
    	long numberOfDays = ChronoUnit.DAYS.between(start, end);
    	
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), Timestamp.valueOf(start));	
		params.put(dm.endDateKey(), Timestamp.valueOf(end));
		params.put(dm.statusKey(), BookingStatus.NEW.name());
		params.put(dm.nightPriceAppliedKey(), nightPriceApplied);
		params.put(dm.vatRateAppliedKey(), 0.18);
		params.put(dm.paidAmountKey(), 0);
		params.put(dm.guestIdKey(), guestid);
		params.put(dm.roomIdKey(), roomid);
		params.put(dm.ttcTotalBookingAmountKey(), numberOfDays * nightPriceApplied);
		params.put(dm.vatBookingAmountKey(), 0.18 * numberOfDays * nightPriceApplied);
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
        return build(id);
	}

	@Override
	public List<Booking> between(LocalDate start, LocalDate end) throws IOException {
		List<Booking> values = new ArrayList<Booking>();
		
		RoomCategoryMetadata caDm = RoomCategoryMetadata.create();
		RoomMetadata roDm = RoomMetadata.create();
		
		String statement = String.format("SELECT bk.%s FROM %s bk "
				+ "JOIN %s ro ON ro.%s=bk.%s "
				+ "left JOIN %s ca ON ca.%s=ro.%s "
				+ "WHERE (bk.%s::date >= ? AND bk.%s::date <= ?) AND ca.%s=? "
				+ "ORDER BY bk.%s", 
				dm.keyName(), dm.domainName(), 
				roDm.domainName(), roDm.keyName(), dm.roomIdKey(),
				caDm.domainName(), caDm.keyName(), roDm.roomcategoryIdKey(),
				dm.startDateKey(), dm.endDateKey(), caDm.moduleIdKey(),
				dm.startDateKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(module.id());
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}		
		
		return values;		
	}

	@Override
	public Booking get(UUID id) throws IOException {
		Booking item = build(id);
		
		if(!contains(item))
			throw new IllegalArgumentException("La réservation n'a pas été trouvée !");
		
		return item;
	}

	@Override
	public Guests guests() throws IOException {
		return new GuestsImpl(this.base, module.company());
	}

	@Override
	public List<Booking> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Booking> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Booking> find(int page, int pageSize, String filter) throws IOException {
		
		HorodateMetadata hm = HorodateImpl.dm();
		RoomMetadata rmDm = RoomMetadata.create();
		RoomCategoryMetadata caDm = RoomCategoryMetadata.create();
		GuestMetadata gtDm = GuestMetadata.create();
		PersonMetadata persDm = PersonImpl.dm();
		
		String statement = String.format("SELECT bk.%s FROM %s bk " +
				   						 "JOIN %s rm ON rm.%s = bk.%s " + 
				   						 "left JOIN %s ca ON ca.%s = rm.%s " +
			   							 "JOIN %s gt ON gt.%s = bk.%s " +
			   							 "left JOIN %s pers ON pers.%s = gt.%s " +
			   							 "WHERE (rm.%s ILIKE ? OR concat(pers.%s, ' ', pers.%s) ILIKE ?) AND ca.%s=? "
			   							 + "ORDER BY bk.%s DESC LIMIT ? OFFSET ?", 
			   							 dm.keyName(), dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 caDm.domainName(), caDm.keyName(), rmDm.roomcategoryIdKey(),
			   							 gtDm.domainName(), gtDm.keyName(), dm.guestIdKey(),
			   							 persDm.domainName(), persDm.keyName(), gtDm.keyName(),
			   							 rmDm.numberKey(), persDm.lastNameKey(), persDm.firstNameKey(), caDm.moduleIdKey(),
			   							 hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		return ds.findDs(statement, params).stream()
					  					   .map(m -> build(UUIDConvert.fromObject(m.key())))
				  					   	   .collect(Collectors.toList());
	}

	@Override
	public int totalCount(String filter) throws IOException {
		
		RoomMetadata rmDm = RoomMetadata.create();
		RoomCategoryMetadata caDm = RoomCategoryMetadata.create();
		GuestMetadata gtDm = GuestMetadata.create();
		PersonMetadata persDm = PersonImpl.dm();
		
		String statement = String.format("SELECT COUNT(bk.%s) FROM %s bk " +
				   						 "JOIN %s rm ON rm.%s = bk.%s " + 
				   						 "left JOIN %s ca ON ca.%s = rm.%s " +
			   							 "JOIN %s gt ON gt.%s = bk.%s " +
			   							 "left JOIN %s pers ON pers.%s = gt.%s " +
			   							 "WHERE (rm.%s ILIKE ? OR concat(pers.%s, ' ', pers.%s) ILIKE ?) AND ca.%s=? ",
			   							 dm.keyName(), dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 caDm.domainName(), caDm.keyName(), rmDm.roomcategoryIdKey(),
			   							 gtDm.domainName(), gtDm.keyName(), dm.guestIdKey(),
			   							 persDm.domainName(), persDm.keyName(), gtDm.keyName(),
			   							 rmDm.numberKey(), persDm.lastNameKey(), persDm.firstNameKey(), caDm.moduleIdKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Booking build(UUID id) {
		return new BookingImpl(base, id);
	}

	@Override
	public boolean contains(Booking item) {
		try {
			return ds.exists(item.id()) && item.room().category().module().isEqual(module);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Booking> at(LocalDate date) throws IOException {
		
		RoomCategoryMetadata caDm = RoomCategoryMetadata.create();
		RoomMetadata roDm = RoomMetadata.create();
		
		String statement = String.format("SELECT bk.%s FROM %s bk "
				+ "JOIN %s ro ON ro.%s=bk.%s "
				+ "left JOIN %s ca ON ca.%s=ro.%s "
				+ "WHERE (bk.%s::date <= ? AND bk.%s::date >= ?) AND ca.%s=? "
				+ "ORDER BY bk.%s", 
				dm.keyName(), dm.domainName(), 
				roDm.domainName(), roDm.keyName(), dm.roomIdKey(),
				caDm.domainName(), caDm.keyName(), roDm.roomcategoryIdKey(),
				dm.startDateKey(), dm.endDateKey(), caDm.moduleIdKey(),
				dm.startDateKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(date));
		params.add(java.sql.Date.valueOf(date));
		params.add(module.id());		
		
		return ds.find(statement, params)				 
				 .stream()
				 .map(m -> build(UUIDConvert.fromObject(m)))
				 .sorted((e1, e2) -> {
					try {
						return e1.room().number().compareTo(e2.room().number());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return 0;
				})
				 .collect(Collectors.toList());
	}

	@Override
	public double monthOccupationRate(LocalDate date) throws IOException {
		LocalDate start = date.withDayOfMonth(1);
		LocalDate end = date.withDayOfMonth(date.lengthOfMonth());
		
		return calculateOccupationRate(start, end);
	}

	@Override
	public double weekWorkDayOccupationRate(LocalDate date) throws IOException{
		LocalDate start = date.with(DayOfWeek.MONDAY);
		LocalDate end = date.with(DayOfWeek.THURSDAY);
		
		return calculateOccupationRate(start, end);
	}

	@Override
	public double weekendOccupationRate(LocalDate date) throws IOException{
		LocalDate start = date.with(DayOfWeek.FRIDAY);
		LocalDate end = date.with(DayOfWeek.SUNDAY);
		
		return calculateOccupationRate(start, end);
	}
	
	private double calculateOccupationRate(LocalDate start, LocalDate end) throws IOException {
		double numberOfDays = Period.between(start, end).getDays() + 1;
		
		double numberOfOccupations = 0;
		for (int i = 0; i < numberOfDays; i++) {
			numberOfOccupations += at(start.plusDays(i)).size();
		}
		
		double numberOfRooms = module.allRooms().all().size();
		
		return (numberOfOccupations / (numberOfRooms * numberOfDays));
	}

	@Override
	public void delete(Booking item) throws IOException {
		ds.delete(item.id());
	}
}
