package com.example.grocerytracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class addGrocery extends AppCompatActivity {
    EditText grocery_name;
    EditText type;
    EditText quantity;
    Button store,cancel;
    ImageView imageView;
    TextView expdate;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    StorageReference nStorageRef;
    public Uri imguri;
    public Bitmap imguri2;
    private StorageTask uploadTask;

    String TAG = "MainActivity" ;

    Integer uptake = 0;

    DatePickerDialog.OnDateSetListener date;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery);
        createNotificationChannel();

        expdate = (TextView) findViewById(R.id.et_expdate);
        grocery_name = findViewById(R.id.et_grocery_name);
        type = findViewById(R.id.et_type);
        quantity=findViewById(R.id.et_quantity);
        store = findViewById(R.id.et_store);
        imageView = findViewById(R.id.image_view);
        cancel = findViewById(R.id.et_cancel);

        nStorageRef= FirebaseStorage.getInstance().getReference("Images");

        Calendar cal = Calendar.getInstance();

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //prompt to ask user whether to take picture from camera or phone gallery
                AlertDialog.Builder askUser = new AlertDialog.Builder(addGrocery.this);
                askUser.setCancelable(false).setPositiveButton("Upload Picture", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Filechooser();
                    } // upload option
                }).setNegativeButton("Take Picture", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takeimage();
                    }
                }); //take camera option
                askUser.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)
            {
                Intent nextpage = new Intent(addGrocery.this,listGrocery.class); // cancel button will bring user to view available grocery
                startActivity(nextpage);
            }
        });


        expdate.setOnClickListener(new View.OnClickListener(){ // dialog box for the date chooser
            @Override
            public void onClick(View view){
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(addGrocery.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        date, year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        date=new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                month += 1;
                Log.d(TAG,"onDateSet: dd/MM/yy: "+ day + "/" + month + "/" +year);
                String date = day + "/" + month + "/" + year; // setting date with month,date and year
                expdate.setText(date);
            }
        };

        store.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)
            {
                Toast.makeText(addGrocery.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(addGrocery.this,ReminderBrodcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(addGrocery.this, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list"); // going to firebase root where we want to stored groceries

                //getting input of user from edit text.
                String name = grocery_name.getLayout().getText().toString();
                String types = type.getLayout().getText().toString();
                Integer quantities = Integer.valueOf(quantity.getLayout().getText().toString());
                String exp_date = expdate.getText().toString();

                //if user chooses image uploading from gallery
                if (uptake == 1)
                {
                    StorageReference Ref=nStorageRef.child(System.currentTimeMillis()+"," + getExtension(imguri));
                    uploadTask=Ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image = String.valueOf(uri);
                                    grocery helperClass = new grocery(name,types,quantities,exp_date,image);
                                    reference.child(name).setValue(helperClass);
                                    Intent nextpage = new Intent(addGrocery.this,listGrocery.class);
                                    startActivity(nextpage);
                                }
                            });
                        }
                    });
                }
                else if (uptake == 2) // if user choose to take image directly from camera
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imguri2.compress(Bitmap.CompressFormat.JPEG,100,baos);

                    final String URL = UUID.randomUUID().toString();
                    StorageReference imageRef = nStorageRef.child(URL);

                    byte[] b =baos.toByteArray();
                    imageRef.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image = String.valueOf(uri);
                                    grocery helperClass = new grocery(name,types,quantities,exp_date,image);
                                    reference.child(name).setValue(helperClass);
                                    Intent nextpage = new Intent(addGrocery.this,listGrocery.class);
                                    startActivity(nextpage);
                                }
                            });
                        }
                    });

                }

                //finish();


            }
        });
    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notification expiry ";
            String description = "Expiry reminder channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String getExtension(Uri uri){ // function to get extension from image
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((cr.getType(uri)));
    }


    private void Filechooser(){ //functio to choose image from phone gallery
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }

    private void takeimage(){ //function to take image using camera
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,200);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode==RESULT_OK && data!=null && data.getData()!=null) //if user chooses image from phone image will be set to image view.
        {
            imguri=data.getData();
            imageView.setImageURI(imguri);
            uptake = 1;
        }
        else if(requestCode == 200 && resultCode==RESULT_OK)//if user chooses image from camera that image will be set to image view.
        {
            imguri2=(Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(imguri2);
            uptake = 2;
        }
    }
}