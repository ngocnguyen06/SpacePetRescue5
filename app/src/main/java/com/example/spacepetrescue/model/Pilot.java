package com.example.spacepetrescue.model;
public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, "Pilot", 5, 4, 20, "pet_pilot_cute");
    }

    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        int bonusDamage = (int)(getBaseSkill() * 0.5);
        int raw = getSkill() + bonusDamage;
        int dealt = threat.receiveAttack(raw);
        return getName() + " pulls a daring maneuver, dealing " + dealt + " boosted damage to " + threat.getName() + "! ✨";
    }
}