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

import java.util.Random;

public class RecruitmentActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spSpecialization;
    private TextView tvStats;
    private Button btnCreate;
    private Button btnCancel;
    private Button btnRandomName;

    private Quarters quarters;

    private String[] cuteNames = {"Marshmallow", "Boba", "Pudding", "Mochi", "Pixel", "Rocket",
            "Cookie", "Pancake", "Bubble", "Oreo", "Peanut", "Coffee", "Meatball", "Dumpling",
            "Waffle", "Candy", "Donut", "Honey", "Nugget", "Berry", "Cinnamon"};

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
        btnRandomName = findViewById(R.id.btnRandomName);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CrewMemberFactory.ALL_SPECS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpecialization.setAdapter(adapter);

        spSpecialization.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int pos, long id) {
                        tvStats.setText(STAT_DESCRIPTIONS[pos]);
                    }
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });

        tvStats.setText(STAT_DESCRIPTIONS[0]);

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Give your pet a name! 🐾", Toast.LENGTH_SHORT).show();
                return;
            }
            String spec = (String) spSpecialization.getSelectedItem();
            CrewMember cm = CrewMemberFactory.create(name, spec);
            if (cm != null) {
                quarters.createCrewMember(cm);
                Toast.makeText(this, spec + " \uD83C\uDF1F" + name + " has joined the crew! 🐾",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnRandomName.setOnClickListener(v -> {
            int index = new Random().nextInt(cuteNames.length);
            etName.setText(cuteNames[index]);
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
