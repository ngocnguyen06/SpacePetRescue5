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
import com.example.spacepetrescue.model.Simulator;
import com.example.spacepetrescue.model.Storage;

import java.util.List;

public class SimulatorActivity extends AppCompatActivity
        implements CrewMemberAdapter.OnPetSelectedListener {

    private Storage storage;
    private Simulator simulator;
    private CrewMemberAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        storage = Storage.getInstance();
        simulator = new Simulator();

        RecyclerView rv = findViewById(R.id.rvSimulator);
        tvEmpty = findViewById(R.id.tvSimulatorEmpty);
        Button btnTrain = findViewById(R.id.btnTrain);
        Button btnToHome = findViewById(R.id.btnSimToQuarters);
        Button btnBack = findViewById(R.id.btnSimulatorBack);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter(getTrainees(), this, true);
        rv.setAdapter(adapter);
        updateEmptyView();

        // Train selected pets
        btnTrain.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select pets to train!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember cm : selected) {
                simulator.train(cm);
            }
            Toast.makeText(this,
                    selected.size() + " pet(s) trained! +1 XP each 🌟",
                    Toast.LENGTH_SHORT).show();
            adapter.clearSelection();
            adapter.notifyDataSetChanged();
        });

        // Send back to Quarters (restores energy)
        btnToHome.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select pets to send home!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember cm : selected) {
                cm.restoreEnergy();
                cm.setCurrentLocation(CrewMember.LOCATION_QUARTERS);
            }
            Toast.makeText(this,
                    selected.size() + " pet(s) sent home 🏠 (energy restored)",
                    Toast.LENGTH_SHORT).show();
            refresh();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onPetSelected(CrewMember cm, boolean checked) {}

    private List<CrewMember> getTrainees() {
        return storage.listByLocation(CrewMember.LOCATION_SIMULATOR);
    }

    private void refresh() {
        adapter.updateData(getTrainees());
        updateEmptyView();
    }

    private void updateEmptyView() {
        tvEmpty.setVisibility(getTrainees().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}