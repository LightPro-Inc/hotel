package com.lightpro.hotel.cmd;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotel.domains.api.BookingStatus;

public class BookingEdit {
	
	private final UUID guestId;
	private final Date start;
	private final Date end;
	private final BookingStatus statusId;
	private final double nightPriceApplied;
	private final String naturePiece;
	private final String numeroPiece;
	private final Date deliveredDatePiece;
	private final String editionPlacePiece;
	private final String editedByPiece;
	private final int numberOfChildren;
	private final int numberOfAdults;
	private final String exactDestination;
	private final GuestEdit guest;
	
	public BookingEdit(){
		throw new UnsupportedOperationException("#BookEdit()");
	}
	
	@JsonCreator
	public BookingEdit(@JsonProperty("guestId") final UUID guestId, 
					@JsonProperty("start") final Date start,
					@JsonProperty("end") final Date end,
					@JsonProperty("statusId") final BookingStatus statusId,
					@JsonProperty("nightPriceApplied") final double nightPriceApplied,
					@JsonProperty("naturePiece") final String naturePiece, 
					@JsonProperty("numeroPiece") final String numeroPiece,
					@JsonProperty("deliveredDatePiece") final Date deliveredDatePiece, 
					@JsonProperty("editionPlacePiece") final String editionPlacePiece,
					@JsonProperty("editedByPiece") final String editedByPiece,
					@JsonProperty("numberOfChildren") final int numberOfChildren,
					@JsonProperty("numberOfAdults") final int numberOfAdults,
					@JsonProperty("exactDestination") final String exactDestination,
					@JsonProperty("guest") final GuestEdit guest){
		
		this.guestId = guestId;
		this.start = start;
		this.end = end;
		this.statusId = statusId;
		this.nightPriceApplied = nightPriceApplied;
		this.naturePiece = naturePiece;
		this.numeroPiece = numeroPiece;
		this.deliveredDatePiece = deliveredDatePiece;
		this.editionPlacePiece = editionPlacePiece;
		this.editedByPiece = editedByPiece;
		this.numberOfChildren = numberOfChildren;
		this.numberOfAdults = numberOfAdults;
		this.exactDestination = exactDestination;
		this.guest = guest == null ? GuestEdit.defaultValue() : guest;
	}
	
	public UUID guestId(){
		return this.guestId;
	}
	
	public Date start(){
		return this.start;
	}
	
	public Date end(){
		return this.end;
	}
	
	public BookingStatus statusId(){
		return this.statusId;
	}
	
	public double nightPriceApplied(){
		return this.nightPriceApplied;
	}
	
	public String naturePiece(){
		return this.naturePiece;
	}
	
	public String numeroPiece(){
		return this.numeroPiece;
	}
	
	public Date deliveredDatePiece(){
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
	
	public GuestEdit guest(){
		return this.guest;
	}
}
