package com.example.spacepetrescue.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepetrescue.R;
import com.example.spacepetrescue.model.CrewMember;
import com.example.spacepetrescue.model.MissionControl;
import com.example.spacepetrescue.model.MissionTemplate;
import com.example.spacepetrescue.model.Storage;
import com.example.spacepetrescue.model.Threat;

import java.util.ArrayList;
import java.util.List;


public class MissionControlActivity extends AppCompatActivity
        implements CrewMemberAdapter.OnPetSelectedListener {
    private Storage storage;
    private MissionControl missionControl;
    private RecyclerView rvMissionPets;
    private CrewMemberAdapter adapter;
    private TextView tvSelectionHint;
    private Button btnLaunch;
    private View layoutBattle;
    private View layoutSelection;
    private TextView tvThreatName;
    private ProgressBar pbThreat;
    private TextView tvThreatHp;
    private TextView tvPetAName;
    private ProgressBar pbPetA;
    private TextView tvPetAHp;

    private TextView tvPetBName;
    private ProgressBar pbPetB;
    private TextView tvPetBHp;

    private TextView tvActivePet;
    private ScrollView svLog;
    private TextView tvBattleLog;

    private Button btnAttack;
    private Button btnSpecial;
    private Button btnEndMission;

    private CrewMember petA;
    private CrewMember petB;
    private Spinner spnMissionLevels;
    private List<MissionTemplate> missionLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        storage = Storage.getInstance();
        missionControl = new MissionControl();

        layoutSelection = findViewById(R.id.layoutMissionSelection);
        layoutBattle = findViewById(R.id.layoutBattleArena);

        rvMissionPets = findViewById(R.id.rvMissionPets);
        tvSelectionHint = findViewById(R.id.tvSelectionHint);
        btnLaunch = findViewById(R.id.btnLaunchMission);
        spnMissionLevels = findViewById(R.id.spnMissionLevels);

        tvThreatName = findViewById(R.id.tvThreatName);
        pbThreat = findViewById(R.id.pbThreat);
        tvThreatHp = findViewById(R.id.tvThreatHp);

        tvPetAName = findViewById(R.id.tvPetAName);
        pbPetA = findViewById(R.id.pbPetA);
        tvPetAHp = findViewById(R.id.tvPetAHp);

        tvPetBName = findViewById(R.id.tvPetBName);
        pbPetB = findViewById(R.id.pbPetB);
        tvPetBHp = findViewById(R.id.tvPetBHp);

        tvActivePet = findViewById(R.id.tvActivePet);
        svLog = findViewById(R.id.svBattleLog);
        tvBattleLog = findViewById(R.id.tvBattleLog);

        btnAttack = findViewById(R.id.btnAttack);
        btnSpecial = findViewById(R.id.btnSpecial);
        btnEndMission = findViewById(R.id.btnEndMission);
        Button btnMissionBack = findViewById(R.id.btnMissionBack);

        rvMissionPets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter(getMissionPets(), this, true);
        rvMissionPets.setAdapter(adapter);

        setupMissionTemplates();

        layoutBattle.setVisibility(View.GONE);

        spnMissionLevels.setVisibility(View.GONE);
        btnLaunch.setText("NEXT: CHOOSE MISSION");
        btnLaunch.setEnabled(false);

        btnLaunch.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.size() == 2) {
                if (spnMissionLevels.getVisibility() == View.GONE) {
                    spnMissionLevels.setVisibility(View.VISIBLE);
                    rvMissionPets.setVisibility(View.GONE);
                    tvSelectionHint.setText("Great! Now choose your destination: ✨");
                    btnLaunch.setText("LAUNCH MISSION 🚀");
                    btnLaunch.setBackgroundTintList(getColorStateList(R.color.pink_accent));
                } else {
                    petA = selected.get(0);
                    petB = selected.get(1);
                    MissionTemplate selectedTemplate = (MissionTemplate) spnMissionLevels.getSelectedItem();
                    startBattle(selectedTemplate);
                }
            } else {
                Toast.makeText(this, "Please choose 2 pets first! 🐾🐾", Toast.LENGTH_SHORT).show();
            }
        });

        btnAttack.setOnClickListener(v -> doTurn(MissionControl.Action.ATTACK));
        btnSpecial.setOnClickListener(v -> doTurn(MissionControl.Action.SPECIAL));

        btnEndMission.setOnClickListener(v -> endAndReturn());
        btnMissionBack.setOnClickListener(v -> finish());
    }

    private void startBattle(MissionTemplate template) {
        missionControl.launchMission(petA, petB, template);
        Threat threat = missionControl.getThreat();

        pbThreat.setMax(threat.getMaxEnergy());
        pbPetA.setMax(petA.getMaxEnergy());
        pbPetB.setMax(petB.getMaxEnergy());

        layoutSelection.setVisibility(View.GONE);
        layoutBattle.setVisibility(View.VISIBLE);
        btnEndMission.setVisibility(View.GONE);
        btnAttack.setEnabled(true);
        btnSpecial.setEnabled(true);

        updateBattleUI();
    }

    private void doTurn(MissionControl.Action action) {
        boolean ongoing = missionControl.takeTurn(action);
        updateBattleUI();
        appendLog();
        if (!ongoing) {
            endBattle();
        }
    }

    private void endBattle() {
        btnAttack.setEnabled(false);
        btnSpecial.setEnabled(false);
        btnEndMission.setVisibility(View.VISIBLE);

        String result = missionControl.getThreat().isDefeated()
                ? "🎉 AMAZING! Your pets saved the day!"
                : "💀 Oh no... Your pets need some rest.";
        tvActivePet.setText(result);
        tvActivePet.setTextColor(missionControl.getThreat().isDefeated()
                ? Color.GREEN : Color.RED);
    }

    private void endAndReturn() {

        finish();
    }

    private void updateBattleUI() {
        Threat t = missionControl.getThreat();

        tvThreatName.setText("⚠ " + t.getName());
        pbThreat.setProgress(t.getEnergy());
        tvThreatHp.setText(t.getEnergy() + "/" + t.getMaxEnergy());

        tvPetAName.setText(petA.getSpecialization() + " " + petA.getName());
        pbPetA.setProgress(petA.getEnergy());
        tvPetAHp.setText(petA.getEnergy() + "/" + petA.getMaxEnergy());

        tvPetBName.setText(petB.getSpecialization() + " " + petB.getName());
        pbPetB.setProgress(petB.getEnergy());
        tvPetBHp.setText(petB.getEnergy() + "/" + petB.getMaxEnergy());

        if (missionControl.isMissionActive()) {
            CrewMember active = missionControl.getActivePet();
            if (active != null) {
                tvActivePet.setText("⭐ It's"  + active.getName() + "'s turn! " + " (" + active.getSpecialization() + ")");
                tvActivePet.setTextColor(Color.WHITE);
            }
        }

        btnSpecial.setText("⚡ Special: " + getSpecialName(missionControl.isMissionActive() ? missionControl.getActivePet() : petA));
    }

    private void appendLog() {
        List<String> lines = missionControl.getLog();
        StringBuilder sb = new StringBuilder();
        for (String line : lines) sb.append(line).append("\n");
        tvBattleLog.setText(sb.toString());
        svLog.post(() -> svLog.fullScroll(View.FOCUS_DOWN));
    }

    private String getSpecialName(CrewMember cm) {
        if (cm == null)
            return "Special";
        switch (cm.getSpecialization()) {
            case "Pilot":
                return "Maneuver";
            case "Engineer":
                return "Repair";
            case "Medic":
                return "Heal";
            case "Scientist":
                return "Pierce";
            case "Soldier":
                return "Power Strike";
            default:
                return "Special";
        }
    }

    private List<CrewMember> getMissionPets() {
        return storage.listByLocation(CrewMember.LOCATION_MISSION);
    }

    @Override
    public void onPetSelected(CrewMember cm, boolean checked) {
        List<CrewMember> selected = adapter.getSelected();
        tvSelectionHint.setText("Selected: " + selected.size() + "/2");
        btnLaunch.setEnabled(selected.size() == 2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!missionControl.isMissionActive()) {
            adapter.updateData(getMissionPets());
        }
    }

    private void setupMissionTemplates() {
        missionLevels = new ArrayList<>();
        missionLevels.add(new MissionTemplate("🛡️ Patrol", "Easy mission for beginners", 1.0, 1));
        missionLevels.add(new MissionTemplate("🛸 Alien Raid", "Moderate difficulty", 1.5, 2));
        missionLevels.add(new MissionTemplate("🕳️ Black Hole", "Extremely dangerous!", 2.5, 5));
        ArrayAdapter<MissionTemplate> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, missionLevels);
        spnMissionLevels.setAdapter(adapter);
    }
}
