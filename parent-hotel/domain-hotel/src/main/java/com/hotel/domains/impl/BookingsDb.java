package com.hotel.domains.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.BookingMetadata;
import com.hotel.domains.api.BookingStatus;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.Guest;
import com.hotel.domains.api.Hotel;
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomCategoryMetadata;
import com.hotel.domains.api.RoomMetadata;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.Period;
import com.infrastructure.core.impl.PeriodBase;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.securities.api.Contact;
import com.securities.api.ContactNature;

public final class BookingsDb extends GuidKeyAdvancedQueryableDb<Booking, BookingMetadata> implements Bookings {

	private final transient Hotel module;
	private final transient Room room;
	private final transient Period period;
	private final transient LocalDate bookDate;
	private final transient Contact guest;
	private final transient Contact customer;
	
	public BookingsDb(final Base base, final Hotel module, Room room, Period period, LocalDate bookDate, Contact guest, Contact customer){
		super(base, "Réservation introuvable !");
		this.module = module;
		this.period = period;
		this.bookDate = bookDate;
		this.room = room;
		this.guest = guest;
		this.customer = customer;
	}
	
	@Override
	public Booking book(Contact customer, Contact guest, LocalDateTime start, LocalDateTime end, Contact seller) throws IOException {
		
		if (seller.isNone())
            throw new IllegalArgumentException("Vous devez indiquer un vendeur !");
		
		if(!module.sellers().contains(seller))
			throw new IllegalArgumentException("Vous devez être un vendeur pour effectuer cette action !");
		
		if (start == null) {
            throw new IllegalArgumentException("Invalid start date : it can't be empty!");
        }
		
		if (end == null) {
            throw new IllegalArgumentException("Invalid end date : it can't be empty !");
        }
		
		if(seller.isNone())
			throw new IllegalArgumentException("Le créateur de la commande doit être un vendeur !");
		
		if(customer.isNone())
			customer = module.contacts().defaultPerson();
		
		if(guest.isNone())
			guest = module.contacts().defaultPerson();
		
		if(guest.nature() != ContactNature.PERSON)
			throw new IllegalArgumentException("Vous devez indiquer une personne physique !");
		
		if(room.isNone())
			throw new IllegalArgumentException("Vous devez indiquer la chambre !");
		
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.startDateKey(), Timestamp.valueOf(start));	
		params.put(dm.endDateKey(), Timestamp.valueOf(end));
		params.put(dm.statusKey(), BookingStatus.NEW.id());
		params.put(dm.guestIdKey(), guest.id());
		params.put(dm.customerIdKey(), customer.id());
		params.put(dm.sellerIdKey(), seller.id());
		params.put(dm.roomIdKey(), room.id());
				
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		Booking booking = build(id);
		
		// Générer le devis
		booking.generateOrder();				
        
		return booking;
	}

	@Override
	public Bookings between(LocalDate start, LocalDate end) throws IOException {
		return new BookingsDb(base, module, room, new PeriodBase(start, end), null, guest, customer);		
	}

	@Override
	public Bookings at(LocalDate date) throws IOException {
		return new BookingsDb(base, module, room, period, date, guest, customer);
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
		double numberOfDays = java.time.Period.between(start, end).getDays() + 1;
		
		double numberOfOccupations = 0;
		for (int i = 0; i < numberOfDays; i++) {
			numberOfOccupations += at(start.plusDays(i)).count();
		}
		
		double numberOfRooms = module.rooms().count();
		
		if(numberOfRooms * numberOfDays == 0)
			return 0;
		
		return (numberOfOccupations / (numberOfRooms * numberOfDays));
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		RoomMetadata rmDm = RoomMetadata.create();
		RoomCategoryMetadata caDm = RoomCategoryMetadata.create();
		
		String statement = String.format("%s bk " +
				   						 "JOIN %s rm ON rm.%s = bk.%s " + 
				   						 "left JOIN %s ca ON ca.%s = rm.%s " +
			   							 "WHERE rm.%s ILIKE ? AND ca.%s=?",
			   							 dm.domainName(), 
			   							 rmDm.domainName(), rmDm.keyName(), dm.roomIdKey(),
			   							 caDm.domainName(), caDm.keyName(), rmDm.roomcategoryIdKey(),
			   							 rmDm.numberKey(), caDm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(!customer.isNone()){
			statement = String.format("%s AND bk.%s=?",statement, dm.customerIdKey());
			params.add(customer.id());
		}
		
		if(!guest.isNone()){
			statement = String.format("%s AND bk.%s=?",statement, dm.guestIdKey());
			params.add(guest.id());
		}
		
		if(period.isDefined()){
			statement = String.format("%s AND (bk.%s::date >= ? AND bk.%s::date <= ?)",statement, dm.startDateKey(), dm.endDateKey());
			params.add(java.sql.Date.valueOf(period.start()));
			params.add(java.sql.Date.valueOf(period.end()));
		}
		
		if(bookDate != null){
			statement = String.format("%s AND (bk.%s::date <= ? AND bk.%s::date >= ?)", statement, dm.startDateKey(), dm.endDateKey());
			params.add(java.sql.Date.valueOf(bookDate));
			params.add(java.sql.Date.valueOf(bookDate));
		}
		
		String orderClause = String.format("ORDER BY bk.%s DESC", dm.startDateKey());
		
		String keyResult = String.format("bk.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	protected Booking newOne(UUID id) {
		return new BookingDb(base, id, module);
	}

	@Override
	public Booking none() {
		return new BookingNone();
	}

	@Override
	public Bookings of(Room room) throws IOException {
		return new BookingsDb(base, module, room, period, bookDate, guest, customer);
	}

	@Override
	public Bookings ofGuest(Guest guest) throws IOException {
		return new BookingsDb(base, module, room, period, bookDate, guest, customer);
	}

	@Override
	public Bookings ofCustomer(Contact customer) throws IOException {
		return new BookingsDb(base, module, room, period, bookDate, guest, customer);
	}
}
