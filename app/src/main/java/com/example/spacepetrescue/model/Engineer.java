package com.example.spacepetrescue.model;
public class Engineer extends CrewMember {
    public Engineer(String name) {
        super(name, "Engineer", 6, 3, 19, "pet_engineer_cute");
    }
    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        int healAmount = 3;
        ally.setEnergy(ally.getEnergy() + healAmount);
        return getName() + " repairs " + ally.getName() + "'s suit, restoring " + healAmount + " energy! 🔧";
    }
}