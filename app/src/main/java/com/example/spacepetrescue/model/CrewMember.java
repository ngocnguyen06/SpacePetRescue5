package com.example.spacepetrescue.model;
import java.io.Serializable;

/**
 * Abstract base class for all crew member specializations.
 * Each crew member has stats, can act in missions, and can be trained.
 */
public abstract class CrewMember implements Serializable {

    // ── Static ID counter (like the example UML diagram) ──────────────────
    private static int idCounter = 1;

    // ── Fields ─────────────────────────────────────────────────────────────
    private int id;
    private String name;
    private String specialization;
    private String avatarPath;   // e.g. "avatar_pilot" – used as drawable resource name

    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;
    private int missionsCompleted;

    // Bonus feature – "No Death": instead of dying, pet goes to Medbay
    private boolean isInjured;

    // Location constants
    public static final String LOCATION_QUARTERS = "Quarters";
    public static final String LOCATION_SIMULATOR = "Simulator";
    public static final String LOCATION_MISSION = "MissionControl";
    public static final String LOCATION_MEDBAY = "Medbay";

    private String currentLocation;

    // ── Constructor ────────────────────────────────────────────────────────
    public CrewMember(String name, String specialization, int baseSkill, int baseResilience, int maxEnergy, String avatarPath) {
        this.id = idCounter++;
        this.name = name;
        this.specialization = specialization;
        this.skill = baseSkill;
        this.resilience = baseResilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;   // starts full
        this.experience = 0;
        this.missionsCompleted = 0;
        this.isInjured = false;
        this.avatarPath = avatarPath;
        this.currentLocation = LOCATION_QUARTERS;
    }

    // ── Static helper ──────────────────────────────────────────────────────
    public static int getNumberOfCreated() {
        return idCounter - 1;
    }

    /** Reset counter (useful when loading from file to avoid duplicates). */
    public static void setIdCounter(int value) {
        idCounter = value;
    }

    // ── Core mission methods ───────────────────────────────────────────────

    /**
     * Calculates damage this crew member deals.
     * Effective skill = base skill + experience bonus.
     * Returns the damage value (before target's resilience is applied).
     */
    public int act() {
        return skill + experience;
    }

    /**
     * Receives damage. Damage absorbed = damage - resilience (min 1 always lands).
     * Returns actual damage taken.
     */
    public int defend(int incomingDamage) {
        int actualDamage = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return actualDamage;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public abstract String useSpecialAbility(CrewMember ally, Threat threat);

    // ── Training ──────────────────────────────────────────────────────────
    public void gainExperience(int xp) {
        this.experience += xp;
        // XP directly adds to effective skill (handled in act())
    }

    // ── Recovery ──────────────────────────────────────────────────────────
    /** Sending back to Quarters restores energy fully but keeps XP. */
    public void restoreEnergy() {
        this.energy = this.maxEnergy;
        this.isInjured = false;
    }

    /** No-Death bonus: go to Medbay instead of being removed. */
    public void sendToMedbay() {
        this.isInjured = true;
        this.energy = maxEnergy;      // reset HP after penalty
        this.experience = Math.max(0, experience - 1); // XP penalty
        this.currentLocation = LOCATION_MEDBAY;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────
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