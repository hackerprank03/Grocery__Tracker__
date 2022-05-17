package com.example.grocerytracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class DinnerFoodAdapter extends RecyclerView.Adapter<DinnerFoodAdapter.DinnerFoodViewHolder> {

    Context context;
    List<DinnerFood> dinnerFoodList;

    public DinnerFoodAdapter(Context context, List<DinnerFood> dinnerFoodList){
        this.context=context;
        this.dinnerFoodList=dinnerFoodList;
    }

    @NonNull
    @Override
    public DinnerFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.dinner_food_row_item,parent, false);
        return new DinnerFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(com.example.grocerytracker.DinnerFoodAdapter.DinnerFoodViewHolder holder, final int position) {
        Picasso.get().load(dinnerFoodList.get(position).getImageUrl()).into(holder.foodImage);
      //  holder.foodImage.setImageResource(popularFoodList.get(position).getImageUrl());
        holder.name.setText(dinnerFoodList.get(position).getName());
        holder.rating.setText(dinnerFoodList.get(position).getRating());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            //Passing id and pic information when clicked to DetailsRecipe class when clicked
            public void onClick(View v) {
                Intent i = new Intent(context, DetailsRecipe.class);
                i.putExtra("id_recipe", dinnerFoodList.get(position).getId());
                i.putExtra("food_pic", dinnerFoodList.get(position).getImageUrl());
                context.startActivity(i);
            }
        });

    }

    @Override
    //Get size of food list
    public int getItemCount() {
        return dinnerFoodList.size();
    }

    // Connecting item to the respective ImageView and TextView
    public static final class DinnerFoodViewHolder extends RecyclerView.ViewHolder{

        ImageView foodImage;
        TextView rating,name;
        public DinnerFoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage=itemView.findViewById(R.id.imageView2);
            name= itemView.findViewById(R.id.recipe_name);
            rating= itemView.findViewById(R.id.recipe_rating);
        }
    }
}
