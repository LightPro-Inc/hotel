package com.hotel.domains.api;

import com.infrastructure.core.DomainMetadata;

public class BookingMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public BookingMetadata() {
		this.domainName = "hotel.bookings";
		this.keyName = "id";
	}
	
	public BookingMetadata(final String domainName, final String keyName){
		this.domainName = domainName;
		this.keyName = keyName;
	}
	
	@Override
	public String domainName() {
		return this.domainName;
	}

	@Override
	public String keyName() {
		return this.keyName;
	}
	
	public String startDateKey() {
		return "startdate";
	}
	
	public String endDateKey() {
		return "enddate";
	}
	
	public String nightPriceAppliedKey() {
		return "night_price_applied";
	}
	
	public String vatRateAppliedKey() {
		return "vat_rate_applied";
	}
	
	public String paidAmountKey() {
		return "paid_amount";
	}
	
	public String statusKey() {
		return "status";
	}	
	
	public String guestIdKey() {
		return "guestid";
	}
	
	public String roomIdKey() {
		return "roomid";
	}
	
	public String ttcTotalBookingAmountKey() {
		return "ttc_total_booking_amount";
	}
	
	public String vatBookingAmountKey() {
		return "vat_booking_amount";
	}
	
	public String naturePieceKey() {
		return "naturepiece";
	}
	
	public String numeroPieceKey() {
		return "numeropiece";
	}	
	
	public String deliveredDatePieceKey() {
		return "delivered_date_piece";
	}
	
	public String editionPlacePieceKey() {
		return "edition_place_piece";
	}
	
	public String editedByPieceKey() {
		return "edited_by_piece";
	}
	
	public String numberOfChildrenKey() {
		return "number_of_children";
	}
	
	public String numberOfAdultsKey() {
		return "number_of_adults";
	}
	
	public String exactDestinationKey() {
		return "exact_destination";
	}
	
	public static BookingMetadata create(){
		return new BookingMetadata();
	}
}
