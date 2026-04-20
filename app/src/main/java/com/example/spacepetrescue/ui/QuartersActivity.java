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

public class QuartersActivity extends AppCompatActivity
        implements CrewMemberAdapter.OnPetSelectedListener {
    private Storage storage;
    private CrewMemberAdapter adapter;
    private TextView tvEmpty;
    private Button btnSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.example.spacepetrescue.util.LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        btnSelectAll = findViewById(R.id.btnSelectAll);
        storage = Storage.getInstance();

        RecyclerView rv = findViewById(R.id.rvQuarters);
        tvEmpty = findViewById(R.id.tvQuartersEmpty);
        Button btnSimulator = findViewById(R.id.btnMoveToSimulator);
        Button btnMission = findViewById(R.id.btnMoveToMission);
        Button btnBack = findViewById(R.id.btnQuartersBack);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter(getResidents(), this, true);
        rv.setAdapter(adapter);
        updateEmptyView();

        btnSimulator.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelected();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select at least one pet first! \uD83D\uDC3E", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember cm : selected) {
                cm.setCurrentLocation(CrewMember.LOCATION_SIMULATOR);
            }
            Toast.makeText(this,
                    selected.size() + " pet(s) are heading to training! 🏋️",
                    Toast.LENGTH_SHORT).show();
            refresh();
        });

        Button btnSelectAll = findViewById(R.id.btnSelectAll);
        btnSelectAll.setOnClickListener(v -> {
            adapter.selectAll();
            Toast.makeText(this, "All pets selected! 🐾", Toast.LENGTH_SHORT).show();
        });

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
                    selected.size() + " pet(s) are ready for adventure! 🚀",
                    Toast.LENGTH_SHORT).show();
            refresh();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onPetSelected(CrewMember cm, boolean checked) {
    }

    private List<CrewMember> getResidents() {
        return storage.listByLocation(CrewMember.LOCATION_QUARTERS);
    }

    private void refresh() {
        adapter.updateData(getResidents());
        updateEmptyView();
    }

    private void updateEmptyView() {
        boolean isEmpty = getResidents().isEmpty();
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (btnSelectAll != null) {
            btnSelectAll.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}