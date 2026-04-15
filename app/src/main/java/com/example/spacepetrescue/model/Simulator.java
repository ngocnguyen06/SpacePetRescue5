package com.example.spacepetrescue.model;

import java.util.List;

/**
 * Simulator – training area.
 * Crew members here can earn XP which boosts their effective skill.
 */
public class Simulator {

    private static final int XP_PER_SESSION = 1;

    private Storage storage;

    public Simulator() {
        this.storage = Storage.getInstance();
    }

    /**
     * Run a training session for a single crew member.
     * Awards XP_PER_SESSION experience points.
     */
    public void train(CrewMember cm) {
        cm.gainExperience(XP_PER_SESSION);
    }

    /** List all crew members currently in the Simulator. */
    public List<CrewMember> listTrainees() {
        return storage.listByLocation(CrewMember.LOCATION_SIMULATOR);
    }
}