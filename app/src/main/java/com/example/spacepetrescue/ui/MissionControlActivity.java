package com.example.spacepetrescue.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepetrescue.R;
import com.example.spacepetrescue.model.CrewMember;
import com.example.spacepetrescue.model.MissionControl;
import com.example.spacepetrescue.model.Storage;
import com.example.spacepetrescue.model.Threat;

import java.util.List;

/**
 * Mission Control – select two pets and run a tactical turn-based mission.
 *
 * Tactical Combat bonus: each turn the player picks ATTACK or SPECIAL.
 * Mission Visualization bonus: health bars update dynamically.
 */
public class MissionControlActivity extends AppCompatActivity
        implements CrewMemberAdapter.OnPetSelectedListener {

    // ── Model ─────────────────────────────────────────────────────────────
    private Storage storage;
    private MissionControl missionControl;

    // ── Views – selection phase ────────────────────────────────────────────
    private RecyclerView rvMissionPets;
    private CrewMemberAdapter adapter;
    private TextView tvSelectionHint;
    private Button btnLaunch;

    // ── Views – battle phase ───────────────────────────────────────────────
    private View layoutBattle;
    private View layoutSelection;

    /**
     *
     */
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

    // ── State ─────────────────────────────────────────────────────────────
    private CrewMember petA;
    private CrewMember petB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        storage = Storage.getInstance();
        missionControl = new MissionControl();

        // Selection phase views
        layoutSelection = findViewById(R.id.layoutMissionSelection);
        layoutBattle = findViewById(R.id.layoutBattleArena);

        rvMissionPets = findViewById(R.id.rvMissionPets);
        tvSelectionHint = findViewById(R.id.tvSelectionHint);
        btnLaunch = findViewById(R.id.btnLaunchMission);

        // Battle views
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

        // RecyclerView
        rvMissionPets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter(getMissionPets(), this, true);
        rvMissionPets.setAdapter(adapter);

        layoutBattle.setVisibility(View.GONE);

        // Launch mission button
        btnLaunch.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.size() != 2) {
                Toast.makeText(this, "Select exactly 2 pets!", Toast.LENGTH_SHORT).show();
                return;
            }
            petA = selected.get(0);
            petB = selected.get(1);
            startBattle();
        });

        // Tactical action buttons
        btnAttack.setOnClickListener(v -> doTurn(MissionControl.Action.ATTACK));
        btnSpecial.setOnClickListener(v -> doTurn(MissionControl.Action.SPECIAL));

        // End / return button
        btnEndMission.setOnClickListener(v -> endAndReturn());
        btnMissionBack.setOnClickListener(v -> finish());
    }

    // ── Battle start ──────────────────────────────────────────────────────

    private void startBattle() {
        missionControl.launchMission(petA, petB);
        Threat threat = missionControl.getThreat();

        // Setup health bars
        pbThreat.setMax(threat.getMaxEnergy());
        pbPetA.setMax(petA.getMaxEnergy());
        pbPetB.setMax(petB.getMaxEnergy());

        // Swap views
        layoutSelection.setVisibility(View.GONE);
        layoutBattle.setVisibility(View.VISIBLE);
        btnEndMission.setVisibility(View.GONE);
        btnAttack.setEnabled(true);
        btnSpecial.setEnabled(true);

        updateBattleUI();
    }

    // ── Turn execution ─────────────────────────────────────────────────────

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
                ? "🎉 VICTORY! Mission Complete!"
                : "💀 DEFEAT. Pets sent to Medbay.";
        tvActivePet.setText(result);
        tvActivePet.setTextColor(missionControl.getThreat().isDefeated()
                ? Color.GREEN : Color.RED);
    }

    private void endAndReturn() {
        // Move surviving pets back to Mission Control (they stay there awaiting next mission)
        // Already handled by MissionControl, just navigate back
        finish();
    }

    // ── UI update ──────────────────────────────────────────────────────────

    private void updateBattleUI() {
        Threat t = missionControl.getThreat();

        // Threat bar
        tvThreatName.setText("⚠ " + t.getName());
        pbThreat.setProgress(t.getEnergy());
        tvThreatHp.setText(t.getEnergy() + "/" + t.getMaxEnergy());

        // Pet A bar
        tvPetAName.setText(petA.getSpecialization() + " " + petA.getName());
        pbPetA.setProgress(petA.getEnergy());
        tvPetAHp.setText(petA.getEnergy() + "/" + petA.getMaxEnergy());

        // Pet B bar
        tvPetBName.setText(petB.getSpecialization() + " " + petB.getName());
        pbPetB.setProgress(petB.getEnergy());
        tvPetBHp.setText(petB.getEnergy() + "/" + petB.getMaxEnergy());

        // Active pet indicator
        if (missionControl.isMissionActive()) {
            CrewMember active = missionControl.getActivePet();
            if (active != null) {
                tvActivePet.setText("Your turn: " + active.getName()
                        + " (" + active.getSpecialization() + ")");
                tvActivePet.setTextColor(Color.WHITE);
            }
        }

        btnSpecial.setText("⚡ Special: " + getSpecialName(
                missionControl.isMissionActive() ? missionControl.getActivePet() : petA));
    }

    private void appendLog() {
        List<String> lines = missionControl.getLog();
        StringBuilder sb = new StringBuilder();
        for (String line : lines) sb.append(line).append("\n");
        tvBattleLog.setText(sb.toString());
        // Auto-scroll to bottom
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
}
