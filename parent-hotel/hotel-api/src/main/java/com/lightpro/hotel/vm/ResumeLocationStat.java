package com.lightpro.hotel.vm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

public class ResumeLocationStat {
	
	private final transient List<LocationStat> stats;
	
	public ResumeLocationStat(List<LocationStat> stats){
		this.stats = stats;
	}
	
	@JsonGetter
	List<LocationStat> getStats(){
		return this.stats;
	}
	
	@JsonGetter
	public double getTotalAmount(){
		double sum = 0;
		
		for (LocationStat locationStat : stats) {
			sum += locationStat.getAmount();
		}
		
		return sum;
	}
}
