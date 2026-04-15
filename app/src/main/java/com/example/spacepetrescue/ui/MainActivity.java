package com.example.spacepetrescue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacepetrescue.R;
import com.example.spacepetrescue.model.CrewMember;
import com.example.spacepetrescue.model.Storage;

public class MainActivity extends AppCompatActivity {

    private Storage storage;
    private TextView tvQuartersCount;
    private TextView tvSimulatorCount;
    private TextView tvMissionCount;
    private TextView tvMedbayCount;
    private TextView tvMissionsTotal;
    private Button btnRecruitment;
    private Button btnQuarters;
    private Button btnSimulator;
    private Button btnMission;
    private Button btnSave;
    private Button btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = Storage.getInstance();

        // Find views
        tvQuartersCount = findViewById(R.id.tvQuartersCount);
        tvSimulatorCount = findViewById(R.id.tvSimulatorCount);
        tvMissionCount = findViewById(R.id.tvMissionControlCount);
        tvMedbayCount = findViewById(R.id.tvMedbayCount);
        tvMissionsTotal = findViewById(R.id.tvMissionsTotal);

        btnRecruitment = findViewById(R.id.btnGoRecruitment);
        btnQuarters = findViewById(R.id.btnGoQuarters);
        btnSimulator = findViewById(R.id.btnGoSimulator);
        btnMission = findViewById(R.id.btnGoMission);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);

        // Navigation
        btnRecruitment.setOnClickListener(v ->
                startActivity(new Intent(this, RecruitmentActivity.class)));
        btnQuarters.setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class)));
        btnSimulator.setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));
        btnMission.setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));

        // Save / Load
        btnSave.setOnClickListener(v -> {
            storage.saveToFile(this);
            Toast.makeText(this, "Game saved! 💾", Toast.LENGTH_SHORT).show();
        });
        btnLoad.setOnClickListener(v -> {
            boolean ok = storage.loadFromFile(this);
            Toast.makeText(this,
                    ok ? "Game loaded! ✅" : "No save file found.",
                    Toast.LENGTH_SHORT).show();
            updateCounts();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounts();
    }

    private void updateCounts() {
        tvQuartersCount.setText("🏠 Quarters: " + storage.listByLocation(CrewMember.LOCATION_QUARTERS).size());
        tvSimulatorCount.setText("🏋️ Simulator: " + storage.listByLocation(CrewMember.LOCATION_SIMULATOR).size());
        tvMissionCount.setText("🚀 Mission Control: " + storage.listByLocation(CrewMember.LOCATION_MISSION).size());
        tvMedbayCount.setText("🏥 Medbay: " + storage.listByLocation(CrewMember.LOCATION_MEDBAY).size());
        tvMissionsTotal.setText("Missions completed: " + storage.getMissionCount());
    }
}