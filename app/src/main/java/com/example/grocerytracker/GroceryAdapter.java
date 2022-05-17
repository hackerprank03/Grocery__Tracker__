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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {

    List<grocery> grolist;
    Context context;
    public GroceryAdapter(Context context, List<grocery> grolist) {
        this.grolist = grolist;
        this.context = context;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.GroceryViewHolder holder, final int position) {

        holder.name.setText(grolist.get(position).getName());
        holder.type.setText(grolist.get(position).getType());
        holder.quantity.setText(grolist.get(position).getQuantity().toString());
        holder.date.setText(grolist.get(position).getDate());
        Picasso.get().load(grolist.get(position).getImageUri()).into(holder.groImage);

        // this two image view will be usd to delte and edit purpose of grocery list in the listGrocery activity
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, updateGrocery.class);
                intent.putExtra("grocery_name",grolist.get(position).getName());
                intent.putExtra("grocery_type",grolist.get(position).getType());
                intent.putExtra("grocery_quantity",grolist.get(position).getQuantity().toString());
                intent.putExtra("grocery_expiry",grolist.get(position).getDate());
                intent.putExtra("image_uri",grolist.get(position).getImageUri());
                context.startActivity(intent);
            }
        });
        // this two image view will be usd to delte and edit purpose of grocery list in the listGrocery activity
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference data = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list").child(grolist.get(position).getName());
                data.removeValue();

                StorageReference deleteimage = FirebaseStorage.getInstance().getReferenceFromUrl(grolist.get(position).getImageUri());
                deleteimage.delete();
                Intent intent = new Intent(context, listGrocery.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return grolist.size();
    }

    public static final class GroceryViewHolder extends RecyclerView.ViewHolder{

        ImageView groImage,edit,delete;
        TextView name,type,quantity,date;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);

            groImage = itemView.findViewById(R.id.imageIv);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            quantity= itemView.findViewById(R.id.quantity);
            date = itemView.findViewById(R.id.date);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
