package com.example.spacepetrescue.model;
import java.io.Serializable;
public class Threat implements Serializable {
    private String name;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;
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
    public Threat(int missionCount, double difficultyMultiplier) {
        int idx = (int)(Math.random() * THREAT_NAMES.length);
        this.name = THREAT_NAMES[idx];

        this.skill = (int) ((4 + missionCount) * difficultyMultiplier);
        this.resilience = (int) ((1 + (missionCount / 2)) * difficultyMultiplier);
        this.maxEnergy = (int) ((20 + (missionCount * 3)) * difficultyMultiplier);
        this.energy = this.maxEnergy;
    }
    public int attack(CrewMember target) {
        int roll = (int)(Math.random() * 3);
        int rawDamage = skill + roll;
        return target.defend(rawDamage);
    }
    public int receiveAttack(int incomingDamage) {
        int actualDamage = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return actualDamage;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }
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
        return name + " (skill:" + skill + " res:" + resilience + " energy:" + energy + "/" + maxEnergy + ")";
    }
}