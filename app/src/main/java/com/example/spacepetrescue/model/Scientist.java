package com.example.spacepetrescue.model;
public class Scientist extends CrewMember {
    public Scientist(String name) {
        super(name, "Scientist", 8, 1, 17, "pet_scientist_cute");
    }
    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        int pierceDamage = getSkill();
        int before = threat.getEnergy();
        threat.receiveAttack(pierceDamage + threat.getResilience());
        int dealt = before - threat.getEnergy();
        return getName() + " analyzes the weakness and deals " + dealt + " piercing damage, ignoring armor! 🔬";
    }
}