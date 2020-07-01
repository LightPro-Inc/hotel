package com.lightpro.hotel.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hotel.domains.api.Guest;
import com.hotel.domains.api.Guests;
import com.infrastructure.core.PaginationSet;
import com.lightpro.hotel.vm.BookingVm;
import com.lightpro.hotel.vm.GuestVm;
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
						
						Guest guest = hotel().guests().get(id);
						
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
						
						Guests guests = hotel().guests();						
						List<GuestVm> guestsVm = guests.find(page, pageSize, filter)
												       .stream()
												       .map(m -> new GuestVm(m))
												       .collect(Collectors.toList());
						
						long count = guests.count(filter);
						PaginationSet<GuestVm> pagedSet = new PaginationSet<GuestVm>(guestsVm, page, count);
						return Response.ok(pagedSet).build();
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
						
						Guest guest = hotel().guests().get(id);
						
						List<BookingVm> bookings = guest.bookings().all()
														.stream()
														.map(m -> new BookingVm(m))
														.collect(Collectors.toList());
						
						return Response.ok(bookings).build();
					}
				});	
	}
}
