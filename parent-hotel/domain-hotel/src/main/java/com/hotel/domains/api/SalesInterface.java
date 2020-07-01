package com.hotel.domains.api;

import java.io.IOException;

import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.PurchaseOrder;

public interface SalesInterface {
	ProductCategory getHotelCategory() throws IOException;
	void syncProduct(RoomCategory category) throws IOException;
	PurchaseOrder generateOrder(Booking booking) throws IOException;
	void linkOrderToBooking(PurchaseOrder order, Booking booking) throws IOException;
	PurchaseOrder updateOrderOf(Booking booking) throws IOException;
}
