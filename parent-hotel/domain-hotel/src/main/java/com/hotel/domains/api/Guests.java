package com.hotel.domains.api;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.securities.api.Person;
import com.securities.api.Sex;

public interface Guests extends AdvancedQueryable<Guest, UUID> {
	Guest add(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException;
	Guest transform(Person item) throws IOException;
}
