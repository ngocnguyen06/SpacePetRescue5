package com.example.spacepetrescue.model;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Central data store for all crew members.
 * Uses HashMap<Integer, CrewMember> so we can look up by ID quickly.
 * Also handles saving/loading to a file (bonus: Data Persistence).
 */
public class Storage {

    private static final String TAG = "Storage";
    private static final String FILE_NAME = "crew_data.dat";

    // ── Singleton ─────────────────────────────────────────────────────────
    private static Storage instance;

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage("Space Pet Rescue Academy");
        }
        return instance;
    }

    // ── Fields ────────────────────────────────────────────────────────────
    private String name;
    private HashMap<Integer, CrewMember> crewMap;

    // Mission counter drives threat difficulty scaling
    private int missionCount;

    // ── Constructor ───────────────────────────────────────────────────────
    private Storage(String name) {
        this.name = name;
        this.crewMap = new HashMap<>();
        this.missionCount = 0;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

    /** Add a new crew member to storage. */
    public void addCrewMember(CrewMember cm) {
        crewMap.put(cm.getId(), cm);
    }

    /** Retrieve a crew member by ID. */
    public CrewMember getCrewMember(int id) {
        return crewMap.get(id);
    }

    /** Remove a crew member (e.g. when "killed" with no-death feature off). */
    public void removeCrewMember(int id) {
        crewMap.remove(id);
    }

    /** List ALL crew members. */
    public List<CrewMember> listCrewMembers() {
        return new ArrayList<>(crewMap.values());
    }

    /** List crew members by location. */
    public List<CrewMember> listByLocation(String location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMap.values()) {
            if (location.equals(cm.getCurrentLocation())) {
                result.add(cm);
            }
        }
        return result;
    }

    // ── Mission counter ───────────────────────────────────────────────────
    public int  getMissionCount() {
        return missionCount;
    }
    public void incrementMissionCount(){
        missionCount++;
    }

    // ── File persistence (bonus feature) ──────────────────────────────────

    /**
     * Saves the entire crew map to internal storage.
     * Uses Java serialization (simple enough for a university project).
     */
    public void saveToFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(crewMap);
            oos.writeInt(missionCount);
            oos.writeInt(CrewMember.getNumberOfCreated()); // save ID counter too
            oos.close();
            Log.d(TAG, "Game saved successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Save failed: " + e.getMessage());
        }
    }

    /**
     * Loads crew data from internal storage.
     * Returns true if load was successful.
     */
    @SuppressWarnings("unchecked")
    public boolean loadFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            crewMap      = (HashMap<Integer, CrewMember>) ois.readObject();
            missionCount = ois.readInt();
            int savedIdCounter = ois.readInt();
            CrewMember.setIdCounter(savedIdCounter + 1);
            ois.close();
            Log.d(TAG, "Game loaded successfully. Crew size: " + crewMap.size());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Load failed: " + e.getMessage());
            return false;
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────
    public String getName() { return name; }

    public int getTotalCrew() { return crewMap.size(); }
}