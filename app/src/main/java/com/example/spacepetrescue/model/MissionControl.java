package com.example.spacepetrescue.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissionControl {
    public enum Action { ATTACK, SPECIAL }
    private Storage storage;
    private List<String> log;
    private CrewMember petA;
    private CrewMember petB;
    private Threat threat;
    private boolean missionActive;
    private int currentTurn;
    private int round;
    private Random random = new Random();
    private MissionTemplate currentMissionTemplate;

    public MissionControl() {
        this.storage = Storage.getInstance();
        this.log = new ArrayList<>();
    }

    public void launchMission(CrewMember a, CrewMember b) {
        launchMission(a, b, null);
    }

    public void launchMission(CrewMember a, CrewMember b, MissionTemplate template) {
        this.petA = a;
        this.petB = b;
        this.currentMissionTemplate = template;
        double multiplier = (template != null) ? template.getDifficultyMultiplier() : 1.0;
        this.threat = new Threat(storage.getMissionCount(), multiplier);
        this.missionActive = true;
        this.currentTurn = 0;
        this.round = 1;
        this.log = new ArrayList<>();

        log.add("🎀 MISSION START 🎀");
        log.add("Threat: " + threat.getName());
        if (template != null) {
            log.add("Mission: " + template.getTitle());
        }
        log.add(petA.toString());
        log.add(petB.toString());
        log.add("--- Round " + round + " ---");
    }

    private void awardXPIfAlive(CrewMember pet) {
        if (pet != null && !pet.isDefeated()) {
            int reward = currentMissionTemplate != null ? currentMissionTemplate.getXpReward() : 1;
            pet.gainExperience(reward);
            pet.incrementMissionsCompleted();
            log.add("✨ " + pet.getName() + " earns " + reward + " XP!");
        }
    }

    public boolean takeTurn(Action action) {
        if (!missionActive) return false;

        CrewMember active = getActivePet();
        if (active == null || active.isDefeated()) {
            advanceTurn();
            return !checkMissionEnd();
        }

        int bonusDmg = 0;
        if ("Soldier".equals(active.getSpecialization())) {
            bonusDmg = 2;
            log.add("✨ Soldier " + active.getName() + " feels brave! (+2 DMG)");
        }

        int variance = random.nextInt(5) - 2;
        if (action == Action.SPECIAL) {
            CrewMember ally = (currentTurn == 0) ? petB : petA;
            if (ally == null || ally.isDefeated()) ally = active;
            String specialLog = active.useSpecialAbility(ally, threat);
            log.add(specialLog);
        } else {
            int dmg = Math.max(1, active.act() + bonusDmg + variance);
            int dealt = threat.receiveAttack(dmg);
            log.add("⭐ " + active.getName() + " uses " + getCuteAttackName() + "! → "
                    + dealt + " dmg [" + threat.getEnergy() + "/" + threat.getMaxEnergy() + " HP]");
        }
        if (threat.isDefeated()) {
            endMissionVictory();
            return false;
        }
        int thrDealt = threat.attack(active);
        log.add("💢 " + threat.getName() + " retaliates → " + thrDealt
                + " damage on " + active.getName()
                + " [" + active.getEnergy() + "/" + active.getMaxEnergy() + " HP]");
        if (active.isDefeated()) {
            handleDefeatedPet(active);
        }

        if (checkMissionEnd()) return false;

        advanceTurn();
        return true;
    }

    private String getCuteAttackName() {
        String[] attacks = {"Sparkle Blast", "Paw Punch", "Stellar Boop", "Cosmic Hug", "Rainbow Beam"};
        return attacks[random.nextInt(attacks.length)];
    }

    private void advanceTurn() {
        currentTurn = (currentTurn == 0) ? 1 : 0;
        if (currentTurn == 0) {
            round++;
            log.add("--- Round " + round + " ---");
        }
    }

    private void handleDefeatedPet(CrewMember pet) {
        log.add("🩹 " + pet.getName() + " is tired! Sending to Medbay to rest...");
        pet.sendToMedbay();
        storage.addCrewMember(pet);
        if (pet == petA) petA = null;
        else petB = null;
    }

    private boolean checkMissionEnd() {
        if (threat.isDefeated()) {
            endMissionVictory();
            return true;
        }
        boolean allPetsDown = (petA == null || petA.isDefeated())
                && (petB == null || petB.isDefeated());
        if (allPetsDown) {
            endMissionDefeat();
            return true;
        }
        return false;
    }

    private void endMissionVictory() {
        missionActive = false;
        log.add("🎉 MISSION COMPLETE 🎉");
        log.add(threat.getName() + " has been befriended/neutralized!");
        storage.incrementMissionCount();
        awardXPIfAlive(petA);
        awardXPIfAlive(petB);
    }

    private void endMissionDefeat() {
        missionActive = false;
        log.add("🛸 MISSION FAILED 🛸");
        log.add("The pets had to retreat!");
    }

    public Threat getThreat() {
        return threat;
    }
    public boolean isMissionActive() {
        return missionActive;
    }
    public List<String> getLog() {
        return log;
    }
    public CrewMember getActivePet() {
        return currentTurn == 0 ? petA : petB;
    }

    public MissionResult runMissionAuto() {
        while (missionActive) {
            takeTurn(Action.ATTACK);
        }
        return buildResult();
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
