package com.example.spacepetrescue.model;

import java.util.List;

/**
 * Simple data holder returned by MissionControl.runMission().
 * The Activity reads this to display the battle log and outcome.
 */
public class MissionResult {

    public enum Outcome { VICTORY, DEFEAT }
    private Outcome outcome;
    private List<String> log;         // line-by-line battle log
    private List<CrewMember> survivors; // crew still alive after mission

    public MissionResult(Outcome outcome, List<String> log, List<CrewMember> survivors) {
        this.outcome = outcome;
        this.log = log;
        this.survivors = survivors;
    }
    public Outcome getOutcome() {
        return outcome;
    }
    public List<String> getLog() {
        return log;
    }
    public List<CrewMember> getSurvivors() {
        return survivors;
    }
    public boolean isVictory() {
        return outcome == Outcome.VICTORY;
    }
}