package com.example.spacepetrescue.model;
class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name, "Soldier", 9, 0, 16, "pet_soldier_cute");
    }

    /** Special Attack: deals double damage. */
    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        int rawDamage = getSkill() * 2;
        int dealt = threat.receiveAttack(rawDamage);
        return getName() + " unleashes a power strike, dealing " + dealt + " massive damage! ⚔️";
    }
}