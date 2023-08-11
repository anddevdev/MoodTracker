package com.example.megakursach;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private Context context;
    private List<Goal> goals;
    private DatabaseHelper databaseHelper;

    public GoalsAdapter(Context context, List<Goal> goals, DatabaseHelper databaseHelper) {
        this.context = context;
        this.goals = goals;
        this.databaseHelper = databaseHelper;
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

        // Set up the click listener to show a confirmation dialog
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                showDeleteConfirmationDialog(clickedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this goal?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGoal(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteGoal(int position) {
        Goal goalToDelete = goals.get(position);

        // Call the method to delete the goal from the database
        databaseHelper.deleteGoal(goalToDelete.getId());

        // Remove the goal from the list and notify the adapter
        goals.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        // Refresh goals in TitlePage activity
        ((TitlePage) context).refreshGoals();
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
