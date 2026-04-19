package com.example.spacepetrescue.ui;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
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
        com.example.spacepetrescue.util.LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(android.R.id.content);
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFFFCE4EC, 0xFFFFC1E3});
        mainLayout.setBackground(gd);
        storage = Storage.getInstance();

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

        btnRecruitment.setOnClickListener(v ->
                startActivity(new Intent(this, RecruitmentActivity.class)));
        btnQuarters.setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class)));
        btnSimulator.setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));
        btnMission.setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));

        btnSave.setOnClickListener(v -> {
            storage.saveToFile(this);
            Toast.makeText(this, "Progress saved! 🎀✨", Toast.LENGTH_SHORT).show();
        });
        btnLoad.setOnClickListener(v -> {
            boolean ok = storage.loadFromFile(this);
            if (ok) {
                Toast.makeText(this, "Welcome back! Data loaded! 🐾✅", Toast.LENGTH_SHORT).show();
                updateCounts();
            } else {
                Toast.makeText(this, "No save file found yet. 🛸", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyCuteBackground() {
        View mainLayout = findViewById(R.id.main);
        if (mainLayout != null) {
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFFFCE4EC, 0xFFFFC1E3});
            mainLayout.setBackground(gd);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounts();
    }

    private void updateCounts() {
        tvQuartersCount.setText("🐾 Napping in Cozy Quarters: " + storage.listByLocation(CrewMember.LOCATION_QUARTERS).size());
        tvSimulatorCount.setText("✨ Training in Training Room: " + storage.listByLocation(CrewMember.LOCATION_SIMULATOR).size());
        tvMissionCount.setText("🚀 Ready for Missions: " + storage.listByLocation(CrewMember.LOCATION_MISSION).size());
        tvMedbayCount.setText("🩹 Resting in Medbay: " + storage.listByLocation(CrewMember.LOCATION_MEDBAY).size());

        int totalMissions = storage.getMissionCount();
        String motivation = (totalMissions > 0) ? "The galaxy is safer thanks to you! 🎀" : "Ready for your first adventure? ✨";
        tvMissionsTotal.setText("Successful Missions: " + totalMissions + "\n" + motivation);
    }
}