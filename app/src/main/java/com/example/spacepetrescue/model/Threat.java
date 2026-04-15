package com.example.spacepetrescue.model;

import java.io.Serializable;

/**
 * Represents a system-generated threat that crew members fight.
 * Stats scale with the number of completed missions (difficulty scaling).
 */
public class Threat implements Serializable {

    private String name;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;

    // Threat type names for flavour
    public static final String[] THREAT_NAMES = {
            "Asteroid Storm",
            "Fuel Leak",
            "Alien Virus",
            "Solar Flare",
            "Broken Engine",
            "Space Pirates",
            "Rogue Robot",
            "Meteor Shower",
            "Oxygen Failure",
            "System Glitch"
    };

    /**
     * @param missionCount  how many missions have been completed so far (scaling)
     */
    public Threat(int missionCount) {
        // Pick a random name
        int idx = (int)(Math.random() * THREAT_NAMES.length);
        this.name = THREAT_NAMES[idx];

        // Scaling formula: gets harder every mission
        this.skill = 4 + missionCount;
        this.resilience = 1 + (missionCount / 2);
        this.maxEnergy = 20 + (missionCount * 3);
        this.energy = this.maxEnergy;
    }

    // ── Combat methods ────────────────────────────────────────────────────

    /**
     * Threat attacks a crew member.
     * Returns actual damage dealt.
     */
    public int attack(CrewMember target) {
        // Add a small random factor (bonus feature: randomness in missions)
        int roll = (int)(Math.random() * 3); // 0-2
        int rawDamage = skill + roll;
        return target.defend(rawDamage);
    }

    /**
     * Crew member acts against the threat.
     * Returns actual damage dealt to threat.
     */
    public int receiveAttack(int incomingDamage) {
        int actualDamage = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return actualDamage;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String getName() {
        return name;
    }
    public int getSkill() {
        return skill;
    }
    public int getResilience() {
        return resilience;
    }
    public int getEnergy() {
        return energy;
    }
    public int getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public String toString() {
        return name + " (skill:" + skill + " res:" + resilience
                + " energy:" + energy + "/" + maxEnergy + ")";
    }
}