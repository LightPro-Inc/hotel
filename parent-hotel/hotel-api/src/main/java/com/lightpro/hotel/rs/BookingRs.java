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
import com.hotel.domains.api.DayOccupation;
import com.hotel.domains.api.DayOccupationStatus;
import com.hotel.domains.api.Guest;
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
import com.securities.api.Contact;
import com.securities.api.Secured;
import com.infrastructure.core.PaginationSet;

@Path("/booking")
public class BookingRs extends HotelBaseRs {
			
	@GET
	@Secured
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
	@Secured
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
							/*for (Booking bk : rc.bookings().all()) {
								amount += bk.ttcTotalBookingAmount();
							}*/
							
							LocationStat stat = new LocationStat(rc.name(), amount);
							stats.add(stat);
						}
						
						return Response.ok(new ResumeLocationStat(stats)).build();
					}
				});
	}
	
	@GET
	@Secured
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
						
						long count = hotel().bookings().count(filter);
						PaginationSet<BookingVm> pagedSet = new PaginationSet<BookingVm>(rooms, page, count);
						return Response.ok(pagedSet).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/{id}/guest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBookingGuest(@PathParam("id") UUID id) throws IOException {			
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						Guest guest = hotel().guests().get(booking.guest().id());
						
						return Response.ok(new GuestVm(guest)).build();
					}
				});	
	}
	
	@GET
	@Secured
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
	@Secured
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
	@Secured
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
	@Secured
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
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBookings(BookingPeriod  period) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<BookingVm> bookingsVm = hotel().bookings().between(period.start(), period.end()).all()
							   	  .stream()
							      .map(m -> new BookingVm(m))
							      .collect(Collectors.toList());
	
						return Response.ok(bookingsVm).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{id}/guest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response identifyGuest(@PathParam("id") UUID id, GuestEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Contact contact = hotel().contacts().build(data.id());
											
			     		// 1 - attribuer la reservation a l'hote
						Booking booking = hotel().bookings().get(id);		
						booking.identifyGuest(contact);
													
						log.info(String.format("Idendification de l'hôte de la réservation de la chambre %s", booking.room().number()));
						return Response.ok().build();
					}
				});				
	}
	
	@POST
	@Secured
	@Path("/{id}/move")
	@Produces({MediaType.APPLICATION_JSON})
	public Response move(@PathParam("id") UUID id, BookingMoved bm) throws IOException {
				
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.move(bm.newStart(), bm.newEnd(), bm.newRoomId());
							
						log.info(String.format("Déplacement de la période réservation de la chambre %s", booking.room().number()));
						return Response.noContent().build();
					}
				});	
	}
	
	@POST
	@Secured
	@Path("/{id}/resize")
	@Produces({MediaType.APPLICATION_JSON})
	public Response resize(@PathParam("id") UUID id, BookingResized br) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.resize(br.newStart(), br.newEnd());
							
						log.info(String.format("Modification de la période de réservation de la chambre %s", booking.room().number()));
						return Response.noContent().build();
					}
				});			
	}	
	
	@POST
	@Secured
	@Path("/{id}/confirm")
	@Produces({MediaType.APPLICATION_JSON})
	public Response confirm(@PathParam("id") UUID id) {
						
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.confirm();

						log.info(String.format("Confirmation de la réservation de la chambre %s", booking.room().number()));
						return Response.ok(new BookingVm(booking)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/cancel")
	@Produces({MediaType.APPLICATION_JSON})
	public Response cancel(@PathParam("id") UUID id) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						String room = booking.room().number();
						booking.cancel();
						
						log.info(String.format("Annulation de la réservation de la chambre %s", room));
						return Response.noContent().build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/checkIn")
	@Produces({MediaType.APPLICATION_JSON})
	public Response checkIn(@PathParam("id") UUID id) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.checkIn();
						
						log.info(String.format("Logement de l'hôte de la chambre %s", booking.room().number()));
						return Response.noContent().build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/checkOut")
	@Produces({MediaType.APPLICATION_JSON})
	public Response checkOut(@PathParam("id") UUID id) {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().get(id);
						booking.checkOut();
						
						log.info(String.format("Sortie de l'hôte de la chambre %s", booking.room().number()));
						return Response.noContent().build();
					}
				});		
	}
	
	@POST
	@Secured
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
	@Secured
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
	@Secured
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
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response editBook(@PathParam("id") UUID id, BookingEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Contact guest = hotel().contacts().build(data.guestId());
						Contact customer = hotel().contacts().build(data.customerId());
						
						Booking booking = hotel().bookings().get(id);
						booking.identifyGuest(guest);
						booking.identifyCustomer(customer);
						booking.pieceInfos(data.naturePiece(), data.numeroPiece(), data.deliveredDatePiece(), data.editionPlacePiece(), data.editedByPiece());
						booking.otherInfos(data.numberOfChildren(), data.numberOfAdults(), data.exactDestination());

						log.info(String.format("Mise à jour de la réservation de la chambre %s", booking.room().number()));
						return Response.ok(new BookingVm(booking)).build();
					}
				});	
		
	}
}
