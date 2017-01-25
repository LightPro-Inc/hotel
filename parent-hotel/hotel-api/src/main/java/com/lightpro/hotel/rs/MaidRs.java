package com.lightpro.hotel.rs;

import java.io.IOException;
import java.time.LocalDateTime;
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

import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.Maids;
import com.infrastructure.core.PaginationSet;
import com.lightpro.hotel.cmd.ActivateMaidCmd;
import com.lightpro.hotel.cmd.MaidEdited;
import com.lightpro.hotel.cmd.PeriodCmd;
import com.lightpro.hotel.vm.MaidDayJobVm;
import com.lightpro.hotel.vm.MaidVm;

@Path("/maid")
public class MaidRs extends HotelBaseRs {
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<MaidVm> items = hotel().maids().all()
													 .stream()
											 		 .map(m -> new MaidVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Path("/active")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllActives() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<MaidVm> items = hotel().maids().actives()
													 .stream()
											 		 .map(m -> new MaidVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maids container = hotel().maids();
						
						List<MaidVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new MaidVm(m))
															 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
						PaginationSet<MaidVm> pagedSet = new PaginationSet<MaidVm>(itemsVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
	
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getSingle(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						MaidVm item = new MaidVm(hotel().maids().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final MaidEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maids containers = hotel().maids();
						Maid item = containers.add(cmd.firstName(), cmd.lastName(), cmd.sex(), cmd.address(), cmd.birthDate(), cmd.tel1(), cmd.tel2(), cmd.email(), cmd.photo());
						
						return Response.ok(new MaidVm(item)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/activate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response activate(@PathParam("id") final UUID id, final ActivateMaidCmd cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid item = hotel().maids().get(id);
						item.activate(cmd.active());
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/dayjob")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDayJobs(final PeriodCmd period) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<MaidDayJobVm> itemsVm = hotel().maidDayJobs()
															 .between(period.start(), period.end())
															 .stream()
															 .map(m -> new MaidDayJobVm(m))
															 .collect(Collectors.toList());
						
						return Response.ok(itemsVm).build();
					}
				});		
	}
	
	@GET
	@Path("/dayjob/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getSingleMaidDayJob(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						MaidDayJob item = hotel().maidDayJobs().get(id);
						
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@DELETE
	@Path("/dayjob/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteMaidDayJob(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						MaidDayJob item = hotel().maidDayJobs().get(id);
						hotel().maidDayJobs().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/plan")
	@Produces({MediaType.APPLICATION_JSON})
	public Response plan(@PathParam("id") final UUID id, final LocalDateTime date) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid maid = hotel().maids().get(id);
						MaidDayJob item = hotel().maidDayJobs().plan(date.toLocalDate(), maid);
						
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/mark-present")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markMaidPresent(@PathParam("id") final UUID id, LocalDateTime date) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid maid = hotel().maids().get(id);
						MaidDayJob item = hotel().maidDayJobs().get(date.toLocalDate(), maid);
												
						item.markPresent();
							
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/mark-absent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markMaidAbsent(@PathParam("id") final UUID id, LocalDateTime date) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid maid = hotel().maids().get(id);
						MaidDayJob item = hotel().maidDayJobs().get(date.toLocalDate(), maid);
												
						item.markAbsent();
							
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final MaidEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid item = hotel().maids().get(id);
						item.update(cmd.firstName(), cmd.lastName(), cmd.sex(), cmd.address(), cmd.birthDate(), cmd.tel1(), cmd.tel2(), cmd.email(), cmd.photo());
						
						return Response.ok(new MaidVm(item)).build();
					}
				});		
	}
	
	@DELETE
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid item = hotel().maids().get(id);
						hotel().maids().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
