package com.example.grocerytracker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class expGroceryAdapter extends RecyclerView.Adapter<expGroceryAdapter.expGroceryViewHolder>{

    private Context context;
    private List<grocery> groceryList;

    public expGroceryAdapter(Context context,List<grocery>groceryList){
        this.context=context;
        this.groceryList=groceryList;
    }

    @NonNull
    @Override
    public expGroceryAdapter.expGroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.expired_items,parent, false);
        return new expGroceryAdapter.expGroceryViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull expGroceryAdapter.expGroceryViewHolder holder, final int position) {
        Picasso.get().load(groceryList.get(position).getImageUri()).into(holder.expImage);
        holder.expName.setText(groceryList.get(position).getName());
        //The following line of code will count the remaining days for the items to be expired
        SimpleDateFormat dateStructure = new SimpleDateFormat("dd/MM/yy");
        LocalDate date = LocalDate.now();
        String cDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
        String oDate = groceryList.get(position).getDate();
        Date dateObj1 = null;
        Date dateObj2 = null;
        try {
            dateObj1 = dateStructure.parse(cDate);
            dateObj2 = dateStructure.parse(oDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = dateObj2.getTime() - dateObj1.getTime();
        int diffDays = (int) (diff/(24 * 60 * 60 * 1000));
        holder.expDay.setText(String.valueOf(diffDays) + " day(s)");

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, updateGrocery.class);
                intent.putExtra("image_uri", groceryList.get(position).getImageUri());
                intent.putExtra("grocery_name", groceryList.get(position).getName());
                intent.putExtra("grocery_type", groceryList.get(position).getType());
                intent.putExtra("grocery_expiry", groceryList.get(position).getDate());
                intent.putExtra("grocery_quantity", groceryList.get(position).getQuantity());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }


    public static final class expGroceryViewHolder extends RecyclerView.ViewHolder{

        ImageView expImage;
        TextView expName,expDay;
        ConstraintLayout mainLayout;
        public expGroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.cardLayout);
            expImage=itemView.findViewById(R.id.exp_image);
            expName = itemView.findViewById(R.id.exp_item_name);
            expDay = itemView.findViewById(R.id.exp_item_day);
        }
    }
}
