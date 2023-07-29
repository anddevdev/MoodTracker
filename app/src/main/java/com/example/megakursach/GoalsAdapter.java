package com.example.megakursach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private Context context;
    private List<Goal> goals;

    public GoalsAdapter(Context context, List<Goal> goals) {
        this.context = context;
        this.goals = goals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.goalTitleTextView.setText(goal.getTitle());
        holder.goalDescriptionTextView.setText(goal.getDescription() + " - Target: " + goal.getTargetValue() + " by " + goal.getTargetDate());
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalTitleTextView;
        TextView goalDescriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            goalTitleTextView = itemView.findViewById(R.id.goalTitleTextView);
            goalDescriptionTextView = itemView.findViewById(R.id.goalDescriptionTextView);
        }
    }
}
