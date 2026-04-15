package com.example.spacepetrescue.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the cooperative, turn-based mission system.
 *
 * Flow:
 *  1. Two crew members are passed in.
 *  2. A Threat is generated based on missionCount.
 *  3. Turns alternate: petA acts, threat retaliates on petA, then petB acts, etc.
 *  4. If a pet is defeated → sent to Medbay (No Death bonus).
 *  5. Mission ends when threat or all pets are defeated.
 *
 * The caller chooses the action each turn: ATTACK or SPECIAL (Tactical Combat bonus).
 * If running automatically (e.g. in a simulation), ATTACK is used by default.
 */
public class MissionControl {

    public enum Action { ATTACK, SPECIAL }

    private Storage storage;
    private List<String> log;

    // Active mission state (kept between turns for step-by-step UI)
    private CrewMember petA;
    private CrewMember petB;        // may become null if defeated
    private Threat threat;
    private boolean missionActive;
    private int currentTurn; // which pet's turn it is: 0=A, 1=B
    private int round;

    public MissionControl() {
        this.storage = Storage.getInstance();
        this.log = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Initialise a new mission with two crew members.
     * Call this before any takeTurn() calls.
     */
    public void launchMission(CrewMember a, CrewMember b) {
        this.petA = a;
        this.petB = b;
        this.threat = new Threat(storage.getMissionCount());
        this.missionActive = true;
        this.currentTurn  = 0;
        this.round = 1;
        this.log = new ArrayList<>();

        log.add("=== MISSION START ===");
        log.add("Threat: " + threat);
        log.add(petA.toString());
        log.add(petB.toString());
        log.add("--- Round " + round + " ---");
    }

    /** Returns the threat generated for the current mission. */
    public Threat getThreat() {
        return threat;
    }
    public boolean isMissionActive() {
        return missionActive;
    }
    public List<String> getLog() {
        return log;
    }
    public int getCurrentTurn() {
        return currentTurn;
    } // 0=petA, 1=petB
    public CrewMember getActivePet() {
        return currentTurn == 0 ? petA : petB;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Step-by-step turn (used by the UI for Tactical Combat)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Execute one pet's turn.
     * @param action ATTACK or SPECIAL
     * @return true if the mission is still going, false if it has ended
     */
    public boolean takeTurn(Action action) {
        if (!missionActive) return false;

        CrewMember active = getActivePet();
        if (active == null || active.isDefeated()) {
            // Skip defeated pet's slot
            advanceTurn();
            return !checkMissionEnd();
        }

        // ── Pet acts ──────────────────────────────────────────────────
        if (action == Action.SPECIAL) {
            CrewMember ally = (currentTurn == 0) ? petB : petA;
            if (ally == null || ally.isDefeated()) ally = active; // solo
            String specialLog = active.useSpecialAbility(ally, threat);
            log.add(specialLog);
        } else {
            int dmg   = active.act();
            int dealt = threat.receiveAttack(dmg);
            log.add(active.getName() + " attacks " + threat.getName()
                    + " → " + dealt + " damage  [" + threat.getEnergy()
                    + "/" + threat.getMaxEnergy() + " HP]");
        }

        // ── Check if threat defeated ───────────────────────────────────
        if (threat.isDefeated()) {
            endMissionVictory();
            return false;
        }

        // ── Threat retaliates against active pet ──────────────────────
        int thrDealt = threat.attack(active);
        log.add(threat.getName() + " retaliates → " + thrDealt
                + " damage on " + active.getName()
                + "  [" + active.getEnergy() + "/" + active.getMaxEnergy() + " HP]");

        // ── Check if pet is defeated ───────────────────────────────────
        if (active.isDefeated()) {
            handleDefeatedPet(active);
        }

        // ── Check overall mission end ──────────────────────────────────
        if (checkMissionEnd()) return false;

        // ── Advance turn ──────────────────────────────────────────────
        advanceTurn();
        return true; // mission continues
    }

    // ─────────────────────────────────────────────────────────────────────
    // Full auto-run (used for non-tactical mode or testing)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Runs the entire mission automatically (always ATTACK action).
     * Returns a MissionResult with full log and outcome.
     */
    public MissionResult runMissionAuto() {
        while (missionActive) {
            // Count rounds for log readability
            if (currentTurn == 0) {
                log.add("--- Round " + round + " ---");
            }
            takeTurn(Action.ATTACK);
        }
        return buildResult();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────

    private void advanceTurn() {
        currentTurn = (currentTurn == 0) ? 1 : 0;
        if (currentTurn == 0) {
            round++;
            log.add("--- Round " + round + " ---");
        }
    }

    /** Handle a pet being knocked out (No Death: goes to Medbay). */
    private void handleDefeatedPet(CrewMember pet) {
        log.add("⚠ " + pet.getName() + " is down! Sending to Medbay...");
        pet.sendToMedbay();
        storage.addCrewMember(pet); // update location in storage
        if (pet == petA) petA = null;
        else petB = null;
    }

    /** Returns true if the mission has ended (either side fully defeated). */
    private boolean checkMissionEnd() {
        boolean allPetsDown = (petA == null || petA.isDefeated())
                && (petB == null || petB.isDefeated());
        if (threat.isDefeated()) {
            endMissionVictory();
            return true;
        }
        if (allPetsDown) {
            endMissionDefeat();
            return true;
        }
        return false;
    }

    private void endMissionVictory() {
        missionActive = false;
        log.add("=== MISSION COMPLETE ===");
        log.add(threat.getName() + " has been neutralised!");
        storage.incrementMissionCount();

        // Award XP and move survivors back to Mission Control
        awardXPIfAlive(petA);
        awardXPIfAlive(petB);
    }

    private void endMissionDefeat() {
        missionActive = false;
        log.add("=== MISSION FAILED ===");
        log.add("All crew members are down!");
        // With No Death they're already in Medbay from handleDefeatedPet()
    }

    private void awardXPIfAlive(CrewMember pet) {
        if (pet != null && !pet.isDefeated()) {
            pet.gainExperience(1);
            pet.incrementMissionsCompleted();
            log.add(pet.getName() + " earns 1 XP  (total: " + pet.getExperience() + ")");
        }
    }

    private MissionResult buildResult() {
        List<CrewMember> survivors = new ArrayList<>();
        if (petA != null && !petA.isDefeated()) survivors.add(petA);
        if (petB != null && !petB.isDefeated()) survivors.add(petB);
        MissionResult.Outcome outcome = threat.isDefeated()
                ? MissionResult.Outcome.VICTORY
                : MissionResult.Outcome.DEFEAT;
        return new MissionResult(outcome, log, survivors);
    }
}