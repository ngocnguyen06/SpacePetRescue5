package com.example.spacepetrescue.model;
import java.util.List;
public class Simulator {
    private static final int XP_PER_SESSION = 1;
    private Storage storage;
    public Simulator() {
        this.storage = Storage.getInstance();
    }
    public void train(CrewMember cm) {
        cm.gainExperience(XP_PER_SESSION);
    }
    public List<CrewMember> listTrainees() {
        return storage.listByLocation(CrewMember.LOCATION_SIMULATOR);
    }
}