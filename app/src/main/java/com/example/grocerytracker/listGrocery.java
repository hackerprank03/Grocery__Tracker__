package com.example.grocerytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listGrocery extends AppCompatActivity {

    List<grocery> fetchdata;
    RecyclerView rv;
    GroceryAdapter ga;
    DatabaseReference dr;
    Button home, add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_grocery);
        rv = findViewById(R.id.recyclerview);
        home = findViewById(R.id.et_home);
        add = findViewById(R.id.floatingActionButton2);

        fetchdata = new ArrayList<>();

        //going to root in firebase where data is stored.
        dr = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list");

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    //reading data from firebase.
                    grocery gro_list=dataSnapshot.getValue(grocery.class);
                    fetchdata.add(gro_list);
                }
                setCards(fetchdata);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //button will bring user to home page when clicked
        home.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)
            {
                Intent nextpage = new Intent(listGrocery.this,MainActivity.class);
                startActivity(nextpage);
            }
        });

        //user will be redirected to page where they can add new grocery information
        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)
            {
                Intent nextpage = new Intent(listGrocery.this,addGrocery.class);
                startActivity(nextpage);
                finish();
            }
        });


    }

    public void setCards(List<grocery> fetchdata){ // setting fetched data in recyclerview
        rv = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        rv.setLayoutManager(layoutManager);
        //problem3
        ga = new GroceryAdapter(this,fetchdata);
        rv.setAdapter(ga);
    }
}