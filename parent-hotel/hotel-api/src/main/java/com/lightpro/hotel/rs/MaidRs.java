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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hotel.domains.api.Maid;
import com.hotel.domains.api.MaidDayJob;
import com.hotel.domains.api.MaidStatus;
import com.hotel.domains.api.Maids;
import com.infrastructure.core.PaginationSet;
import com.lightpro.hotel.cmd.ActivateMaidCmd;
import com.lightpro.hotel.cmd.PeriodCmd;
import com.lightpro.hotel.vm.MaidDayJobVm;
import com.lightpro.hotel.vm.MaidVm;
import com.securities.api.Contact;
import com.securities.api.Secured;

@Path("/maid")
public class MaidRs extends HotelBaseRs {
	
	@GET
	@Secured
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
	@Secured
	@Path("/active")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllActives() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<MaidVm> items = hotel().maids().withStatus(MaidStatus.ACTIVE).all()
													 .stream()
											 		 .map(m -> new MaidVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
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
													
						long count = container.count(filter);
						PaginationSet<MaidVm> pagedSet = new PaginationSet<MaidVm>(itemsVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
	
	@GET
	@Secured
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
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Contact contact = hotel().contacts().build(id);
						Maid item = hotel().maids().add(contact);
						
						log.info(String.format("Cr�ation de l'employ� d'entretien %s", item.name()));
						return Response.ok(new MaidVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/activate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response activate(@PathParam("id") final UUID id, final ActivateMaidCmd cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid item = hotel().maids().get(id);
						item.activate(cmd.active());
						
						if(cmd.active())
							log.info(String.format("Activation de l'employ� d'entretien %s", item.name()));
						else
							log.info(String.format("D�sactivation de l'employ� d'entretien %s", item.name()));
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/dayjob")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDayJobs(final PeriodCmd period) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<MaidDayJobVm> itemsVm = hotel().maidDayJobs()
															 .between(period.start(), period.end()).all()
															 .stream()
															 .map(m -> new MaidDayJobVm(m))
															 .collect(Collectors.toList());
						
						return Response.ok(itemsVm).build();
					}
				});		
	}
	
	@GET
	@Secured
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
	@Secured
	@Path("/dayjob/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteMaidDayJob(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						MaidDayJob item = hotel().maidDayJobs().get(id);
						hotel().maidDayJobs().delete(item);
						
						log.info(String.format("Suppression du jour plannifi� de l'employ� d'entretien %s", item.maid().name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/plan")
	@Produces({MediaType.APPLICATION_JSON})
	public Response plan(@PathParam("id") final UUID id, final LocalDateTime date) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid maid = hotel().maids().get(id);
						MaidDayJob item = maid.daysJob().add(date.toLocalDate());
						
						log.info(String.format("Plannification du jour de l'employ� d'entretien %s", maid.name()));
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-present")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markMaidPresent(@PathParam("id") final UUID id, LocalDateTime date) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid maid = hotel().maids().get(id);
						MaidDayJob item = maid.daysJob().get(date.toLocalDate());
												
						item.markPresent();
							
						log.info(String.format("Pointage de la pr�sence de l'employ� d'entretien %s", maid.name()));
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-absent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markMaidAbsent(@PathParam("id") final UUID id, LocalDateTime date) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid maid = hotel().maids().get(id);
						MaidDayJob item = maid.daysJob().get(date.toLocalDate());
												
						item.markAbsent();
							
						log.info(String.format("Pointage de l'absence de l'employ� d'entretien %s", maid.name()));
						return Response.ok(new MaidDayJobVm(item)).build();
					}
				});		
	}
	
	@DELETE
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Maid item = hotel().maids().get(id);
						String name = item.name();
						hotel().maids().delete(item);
						
						log.info(String.format("Suppression de l'employ� d'entretien %s", name));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
