package com.example.spacepetrescue.model;
// ─────────────────────────────────────────────────────────────────────────────
// PILOT  –  Blue  –  Skill 5, Resilience 4, MaxEnergy 20
// Special: Evasive Maneuver – halves next threat attack (sets resilience temp high)
// ─────────────────────────────────────────────────────────────────────────────
class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, "Pilot", 5, 4, 20, "pet_pilot_cute");
    }

    /**
     * Evasive Maneuver: the Pilot predicts the threat's move and boosts the
     * ally's resilience by 2 for the description purposes (shown in log).
     * In practice, we just reduce incoming damage on the threat's next attack
     * by boosting the ally's defend return — here we apply a flat -2 to threat.
     */
    @Override
    public String useSpecialAbility(CrewMember ally, Threat threat) {
        // Deal 1.5x normal damage this turn
        int bonusDamage = (int)(getBaseSkill() * 0.5);
        int raw = getSkill() + bonusDamage;
        int dealt = threat.receiveAttack(raw);
        return getName() + " pulls a daring maneuver, dealing " + dealt + " boosted damage to " + threat.getName() + "! ✨";
    }
}