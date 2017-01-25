package com.lightpro.hotel.vm;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonGetter;

public class RateOccupation {
	
	private final transient double rate;
	private final transient boolean isWeekPeriod;
	private final transient LocalDate day;
	
	public RateOccupation(){
		throw new UnsupportedOperationException("#RateOccupation()");
	}
	
	public RateOccupation(final double rate, final boolean isWeekPeriod, final LocalDate day) {
        this.rate = rate;
        this.isWeekPeriod = isWeekPeriod;
        this.day = day;
    }
	
	@JsonGetter
	public double getRate(){
		return rate;
	}
	
	@JsonGetter
	public double getRateInPercent(){
		return rate * 100;
	}
	
	@JsonGetter
	public String getPeriodFormatted(){
		String formatted;
		if(isWeekPeriod){
			if(belongToSameWeek(day, LocalDate.now()))
				formatted = String.format("Semaine en cours");
			else {
				LocalDate start = day.with(DayOfWeek.MONDAY);
				LocalDate end = day.with(DayOfWeek.SUNDAY);
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
				formatted = String.format("Semaine du %s au %s %s", start.getDayOfMonth(), end.getDayOfMonth(), day.format(formatter));
			}				
		}else{
			
			
			if(belongToSameMonth(day, LocalDate.now()))
				formatted = String.format("Mois en cours");
			else {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
				formatted = day.format(formatter);
			}
		}
		
		return formatted;
	}
	
	private boolean belongToSameMonth(LocalDate date1, LocalDate date2){
		return date1.getMonthValue() == date2.getMonthValue() && date1.getYear() == date2.getYear();
	}
	
	private boolean belongToSameWeek(LocalDate date1, LocalDate date2){
		LocalDate start = date1.with(DayOfWeek.MONDAY);
		LocalDate end = date1.with(DayOfWeek.SUNDAY);
		return date2.isAfter(start) && date2.isBefore(end);
	}
	
	@JsonGetter
	public String getRateInPercentFormatted(){
		return String.format("%.1f", rate * 100);
	}
}
