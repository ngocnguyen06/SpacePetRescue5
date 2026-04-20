package com.example.spacepetrescue.ui;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepetrescue.R;
import com.example.spacepetrescue.model.CrewMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrewMemberAdapter
        extends RecyclerView.Adapter<CrewMemberAdapter.PetViewHolder> {

    public interface OnPetSelectedListener {
        void onPetSelected(CrewMember cm, boolean checked);
    }

    private List<CrewMember> pets;
    private Set<Integer> selectedIds;
    private OnPetSelectedListener listener;
    private boolean selectable;
    public CrewMemberAdapter(List<CrewMember> pets, OnPetSelectedListener listener, boolean selectable) {
        this.pets = new ArrayList<>(pets);
        this.listener = listener;
        this.selectable = selectable;
        this.selectedIds = new HashSet<>();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new PetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        CrewMember cm = pets.get(position);
        holder.bind(cm);
    }

    @Override
    public int getItemCount() { return pets.size(); }

    public void updateData(List<CrewMember> newPets) {
        pets.clear();
        pets.addAll(newPets);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<CrewMember> getSelected() {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : pets) {
            if (selectedIds.contains(cm.getId())) result.add(cm);
        }
        return result;
    }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    class PetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivAvatar;
        TextView tvName;
        TextView tvSpec;
        TextView tvStats;
        ProgressBar pbEnergy;
        TextView tvEnergyLabel;
        CheckBox cbSelect;

        PetViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            ivAvatar = itemView.findViewById(R.id.ivCrewAvatar);
            tvName = itemView.findViewById(R.id.tvCrewName);
            tvSpec = itemView.findViewById(R.id.tvCrewSpec);
            tvStats = itemView.findViewById(R.id.tvCrewStats);
            pbEnergy = itemView.findViewById(R.id.pbCrewEnergy);
            tvEnergyLabel = itemView.findViewById(R.id.tvEnergyLabel);
            cbSelect = itemView.findViewById(R.id.cbCrewSelect);
        }

        void bind(CrewMember cm) {
            tvName.setText(cm.getName());
            tvSpec.setText(cm.getSpecialization());
            tvStats.setText("⚔️ Skill: " + cm.getSkill()
                    + "\n🛡️ Resilience: " + cm.getResilience()
                    + "\n❤️ Health: " + cm.getEnergy()
                    + "\n✨ XP: " + cm.getExperience());
            tvName.setTextColor(Color.BLACK);
            tvSpec.setTextColor(Color.BLACK);
            tvStats.setTextColor(Color.parseColor("#333333"));
            pbEnergy.setMax(cm.getMaxEnergy());
            pbEnergy.setProgress(cm.getEnergy());
            tvEnergyLabel.setText(cm.getEnergy() + "/" + cm.getMaxEnergy());

            Resources res = itemView.getContext().getResources();
            String pkg = itemView.getContext().getPackageName();
            int drawableId = res.getIdentifier(cm.getAvatarPath(), "drawable", pkg);
            if (drawableId != 0) {
                ivAvatar.setImageResource(drawableId);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_pet_default);
            }

            cardView.setCardBackgroundColor(specColour(cm.getSpecialization()));

            cbSelect.setVisibility(selectable ? View.VISIBLE : View.GONE);
            cbSelect.setChecked(selectedIds.contains(cm.getId()));
            cbSelect.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) selectedIds.add(cm.getId());
                else selectedIds.remove(cm.getId());
                if (listener != null) listener.onPetSelected(cm, checked);
            });

            itemView.setOnClickListener(v -> cbSelect.toggle());

            if (cm.isInjured()) {
                tvName.setText(cm.getName() + " 🏥 (recovering)");
            }
        }

        private int specColour(String spec) {
            switch (spec) {
                case "Pilot":
                    return Color.parseColor("#DDEEFF");
                case "Engineer":
                    return Color.parseColor("#FFFACD");
                case "Medic":
                    return Color.parseColor("#DDFFDD");
                case "Scientist":
                    return Color.parseColor("#F0DDFF");
                case "Soldier":
                    return Color.parseColor("#FFE0E0");
                default:
                    return Color.parseColor("#F5F5F5");
            }
        }
    }
}
