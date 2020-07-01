package com.lightpro.hotel.vm;

import java.util.List;

public final class ResumeLocationStat {
	
	public final double totalAmount;
	
	public ResumeLocationStat(List<LocationStat> stats){
		this.totalAmount = getTotalAmount(stats);
	}

	private static double getTotalAmount(List<LocationStat> stats){
		double sum = 0;
		
		for (LocationStat locationStat : stats) {
			sum += locationStat.getAmount();
		}
		
		return sum;
	}
}
