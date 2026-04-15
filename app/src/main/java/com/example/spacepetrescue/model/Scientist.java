package com.example.spacepetrescue.model;
class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name, "Scientist", 8, 1, 17, "pet_scientist_cute");
    }

    /**
     * Analyze Weakness: deals normal damage AND reduces threat's resilience
     * (we simulate this by dealing extra pierce damage in the log).
     */
    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        // Piercing attack ignores resilience fully
        int pierceDamage = getSkill();
        // Directly reduce threat energy (bypasses resilience)
        int before = threat.getEnergy();
        threat.receiveAttack(pierceDamage + threat.getResilience()); // compensate to bypass
        int dealt = before - threat.getEnergy();
        return getName() + " analyzes the weakness and deals " + dealt
                + " piercing damage, ignoring armor! 🔬";
    }
}