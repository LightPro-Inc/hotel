package com.lightpro.hotel.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
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
import com.hotel.domains.api.Room;
import com.hotel.domains.api.RoomStatus;
import com.infrastructure.core.PaginationSet;
import com.lightpro.hotel.cmd.BookingEdit;
import com.lightpro.hotel.cmd.RoomEdit;
import com.lightpro.hotel.vm.BookingVm;
import com.lightpro.hotel.vm.RoomFloorVm;
import com.lightpro.hotel.vm.RoomVm;
import com.securities.api.Contact;
import com.securities.api.Secured;

@Path("/room")
public class RoomRs extends HotelBaseRs {
	
	@GET
	@Secured
	@Path("/floor")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomFloors() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomFloorVm> roomFloorsVm = hotel().roomFloors()
								   .stream()
								   .map(m -> new RoomFloorVm(m))
								   .collect(Collectors.toList());
		
						return Response.ok(roomFloorsVm).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRooms() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().rooms()
								.all()
							    .stream()
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/available")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomsAvailables() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().rooms().all()
							    .stream()
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/free")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getFreeRooms() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().rooms()
								.all()								
							    .stream()
							    .filter(m -> {
									try {
										return m.isFree();
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								})
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/occupied")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomsOccupieds() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().rooms().all()
							    .stream()
							    .filter(m -> {
									try {
										return m.isOccupied();
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								})
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/dirty")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomsDirties() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().rooms().withStatus(RoomStatus.DIRTY).all()
							    .stream()
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRooms(   @QueryParam("page") int page, 
								@QueryParam("pageSize") int pageSize, 
								@QueryParam("filter") String filter) throws IOException {
						
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> rooms =  hotel().rooms()
												   .find(page, pageSize, filter)
												   .stream()
												   .map(m -> new RoomVm(m))
												   .collect(Collectors.toList());
						
						long count = hotel().rooms().count(filter);
						PaginationSet<RoomVm> pagedSet = new PaginationSet<RoomVm>(rooms, page, count);
						return Response.ok(pagedSet).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/{number}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoom(@PathParam("number") String number) throws IOException {

		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room entity = hotel().rooms().get(number);
						
						RoomVm vm = new RoomVm(entity);
						
						return Response.ok(vm).build();
					}
				});			
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response editRoom(@PathParam("id") UUID id, RoomEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().rooms().get(id);
						room.update(data.number(), data.floorId());

						log.info(String.format("Mise à jour des données de la chambre %s", room.number()));
						return Response.status(Status.NO_CONTENT).build();
					}
				});			
	}
	
	@DELETE
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteRoom(@PathParam("id") UUID number) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room item = hotel().rooms().get(number);
						hotel().rooms().delete(item);
						
						log.info(String.format("Suppresion de la chambre %s", item.number()));
						return Response.noContent().build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{id}/book")
	@Produces({MediaType.APPLICATION_JSON})
	public Response bookRoom(@PathParam("id") UUID id, BookingEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Contact guest = hotel().contacts().build(data.guestId());
						Contact customer = hotel().contacts().build(data.customerId());
						Contact seller = currentUser;
						Room room = hotel().rooms().get(id);
						Booking booking = room.book(customer, guest, data.start(), data.end(), seller);	

						log.info(String.format("Réservation de la chambre %s", booking.room().number()));
						return Response.status(Status.CREATED)
								       .entity(new BookingVm(booking))
								       .build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-cleanup")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markCleanup(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().rooms().get(id);
						room.changeStatus(RoomStatus.CLEANUP);	

						log.info(String.format("Déclarer propre la chambre %s", room.number()));
						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-dirty")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markDirty(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().rooms().get(id);
						room.changeStatus(RoomStatus.DIRTY);	

						log.info(String.format("Déclarer sale la chambre %s", room.number()));
						
						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-out-of-service")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markOutOfService(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().rooms().get(id);
						room.changeStatus(RoomStatus.OUTOFSERVICE);	

						log.info(String.format("Déclarer hors service la chambre %s", room.number()));
						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-in-service")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markInService(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().rooms().get(id);
						room.changeStatus(RoomStatus.READY);	

						log.info(String.format("Déclarer en service la chambre %s", room.number()));
						
						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
}
