package com.example.spacepetrescue.ui;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacepetrescue.R;
import com.example.spacepetrescue.model.CrewMember;
import com.example.spacepetrescue.model.CrewMemberFactory;
import com.example.spacepetrescue.model.Quarters;

/**
 * Recruitment Screen – name a pet and pick a specialization.
 * Shows the base stats for the selected specialization so the player
 * can make an informed choice.
 */
public class RecruitmentActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spSpecialization;
    private TextView tvStats;
    private Button btnCreate;
    private Button btnCancel;

    private Quarters quarters;

    // Stat display for each spec (matches Table 1 from instructions)
    private static final String[] STAT_DESCRIPTIONS = {
            "Pilot     | Skill: 5  | Resilience: 4  | Energy: 20  🔵",
            "Engineer  | Skill: 6  | Resilience: 3  | Energy: 19  🟡",
            "Medic     | Skill: 7  | Resilience: 2  | Energy: 18  🟢",
            "Scientist | Skill: 8  | Resilience: 1  | Energy: 17  🟣",
            "Soldier   | Skill: 9  | Resilience: 0  | Energy: 16  🔴"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment);

        quarters = new Quarters();

        etName = findViewById(R.id.etPetName);
        spSpecialization = findViewById(R.id.spSpecialization);
        tvStats = findViewById(R.id.tvSpecStats);
        btnCreate = findViewById(R.id.btnCreatePet);
        btnCancel = findViewById(R.id.btnCancelRecruitment);

        // Populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CrewMemberFactory.ALL_SPECS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpecialization.setAdapter(adapter);

        // Update stat display when spinner changes
        spSpecialization.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int pos, long id) {
                        tvStats.setText(STAT_DESCRIPTIONS[pos]);
                    }
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });

        // Show first stats immediately
        tvStats.setText(STAT_DESCRIPTIONS[0]);

        // Create button
        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a name!", Toast.LENGTH_SHORT).show();
                return;
            }
            String spec = (String) spSpecialization.getSelectedItem();
            CrewMember cm = CrewMemberFactory.create(name, spec);
            if (cm != null) {
                quarters.createCrewMember(cm);
                Toast.makeText(this,
                        spec + " '" + name + "' joined the crew! 🐾",
                        Toast.LENGTH_SHORT).show();
                finish(); // go back to overview
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}