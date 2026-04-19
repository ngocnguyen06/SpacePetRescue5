package com.example.spacepetrescue.model;
public class CrewMemberFactory {

    public static final String PILOT = "Pilot";
    public static final String ENGINEER = "Engineer";
    public static final String MEDIC = "Medic";
    public static final String SCIENTIST = "Scientist";
    public static final String SOLDIER = "Soldier";
    public static final String[] ALL_SPECS = {
            PILOT, ENGINEER, MEDIC, SCIENTIST, SOLDIER
    };

    public static CrewMember create(String name, String specialization) {
        switch (specialization) {
            case PILOT:
                return new Pilot(name);
            case ENGINEER:
                return new Engineer(name);
            case MEDIC:
                return new Medic(name);
            case SCIENTIST:
                return new Scientist(name);
            case SOLDIER:
                return new Soldier(name);
            default:
                return null;
        }
    }
}