package com.lightpro.hotel.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.hotel.domains.api.Bookings;
import com.hotel.domains.api.Guest;
import com.hotel.domains.api.Guests;
import com.infrastructure.core.ErrorMessage;
import com.infrastructure.core.PaginationSet;
import com.lightpro.hotel.cmd.GuestEdit;
import com.lightpro.hotel.vm.BookingVm;
import com.lightpro.hotel.vm.GuestVm;
import com.securities.api.Person;
import com.securities.api.Secured;

@Path("/guest")
public class GuestRs extends HotelBaseRs {
	
	@GET
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getGuest(@PathParam("id") UUID id) throws IOException {

		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Guest guest = hotel().bookings().guests().get(id);
						
						if(guest == null)
							return Response.status(Status.NOT_FOUND).build();
						
						return Response.ok(new GuestVm(guest)).build();
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
						
						Bookings bookings = hotel().bookings();						
						List<GuestVm> guests = bookings.guests().find(page, pageSize, filter)
												       .stream()
												       .map(m -> new GuestVm(m))
												       .collect(Collectors.toList());
						
						int count = bookings.guests().totalCount(filter);
						PaginationSet<GuestVm> pagedSet = new PaginationSet<GuestVm>(guests, page, count);
						return Response.ok(pagedSet).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response editGuest(@PathParam("id") UUID id, GuestEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Guests container = hotel().bookings().guests();
						Guest guest = container.build(id);
						
						if(guest.isPresent())
							guest.update(data.firstName(), data.lastName(), data.sex(), data.address(), data.birthDate(), data.tel1(), data.tel2(), data.email(), data.photo());
						else
						{
							Person person = hotel().company().persons().build(data.id());
							if(person.isPresent()){
								guest = container.transform(person);
							}else{
								guest = container.add(data.firstName(), data.lastName(), data.sex(), data.address(), data.birthDate(), data.tel1(), data.tel2(), data.email(), data.photo());
							}					
						}

						return Response.ok(new GuestVm(guest)).build();
					}
				});	
		
	}
	
	@GET
	@Secured
	@Path("/{id}/bookings")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getGuestBookings(@PathParam("id") UUID id) throws IOException {			
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Guest guest = hotel().bookings().guests().get(id);
						
						if(guest.id() == null)
							return Response.status(Status.NOT_FOUND)
										   .entity(new ErrorMessage("Liste des réservations d'un client", "Client non trouvé!")).build();
							
						List<BookingVm> bookings = guest.bookings().all()
														.stream()
														.map(m -> new BookingVm(m))
														.collect(Collectors.toList());
						
						return Response.ok(bookings).build();
					}
				});	
	}
}
