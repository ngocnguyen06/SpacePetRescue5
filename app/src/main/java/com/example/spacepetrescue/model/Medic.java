package com.example.spacepetrescue.model;
class Medic extends CrewMember {

    public Medic(String name) {
        super(name, "Medic", 7, 2, 18, "pet_medic_cute");
    }

    /** Emergency Heal: restores 5 energy to the ally. */
    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        int healAmount = 5;
        ally.setEnergy(ally.getEnergy() + healAmount);
        return getName() + " administers emergency treatment to " + ally.getName()
                + ", restoring " + healAmount + " energy! 💊";
    }
}