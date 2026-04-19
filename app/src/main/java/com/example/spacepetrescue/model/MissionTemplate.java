package com.example.spacepetrescue.model;
import java.io.Serializable;
public class MissionTemplate implements Serializable {
    private String title;
    private String description;
    private double difficultyMultiplier;
    private int xpReward;
    public MissionTemplate(String title, String description, double difficultyMultiplier, int xpReward) {
        this.title = title;
        this.description = description;
        this.difficultyMultiplier = difficultyMultiplier;
        this.xpReward = xpReward;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public double getDifficultyMultiplier() {
        return difficultyMultiplier;
    }
    public int getXpReward() {
        return xpReward;
    }
    @Override
    public String toString() {
        return title + " (x" + difficultyMultiplier + ")";
    }
}