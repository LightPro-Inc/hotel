package com.hotel.domains.api;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;
import com.securities.api.Sex;

public interface Maids extends AdvancedQueryable<Maid>, Updatable<Maid> {
	List<Maid> actives() throws IOException;
	Maid add(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException;
}
