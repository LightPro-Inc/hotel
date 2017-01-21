package com.hotel.domains.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstRoomFloor implements RoomFloor {

	private final transient String id;
	private final transient String name;	
	
	public ConstRoomFloor(String id) throws IOException {
		Optional<RoomFloor> floorOptional = allFloors().stream().filter(f -> f.id().compareTo(id) == 0).findFirst();
		
		if(!floorOptional.isPresent())
			throw new IOException("Floor doesn't exist !");
		
		RoomFloor floor = floorOptional.get();
		this.id = floor.id();
		this.name = floor.name();
		
	}
	
	private ConstRoomFloor(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String name() throws IOException {
		return name;
	}

	public static List<RoomFloor> allFloors() {
		List<RoomFloor> floors = new ArrayList<RoomFloor>();
		
		floors.add(new ConstRoomFloor("RC", "Rez de chaussée"));
		floors.add(new ConstRoomFloor("ETA1", "Etage 1"));
		floors.add(new ConstRoomFloor("ETA2", "Etage 2"));
		floors.add(new ConstRoomFloor("ETA3", "Etage 3"));
		floors.add(new ConstRoomFloor("ETA4", "Etage 4"));
		floors.add(new ConstRoomFloor("ETA5", "Etage 5"));
				
		return floors;
	}
}
