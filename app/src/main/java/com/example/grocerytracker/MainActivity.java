package com.example.grocerytracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.IpSecManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView cardsRecycler;
    private RecyclerView expiryRecycler;
    private tutorialCardsAdapter TutorialCardAdapter;
    private expGroceryAdapter ExpGroceryAdapter;
    private List<Tutorials> tutorialsList;
    private List<grocery> expiredItems;
    private grocery GroceryItems;
    private ArrayList<String> Steps;
    //The endpoint parameters for the kitchen tips website, which will be used to scrap later
    private String stepsURL [] ={"how-to-beat-egg-whites", "how-to-cook-basmati-rice",
            "how-to-cook-pasta", "how-to-cook-salmon", "how-to-fry-a-steak",
            "how-to-peel-and-deveine-a-prawn", "how-to-poach-an-egg",
            "how-to-prepare-an-avocado", "how-to-remove-tomato-skin",
            "how-to-zest-a-lemon"};
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private DatabaseReference ref;
    private FirebaseAuth fAuth;
    private ImageView addBtn, storeBtn, viewBtn, recipeBtn;
    private TextView userName, ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navi_view);
        headerView = navigationView.getHeaderView(0);
        addBtn = findViewById(R.id.addBtn);
        viewBtn = findViewById(R.id.viewBtn);
        recipeBtn = findViewById(R.id.recipeBtn);
        storeBtn = findViewById(R.id.storeBtn);
        userName = headerView.findViewById(R.id.userName);
        ownerName =findViewById(R.id.owner);
        fAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference(fAuth.getCurrentUser().getUid());
        //Connecting to the database to find the name of the user
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Log.d("username", snapshot.child("userName").getValue().toString());
                    String name = snapshot.child("userName").getValue().toString();
                    userName.setText(name);
                    ownerName.setText(name + "'s");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Initializing the navigation drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        loadExpItems();
        loadCards();



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), addGrocery.class));
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), listGrocery.class));
            }
        });

        recipeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Recipe.class));
            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://www.google.com/maps/search/nearest+grocery+store/@5.3968895,100.3159552,15z/data=!3m1!4b1"));
                startActivity(viewIntent);
            }
        });
    }
    //This is the function to load the tutorial cards
    public void loadCards(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tutorialsList = new ArrayList<>();
                    //This part will scarp the website
                    for(int i = 0; i < 10; i++) {
                        //This part will scrap the whole page
                        String url = "https://spoonacular.com/academy/" + stepsURL[i];
                        Log.d("URLs", url);
                        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                        //String title = doc.select("div.academyLesson").select("h1").text();
                        Elements data = doc.select("div.academyLesson");
                        String articleTitle = data.select("h1").text();
                        Log.d("Title", articleTitle);
                        String shortDesc = data.select("div.row g").select("ol.steps").select("li").text();
                        Elements steps = data.select("div.column.postBody").select("ol.steps");
                        String imageUrl = data.select("div.column.postBody").select("iframe").attr("src").toString();

                        int numSteps = steps.size();
                        int totalSteps = (steps.select("li")).size();
                        Log.d("Number of items", String.valueOf(numSteps));
                        Log.d("total steps", String.valueOf(totalSteps));
                        Steps = new ArrayList<>();
                        //This part will scrap the instructions
                        for (int j = 0; j < totalSteps; j++) {
                            Steps.add(steps.select("li").eq(j).text());
                            Log.d("Steps", steps.select("li").eq(j).text());
                        }
                        tutorialsList.add(new Tutorials(articleTitle, shortDesc, imageUrl, Steps));

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCardsRecycler(tutorialsList);
                        }
                    });
                    //adapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("items", "hello");
                }
            }
        }){}.start();
    }
    //Load the items that are going to expire in 3 days
    public void loadExpItems(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat dateStructure = new SimpleDateFormat("dd/MM/yy");

                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        try {
                            expiredItems = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                final grocery gro_list = dataSnapshot.getValue(grocery.class);
                                //To calculate the current date
                                LocalDate date = LocalDate.now();
                                String cDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                                String oDate = gro_list.getDate();
                                Date dateObj1 = dateStructure.parse(cDate);
                                Date dateObj2 = dateStructure.parse(oDate);
                                long diff = dateObj2.getTime() - dateObj1.getTime();
                                int diffDays = (int) (diff/(24 * 60 * 60 * 1000));
                                //To find the items which will expire in three days
                                if (diffDays<= 3) {
                                    String txt = gro_list.getName();
                                    String ty = gro_list.getType();
                                    String quan = String.valueOf(gro_list.getQuantity());
                                    String exp = gro_list.getDate();
                                    String img = gro_list.getImageUri();
                                    //GroceryItems = new grocery(txt, ty, Integer.valueOf(quan), exp, img);
                                    expiredItems.add(gro_list);
                                } else {
                                    continue;
                                }

                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!expiredItems.isEmpty()) {
                                            setExpiryRecycler(expiredItems);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (NumberFormatException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }){}.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Initializing the cards for tutorial by connecting to the adapter
    public void setCardsRecycler(List<Tutorials> tutorialsList){
        cardsRecycler = findViewById(R.id.cards_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        cardsRecycler.setLayoutManager(layoutManager);
        TutorialCardAdapter = new tutorialCardsAdapter(this,tutorialsList);
        cardsRecycler.setAdapter(TutorialCardAdapter);
    }
    //Initializing the cards for expiring items by connecting to the adapter
    public void setExpiryRecycler(List<grocery> expiredItems){
        expiryRecycler = findViewById(R.id.expiry_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        expiryRecycler.setLayoutManager(layoutManager);
        ExpGroceryAdapter = new expGroceryAdapter(this,expiredItems);
        expiryRecycler.setAdapter(ExpGroceryAdapter);
    }

    //The buttons function for navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.adding_items:
                startActivity(new Intent(getApplicationContext(), addGrocery.class));
                break;
            case R.id.viewing_items:
            case R.id.deleting_items:
                startActivity(new Intent(getApplicationContext(), listGrocery.class));
                break;
            case R.id.breakfast_view:
            case R.id.lunch_view:
            case R.id.dinner_view:
                startActivity(new Intent(getApplicationContext(), Recipe.class));
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                break;

        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
