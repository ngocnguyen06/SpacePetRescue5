package com.example.spacepetrescue.ui;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepetrescue.R;
import com.example.spacepetrescue.adapter.CrewMemberAdapter;
import com.example.spacepetrescue.model.CrewMember;
import com.example.spacepetrescue.model.Storage;

import java.util.List;

/**
 * Quarters View – shows all pets at home.
 * Player can select pets and move them to Simulator or Mission Control.
 */
public class QuartersActivity extends AppCompatActivity
        implements CrewMemberAdapter.OnPetSelectedListener {

    private Storage storage;
    private CrewMemberAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        storage = Storage.getInstance();

        RecyclerView rv  = findViewById(R.id.rvQuarters);
        tvEmpty = findViewById(R.id.tvQuartersEmpty);
        Button btnSimulator = findViewById(R.id.btnMoveToSimulator);
        Button btnMission = findViewById(R.id.btnMoveToMission);
        Button btnBack = findViewById(R.id.btnQuartersBack);

        // RecyclerView setup
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter(getResidents(), this, true);
        rv.setAdapter(adapter);
        updateEmptyView();

        // Move to Simulator
        btnSimulator.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select at least one pet first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember cm : selected) {
                cm.setCurrentLocation(CrewMember.LOCATION_SIMULATOR);
            }
            Toast.makeText(this,
                    selected.size() + " pet(s) moved to Simulator 🏋️",
                    Toast.LENGTH_SHORT).show();
            refresh();
        });

        // Move to Mission Control
        btnMission.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select at least one pet first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember cm : selected) {
                cm.setCurrentLocation(CrewMember.LOCATION_MISSION);
            }
            Toast.makeText(this,
                    selected.size() + " pet(s) moved to Mission Control 🚀",
                    Toast.LENGTH_SHORT).show();
            refresh();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onPetSelected(CrewMember cm, boolean checked) {
        // adapter handles internal selection state
    }

    private List<CrewMember> getResidents() {
        return storage.listByLocation(CrewMember.LOCATION_QUARTERS);
    }

    private void refresh() {
        adapter.updateData(getResidents());
        updateEmptyView();
    }

    private void updateEmptyView() {
        tvEmpty.setVisibility(getResidents().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}