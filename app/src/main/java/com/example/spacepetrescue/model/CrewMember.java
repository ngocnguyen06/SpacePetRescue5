package com.example.spacepetrescue.model;
import java.io.Serializable;

public abstract class CrewMember implements Serializable {
    private static int idCounter = 1;
    private int id;
    private String name;
    private String specialization;
    private String avatarPath;
    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;
    private int missionsCompleted;
    private boolean isInjured;

    public static final String LOCATION_QUARTERS = "Quarters";
    public static final String LOCATION_SIMULATOR = "Simulator";
    public static final String LOCATION_MISSION = "MissionControl";
    public static final String LOCATION_MEDBAY = "Medbay";

    private String currentLocation;

    public CrewMember(String name, String specialization, int baseSkill, int baseResilience, int maxEnergy, String avatarPath) {
        this.id = idCounter++;
        this.name = name;
        this.specialization = specialization;
        this.skill = baseSkill;
        this.resilience = baseResilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
        this.missionsCompleted = 0;
        this.isInjured = false;
        this.avatarPath = avatarPath;
        this.currentLocation = LOCATION_QUARTERS;
    }
    public static int getNumberOfCreated() {
        return idCounter - 1;
    }
    public static void setIdCounter(int value) {
        idCounter = value;
    }

    public int act() {
        return skill + experience;
    }

    public int defend(int incomingDamage) {
        int actualDamage = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return actualDamage;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public abstract String useSpecialAbility(CrewMember ally, Threat threat);

    public void gainExperience(int xp) {
        this.experience += xp;
    }
    public void restoreEnergy() {
        this.energy = this.maxEnergy;
        this.isInjured = false;
    }

    public void sendToMedbay() {
        this.isInjured = true;
        this.energy = maxEnergy;
        this.experience = Math.max(0, experience - 1);
        this.currentLocation = LOCATION_MEDBAY;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSpecialization() {
        return specialization;
    }
    public String getAvatarPath() {
        return avatarPath;
    }
    public int getSkill() {
        return skill + experience;
    }
    public int getBaseSkill() {
        return skill;
    }
    public int getResilience() {
        return resilience;
    }
    public int getExperience() {
        return experience;
    }
    public int getEnergy() {
        return energy;
    }
    public int getMaxEnergy() {
        return maxEnergy;
    }
    public int getMissionsCompleted() {
        return missionsCompleted;
    }
    public boolean isInjured() {
        return isInjured;
    }
    public String getCurrentLocation() {
        return currentLocation;
    }
    public void setCurrentLocation(String loc) {
        this.currentLocation = loc;
    }
    public void setEnergy(int e) {
        this.energy = Math.max(0, Math.min(e, maxEnergy));
    }
    public void incrementMissionsCompleted() {
        this.missionsCompleted++;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return specialization + "(" + name + ") skill:" + getSkill()
                + " res:" + resilience
                + " exp:" + experience
                + " energy:" + energy + "/" + maxEnergy;
    }
}