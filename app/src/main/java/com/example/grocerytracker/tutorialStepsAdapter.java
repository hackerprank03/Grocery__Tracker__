package com.example.grocerytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class tutorialStepsAdapter extends RecyclerView.Adapter<tutorialStepsAdapter.tutorialStepsViewHolder> {

    private Context context;
    private ArrayList<String> steps;


    public tutorialStepsAdapter(Context context, ArrayList<String> steps){
        this.context=context;
        this.steps=steps;
    }

    @NonNull
    @Override

    public tutorialStepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tutorial_steps_item,parent, false);
        return new tutorialStepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tutorialStepsAdapter.tutorialStepsViewHolder holder, int position) {
        holder.steps.setText(steps.get(position));
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    //problem2
    public static final class tutorialStepsViewHolder extends RecyclerView.ViewHolder{

        TextView steps;
        public tutorialStepsViewHolder(@NonNull View itemView) {
            super(itemView);
            steps = itemView.findViewById(R.id.tuto_text);
        }
    }
}
