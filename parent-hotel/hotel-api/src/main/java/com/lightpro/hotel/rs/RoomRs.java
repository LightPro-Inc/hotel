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

@Path("/room")
public class RoomRs extends HotelBaseRs {
	
	@GET
	@Path("/floor")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomFloors() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomFloorVm> roomFloorsVm = hotel().allRoomFloors()
								   .stream()
								   .map(m -> new RoomFloorVm(m))
								   .collect(Collectors.toList());
		
						return Response.ok(roomFloorsVm).build();
					}
				});			
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRooms() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().allRooms()
								.all()
							    .stream()
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Path("/available")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomsAvailables() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().allRooms()
								.availables()
							    .stream()
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Path("/free")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getFreeRooms() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().allRooms()
								.all()								
							    .stream()
							    .filter(m -> {
									try {
										return m.isFree();
									} catch (IOException e) {
										e.printStackTrace();
									}
									return false;
								})
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Path("/occupied")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomsOccupieds() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().allRooms().all()
							    .stream()
							    .filter(m -> {
									try {
										return m.isOccupied();
									} catch (IOException e) {
										e.printStackTrace();
									}
									return false;
								})
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Path("/dirty")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoomsDirties() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<RoomVm> roomsVm = hotel().allRooms().all()
							    .stream()
							    .filter(m -> {
									try {
										return m.status() == RoomStatus.DIRTY;
									} catch (IOException e) {
										e.printStackTrace();
									}
									return false;
								})
							    .map(m -> new RoomVm(m))
							    .collect(Collectors.toList());
	
						return Response.ok(roomsVm).build();
					}
				});			
	}
	
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRooms(   @QueryParam("page") int page, 
								@QueryParam("pageSize") int pageSize, 
								@QueryParam("filter") String filter) throws IOException {
						
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						String filterLocal = (filter == null) ? "" : filter;
						
						List<RoomVm> rooms =  hotel().allRooms()
												   .find(page, pageSize, filterLocal)
												   .stream()
												   .map(m -> new RoomVm(m))
												   .collect(Collectors.toList());
						
						int count = hotel().allRooms().totalCount(filter);
						PaginationSet<RoomVm> pagedSet = new PaginationSet<RoomVm>(rooms, page, count);
						return Response.ok(pagedSet).build();
					}
				});		
	}
	
	@GET
	@Path("/{number}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRoom(@PathParam("number") String number) throws IOException {

		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room entity = hotel().allRooms().findSingle(number);
						if(entity == null)
							return Response.status(Status.NOT_FOUND).build();
						
						RoomVm vm = new RoomVm(entity);
						
						return Response.ok(vm).build();
					}
				});			
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response editRoom(@PathParam("id") UUID id, RoomEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().allRooms().findSingle(id);
						room.update(data.number(), data.floorId());

						return Response.status(Status.NO_CONTENT).build();
					}
				});			
	}
	
	@DELETE
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteRoom(@PathParam("id") UUID number) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						hotel().allRooms().delete(number);
						
						return Response.noContent().build();
					}
				});			
	}
	
	@POST
	@Path("/{id}/book")
	@Produces({MediaType.APPLICATION_JSON})
	public Response bookRoom(@PathParam("id") UUID id, BookingEdit data) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Booking booking = hotel().bookings().book(data.guestId(), id, data.start(), data.end(), data.nightPriceApplied());	

						return Response.status(Status.CREATED)
								       .entity(new BookingVm(booking))
								       .build();
					}
				});			
	}
	
	@POST
	@Path("/{id}/mark-cleanup")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markCleanup(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().allRooms().findSingle(id);
						room.changeStatus(RoomStatus.CLEANUP);	

						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
	
	@POST
	@Path("/{id}/mark-dirty")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markDirty(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().allRooms().findSingle(id);
						room.changeStatus(RoomStatus.DIRTY);	

						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
	
	@POST
	@Path("/{id}/mark-out-of-service")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markOutOfService(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().allRooms().findSingle(id);
						room.changeStatus(RoomStatus.OUTOFSERVICE);	

						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
	
	@POST
	@Path("/{id}/mark-in-service")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markInService(@PathParam("id") UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Room room = hotel().allRooms().findSingle(id);
						room.changeStatus(RoomStatus.READY);	

						return Response.ok(new RoomVm(room)).build();
					}
				});			
	}
}
