package com.example.spacepetrescue.model;
import java.util.List;
public class Quarters {
    private Storage storage;
    public Quarters() {
        this.storage = Storage.getInstance();
    }
    public void createCrewMember(CrewMember cm) {
        cm.setCurrentLocation(CrewMember.LOCATION_QUARTERS);
        storage.addCrewMember(cm);
    }
    public void restoreEnergy(CrewMember cm) {
        cm.restoreEnergy();
        cm.setCurrentLocation(CrewMember.LOCATION_QUARTERS);
    }
    public List<CrewMember> listResidents() {
        return storage.listByLocation(CrewMember.LOCATION_QUARTERS);
    }
}