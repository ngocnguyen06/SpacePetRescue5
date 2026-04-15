package com.example.spacepetrescue.model;

import java.util.List;

/**
 * Quarters – the starting location for all crew members.
 * When a pet returns here its energy is fully restored.
 */
public class Quarters {

    private Storage storage;
    public Quarters() {
        this.storage = Storage.getInstance();
    }

    /**
     * Recruit a new crew member and place them in Quarters.
     */
    public void createCrewMember(CrewMember cm) {
        cm.setCurrentLocation(CrewMember.LOCATION_QUARTERS);
        storage.addCrewMember(cm);
    }

    /**
     * Restore energy of a returning crew member and move them to Quarters.
     */
    public void restoreEnergy(CrewMember cm) {
        cm.restoreEnergy();
        cm.setCurrentLocation(CrewMember.LOCATION_QUARTERS);
    }

    /** List all crew members currently in Quarters. */
    public List<CrewMember> listResidents() {
        return storage.listByLocation(CrewMember.LOCATION_QUARTERS);
    }
}