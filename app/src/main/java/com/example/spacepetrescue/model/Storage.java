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
public class Storage {
    private static final String TAG = "Storage";
    private static final String FILE_NAME = "crew_data.dat";
    private static Storage instance;
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage("Space Pet Rescue Academy");
        }
        return instance;
    }
    private String name;
    private HashMap<Integer, CrewMember> crewMap;
    private int missionCount;
    private Storage(String name) {
        this.name = name;
        this.crewMap = new HashMap<>();
        this.missionCount = 0;
    }
    public void addCrewMember(CrewMember cm) {
        crewMap.put(cm.getId(), cm);
    }
    public CrewMember getCrewMember(int id) {
        return crewMap.get(id);
    }

    public void removeCrewMember(int id) {
        crewMap.remove(id);
    }
    public List<CrewMember> listCrewMembers() {
        return new ArrayList<>(crewMap.values());
    }

    public List<CrewMember> listByLocation(String location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMap.values()) {
            if (location.equals(cm.getCurrentLocation())) {
                result.add(cm);
            }
        }
        return result;
    }

    public int  getMissionCount() {
        return missionCount;
    }
    public void incrementMissionCount(){
        missionCount++;
    }
    public void saveToFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(crewMap);
            oos.writeInt(missionCount);
            oos.writeInt(CrewMember.getNumberOfCreated());
            oos.close();
            Log.d(TAG, "Game saved successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Save failed: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    public boolean loadFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            crewMap = (HashMap<Integer, CrewMember>) ois.readObject();
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
    public String getName() { return name; }
    public int getTotalCrew() { return crewMap.size(); }
}