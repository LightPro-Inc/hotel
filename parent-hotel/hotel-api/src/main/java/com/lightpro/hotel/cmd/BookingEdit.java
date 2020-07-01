package com.lightpro.hotel.cmd;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotel.domains.api.BookingStatus;

public class BookingEdit {
	
	private final UUID customerId;
	private final UUID guestId;
	private final LocalDateTime start;
	private final LocalDateTime end;
	private final BookingStatus statusId;
	private final String naturePiece;
	private final String numeroPiece;
	private final LocalDate deliveredDatePiece;
	private final String editionPlacePiece;
	private final String editedByPiece;
	private final int numberOfChildren;
	private final int numberOfAdults;
	private final String exactDestination;
	
	public BookingEdit(){
		throw new UnsupportedOperationException("#BookEdit()");
	}
	
	@JsonCreator
	public BookingEdit(@JsonProperty("customerId") final UUID customerId,
					@JsonProperty("guestId") final UUID guestId, 
					@JsonProperty("start") final LocalDateTime start,
					@JsonProperty("end") final LocalDateTime end,
					@JsonProperty("statusId") final BookingStatus statusId,
					@JsonProperty("naturePiece") final String naturePiece, 
					@JsonProperty("numeroPiece") final String numeroPiece,
					@JsonProperty("deliveredDatePiece") final LocalDate deliveredDatePiece, 
					@JsonProperty("editionPlacePiece") final String editionPlacePiece,
					@JsonProperty("editedByPiece") final String editedByPiece,
					@JsonProperty("numberOfChildren") final int numberOfChildren,
					@JsonProperty("numberOfAdults") final int numberOfAdults,
					@JsonProperty("exactDestination") final String exactDestination){
		
		this.customerId = customerId;
		this.guestId = guestId;
		this.start = start;
		this.end = end;
		this.statusId = statusId;
		this.naturePiece = naturePiece;
		this.numeroPiece = numeroPiece;
		this.deliveredDatePiece = deliveredDatePiece;
		this.editionPlacePiece = editionPlacePiece;
		this.editedByPiece = editedByPiece;
		this.numberOfChildren = numberOfChildren;
		this.numberOfAdults = numberOfAdults;
		this.exactDestination = exactDestination;
	}
	
	public UUID customerId(){
		return this.customerId;
	}
	
	public UUID guestId(){
		return this.guestId;
	}
	
	public LocalDateTime start(){
		return this.start;
	}
	
	public LocalDateTime end(){
		return this.end;
	}
	
	public BookingStatus statusId(){
		return this.statusId;
	}
	
	public String naturePiece(){
		return this.naturePiece;
	}
	
	public String numeroPiece(){
		return this.numeroPiece;
	}
	
	public LocalDate deliveredDatePiece(){
		return this.deliveredDatePiece;
	}
	
	public String editionPlacePiece(){
		return this.editionPlacePiece;
	}
	
	public String editedByPiece(){
		return this.editedByPiece;
	}
	
	public int numberOfChildren(){
		return this.numberOfChildren;
	}
	
	public int numberOfAdults(){
		return this.numberOfAdults;
	}
	
	public String exactDestination(){
		return this.exactDestination;
	}
}
