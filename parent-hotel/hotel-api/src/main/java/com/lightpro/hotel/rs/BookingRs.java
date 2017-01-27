package com.lightpro.hotel.rs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.hotel.domains.api.Booking;
import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.DayOccupation;
import com.hotel.domains.api.DayOccupationStatus;
import com.hotel.domains.api.Guest;
import com.hotel.domains.api.Guests;
import com.hotel.domains.api.RoomCategories;
import com.hotel.domains.api.RoomCategory;
import com.lightpro.hotel.cmd.BookingEdit;
import com.lightpro.hotel.cmd.BookingMoved;
import com.lightpro.hotel.cmd.BookingPeriod;
import com.lightpro.hotel.cmd.BookingResized;
import com.lightpro.hotel.cmd.GuestEdit;
import com.lightpro.hotel.vm.BookingVm;
import com.lightpro.hotel.vm.DayOccupationVm;
import com.lightpro.hotel.vm.GuestVm;
import com.lightpro.hotel.vm.LocationStat;
import com.lightpro.hotel.vm.RateOccupation;
import com.lightpro.hotel.vm.ResumeLocationStat;
import com.securities.api.Person;
import com.infrastructure.core.PaginationSet;

@Path("/booking")
public class BookingRs extends HotelBaseRs {
			
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBooking(@PathParam("id") UUID id) throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						
						if(booking == null)
							return Response.status(Status.NOT_FOUND).build();
						
						BookingVm vm = new BookingVm(booking);
						
						return Response.ok(vm).build();
					}
				});
	}
	
	@GET
	@Path("/resumeStat")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getResumeStat() throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<LocationStat> stats = new ArrayList<LocationStat>();
						
						RoomCategories roomCategories = hotel().roomCategories();
						
						for (RoomCategory rc : roomCategories.all()) {
							
							double amount = 0;							
							for (Booking bk : rc.bookings().all()) {
								amount += bk.ttcTotalBookingAmount();
							}
							
							LocationStat stat = new LocationStat(rc.name(), amount);
							stats.add(stat);
						}
						
						return Response.ok(new ResumeLocationStat(stats)).build();
					}
				});
	}
	
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBookings(@QueryParam("page") int page, 
								@QueryParam("pageSize") int pageSize, 
								@QueryParam("filter") String filter) throws IOException {
						
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						String filterLocal = (filter == null) ? "" : filter;
						
						List<BookingVm> rooms =  hotel().bookings().find(page, pageSize, filterLocal)
												   .stream()
												   .map(m -> new BookingVm(m))
												   .collect(Collectors.toList());
						
						int count = hotel().bookings().totalCount(filter);
						PaginationSet<BookingVm> pagedSet = new PaginationSet<BookingVm>(rooms, page, count);
						return Response.ok(pagedSet).build();
					}
				});		
	}
	
	@GET
	@Path("/{id}/guest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBookingGuest(@PathParam("id") UUID id) throws IOException {			
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						
						if(booking.guest().id() == null)
							return Response.status(Status.NOT_FOUND).build();
							
						return Response.ok(new GuestVm(booking.guest())).build();
					}
				});	
	}
	
	@GET
	@Path("/day-occupation")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDayOccupations() throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<DayOccupation> items = hotel().dayOccupations().of(LocalDate.now());
						
						List<DayOccupationVm> itemsVm = items.stream()
															 .map(m -> new DayOccupationVm(m))
															 .collect(Collectors.toList());
						
						return Response.ok(itemsVm).build();
					}
				});
	}
	
	@GET
	@Path("/day-occupation/client-en-recouche")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDayOccupationsClientEnRechouche() throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<DayOccupation> items = hotel().dayOccupations().of(LocalDate.now());
						
						List<DayOccupationVm> itemsVm = items.stream()
															 .filter(m -> {
																try {
																	return m.status() == DayOccupationStatus.CUSTOMER_RECOUCHE;
																} catch (IOException e) {
																	e.printStackTrace();
																}
																return false;
															})
															 .map(m -> new DayOccupationVm(m))
															 .collect(Collectors.toList());
						
						return Response.ok(itemsVm).build();
					}
				});
	}
	
	@GET
	@Path("/day-occupation/client-attendu")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDayOccupationsClientAttendus() throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<DayOccupation> items = hotel().dayOccupations().of(LocalDate.now());
						
						List<DayOccupationVm> itemsVm = items.stream()
															 .filter(m -> {
																try {
																	return m.status() == DayOccupationStatus.CUSTOMER_WAITED;
																} catch (IOException e) {
																	e.printStackTrace();
																}
																return false;
															})
															 .map(m -> new DayOccupationVm(m))
															 .collect(Collectors.toList());
						
						return Response.ok(itemsVm).build();
					}
				});
	}
	
	@GET
	@Path("/day-occupation/client-arrive")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDayOccupationsClientArrives() throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<DayOccupation> items = hotel().dayOccupations().of(LocalDate.now());
						
						List<DayOccupationVm> itemsVm = items.stream()
															 .filter(m -> {
																try {
																	return m.status() == DayOccupationStatus.CUSTOMER_ARRIVED;
																} catch (IOException e) {
																	e.printStackTrace();
																}
																return false;
															})
															 .map(m -> new DayOccupationVm(m))
															 .collect(Collectors.toList());
						
						return Response.ok(itemsVm).build();
					}
				});
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBookings(BookingPeriod  period) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<BookingVm> bookingsVm = hotel().bookings().between(period.start(), period.end())
							   	  .stream()
							      .map(m -> new BookingVm(m))
							      .collect(Collectors.toList());
	
						return Response.ok(bookingsVm).build();
					}
				});			
	}
	
	@POST
	@Path("/{id}/guest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response identifyGuest(@PathParam("id") UUID id, GuestEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Bookings bookings = hotel().bookings();
						Guest guest = bookings.guests().build(data.id());
						
						// 1 - enregistrer l'hote
						if(!guest.isPresent()) {
							// nouvel hote
							Person person = hotel().company().persons().build(data.id());
							if(person.isPresent()){
								guest = bookings.guests().transform(person);
							}else{
								guest = bookings.guests().add(data.firstName(), data.lastName(), data.sex(), data.address(), data.birthDate(), data.tel1(), data.tel2(), data.email(), data.photo());
							}							
						}else {
							// mettre les données de l'hote ce jour
							guest.update(data.firstName(), data.lastName(), data.sex(), data.address(), data.birthDate(), data.tel1(), data.tel2(), data.email(), data.photo());							
						}
						
						// 2 - attribuer la reservation a l'hote
						Booking booking = bookings.get(id);		
						booking.identifyGuest(guest.id());
													
						return Response.ok(new GuestVm(guest)).build();
					}
				});				
	}
	
	@POST
	@Path("/{id}/move")
	@Produces({MediaType.APPLICATION_JSON})
	public Response move(@PathParam("id") UUID id, BookingMoved bm) throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.move(bm.newStart(), bm.newEnd(), bm.newRoomId());
							
						return Response.noContent().build();
					}
				});	
	}
	
	@POST
	@Path("/{id}/resize")
	@Produces({MediaType.APPLICATION_JSON})
	public Response resize(@PathParam("id") UUID id, BookingResized br) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.resize(br.newStart(), br.newEnd());
							
						return Response.noContent().build();
					}
				});			
	}	
	
	@POST
	@Path("/{id}/confirm")
	@Produces({MediaType.APPLICATION_JSON})
	public Response confirm(@PathParam("id") UUID id) {
						
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.confirm();

						return Response.ok(new BookingVm(booking)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/cancel")
	@Produces({MediaType.APPLICATION_JSON})
	public Response cancel(@PathParam("id") UUID id) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						hotel().bookings().get(id).cancel();
						
						return Response.noContent().build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/checkIn")
	@Produces({MediaType.APPLICATION_JSON})
	public Response checkIn(@PathParam("id") UUID id) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						hotel().bookings().get(id).checkIn();
						
						return Response.noContent().build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/checkOut")
	@Produces({MediaType.APPLICATION_JSON})
	public Response checkOut(@PathParam("id") UUID id) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						hotel().bookings().get(id).checkOut();
						
						return Response.noContent().build();
					}
				});		
	}
	
	@POST
	@Path("/rate-occupation/month")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getMonthRateOccupation(final LocalDateTime date) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						double rate = hotel().bookings().monthOccupationRate(date.toLocalDate());
						
						return Response.ok(new RateOccupation(rate, false, date.toLocalDate())).build();
					}
				});		
	}
	
	@POST
	@Path("/rate-occupation/week-work-day")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRateWeekWorkDayOccupation(final LocalDateTime date) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						double rate = hotel().bookings().weekWorkDayOccupationRate(date.toLocalDate());
						
						return Response.ok(new RateOccupation(rate, true, date.toLocalDate())).build();
					}
				});		
	}
	
	@POST
	@Path("/rate-occupation/weekend")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRateWeekendOccupation(final LocalDateTime date) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						double rate = hotel().bookings().weekendOccupationRate(date.toLocalDate());
						
						return Response.ok(new RateOccupation(rate, true, date.toLocalDate())).build();
					}
				});		
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response editBook(@PathParam("id") UUID id, BookingEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Guests container = hotel().bookings().guests();
						Guest guest = container.build(data.guestId());
												
						if(guest.isPresent())
							guest.update(data.guest().firstName(), data.guest().lastName(), data.guest().sex(), data.guest().address(), data.guest().birthDate(), data.guest().tel1(), data.guest().tel2(), data.guest().email(), data.guest().photo());
						else
						{
							Person person = hotel().company().persons().build(data.guestId());
							if(person.isPresent()){
								guest = container.transform(person);
							}else{
								guest = container.add(data.guest().firstName(), data.guest().lastName(), data.guest().sex(), data.guest().address(), data.guest().birthDate(), data.guest().tel1(), data.guest().tel2(), data.guest().email(), data.guest().photo());
							}					
						}
						
						Booking booking = hotel().bookings().get(id);
						booking.pieceInfos(data.naturePiece(), data.numeroPiece(), data.deliveredDatePiece(), data.editionPlacePiece(), data.editedByPiece());
						booking.otherInfos(data.numberOfChildren(), data.numberOfAdults(), data.exactDestination());

						return Response.ok(new GuestVm(guest)).build();
					}
				});	
		
	}
}
