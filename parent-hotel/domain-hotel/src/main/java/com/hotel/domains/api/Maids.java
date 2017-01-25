package com.hotel.domains.api;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.securities.api.Sex;

public interface Maids extends Queryable<Maid> {
	List<Maid> actives() throws IOException;
	Maid add(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException;
	void delete(Maid item) throws IOException;
	Maid get(UUID id) throws IOException;
}
