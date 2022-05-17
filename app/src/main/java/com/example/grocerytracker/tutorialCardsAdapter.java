package com.example.grocerytracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
//Adapter to display the cards for the kitchen tips in the main ui
public class tutorialCardsAdapter extends RecyclerView.Adapter<tutorialCardsAdapter.tutorialCardViewHolder> {
    private Context context;
    private List<Tutorials> tutorialsList;

    public tutorialCardsAdapter(Context context,List<Tutorials>tutorialsList){
        this.context=context;
        this.tutorialsList=tutorialsList;
    }


    @NonNull
    @Override

    public tutorialCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.tutorial_cards_item,parent, false);
        return new tutorialCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tutorialCardsAdapter.tutorialCardViewHolder holder, final int position) {

        Picasso.get().load(tutorialsList.get(position).getThumbnailURL()).into(holder.foodImage);


        holder.title.setText(tutorialsList.get(position).getTitle());
        //holder.desc.setText(tutorialsList.get(position).getShortDesc());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the information to next Intent if there's any of the card is pressed
                ArrayList<String> Stps = tutorialsList.get(position).getSteps();
                int stepsnum = Stps.size();
                Log.d("Number of steps: ", String.valueOf(stepsnum));
                Intent intent = new Intent(context, TutorialSteps.class);
                intent.putExtra("image_url", tutorialsList.get(position).getSteps());
                intent.putExtra("youtube_link", tutorialsList.get(position).getVideoID());
                intent.putExtra("title", tutorialsList.get(position).getTitle());
                intent.putExtra("short_desc", tutorialsList.get(position).getShortDesc());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tutorialsList.size();
    }

    //problem2
    public static final class tutorialCardViewHolder extends RecyclerView.ViewHolder{

        ImageView foodImage;
        TextView desc,title;
        ConstraintLayout mainLayout;
        public tutorialCardViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage=itemView.findViewById(R.id.stepsImg);
            title = itemView.findViewById(R.id.stepsTitle);
            //desc= itemView.findViewById(R.id.stepsDesc);
            mainLayout= itemView.findViewById(R.id.main_layout);
        }
    }

}
