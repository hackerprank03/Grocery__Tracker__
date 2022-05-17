package com.example.grocerytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Calendar;

public class updateGrocery extends AppCompatActivity {

    EditText et_grocery_name;
    EditText et_type;
    EditText et_quantity;
    ImageView imageView;
    TextView et_expdate;

    Button cancel,update;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    StorageReference nStorageRef;
    public Uri imguri;
    private StorageTask uploadTask;

    String TAG = "MainActivity";
    DatePickerDialog.OnDateSetListener date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grocery);

        et_expdate = (TextView) findViewById(R.id.et_expdate);
        et_grocery_name = findViewById(R.id.et_grocery_name);
        et_type = findViewById(R.id.et_type);
        et_quantity=findViewById(R.id.et_quantity);
        imageView = findViewById(R.id.image_view);

        update = findViewById(R.id.et_update);
        cancel = findViewById(R.id.et_cancel);

        nStorageRef= FirebaseStorage.getInstance().getReference("Images"); //going to the root in firebase storage where the image is stored

        final String image = getIntent().getStringExtra("image_uri");
        final String name = getIntent().getStringExtra("grocery_name");
        final String type = getIntent().getStringExtra("grocery_type");
        final String expiry = getIntent().getStringExtra("grocery_expiry");
        final String quantity = getIntent().getStringExtra("grocery_quantity");

        Glide.with(this).load(image).into(imageView); //displaying image in imageview using its url
        et_grocery_name.setText(name);
        et_type.setText(type);
        et_quantity.setText(quantity);
        et_expdate.setText(expiry);

        //this activity only will work if user want to edit other info except for image
        //if they click imageview they will be redirected to another page where they can edit image as well
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //sending info into updateImageGrocery activity.
                Intent nextpage = new Intent(updateGrocery.this,updateImageGrocery.class);
                nextpage.putExtra("grocery_name", name);
                nextpage.putExtra("grocery_type", type);
                nextpage.putExtra("grocery_expiry", expiry);
                nextpage.putExtra("grocery_quantity", String.valueOf(quantity));
                nextpage.putExtra("image_uri", image);
                startActivity(nextpage);
            }
        });

        //when user clicks update the old infos will be deleted and new infos will be stored in firebase.
        update.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)
            {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list");

                String edited_name = et_grocery_name.getLayout().getText().toString();
                String edited_type = et_type.getLayout().getText().toString();
                Integer edited_quantity = Integer.valueOf(et_quantity.getLayout().getText().toString());
                String edited_date = et_expdate.getText().toString();
                String edited_image = image;

                DatabaseReference data = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list").child(name);
                data.removeValue();

                grocery helperClass = new grocery(edited_name,edited_type,edited_quantity,edited_date,edited_image);
                reference.child(edited_name).setValue(helperClass);

                Intent nextpage = new Intent(updateGrocery.this,listGrocery.class);
                startActivity(nextpage);
            }
        });

        //if user cancel update they will be redirected to page where they can view all the groceries
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)
            {
                Intent nextpage = new Intent(updateGrocery.this,listGrocery.class);
                startActivity(nextpage);
            }
        });

        //when date text view is clicked user can select new date
        et_expdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(updateGrocery.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        date, year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //setting date with date,month and year.
        date=new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                month += 1;
                Log.d(TAG,"onDateSet: dd/MM/yy "+ month + "/" + day + "/" +year);
                String date = day + "/" + month + "/" + year;
                et_expdate.setText(date);
            }
        };
    }
}