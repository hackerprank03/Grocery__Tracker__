package com.example.grocerytracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Recipe extends AppCompatActivity {

    //Declaring Variables
    RecyclerView popularRecycler, breakfastRecycler, lunchRecycler, dinnerRecycler;
    popularFoodAdapter PopularFoodAdapter;
    BreakfastFoodAdapter breakfastFoodAdapter;
    LunchFoodAdapter lunchFoodAdapter;
    DinnerFoodAdapter dinnerFoodAdapter;
    //String breakfastMain = "https://api.spoonacular.com/recipes/complexSearch?query=meat&type=breakfast&number=10&apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String breakfastMain = "https://api.spoonacular.com/recipes/complexSearch?query=meat&type=breakfast&number=10&apiKey=b07a52c682e244109a54eb58858235d3";
    //String randomRecipe = "https://api.spoonacular.com/recipes/complexSearch?query=";
    String randomRecipe = "https://api.spoonacular.com/recipes/complexSearch?includeIngredients=";
    //String apiKey = "&apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String apiKey = "&apiKey=b07a52c682e244109a54eb58858235d3";
    String type, query, number;
    List<PopularFoods> popularFoodList;
    List<BreakfastFood> breakfastFoodList;
    List<LunchFood> lunchFoodList;
    List<DinnerFood> dinnerFoodList;
    Button button;
    ArrayList<String> ingredientsList;

    //Parsing popular food
    protected void parsePopular(JSONArray result) throws JSONException{
        popularFoodList = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            JSONObject recipe = result.getJSONObject(i);
            String tittle = recipe.getString("title");
            String imageLink = recipe.getString("image");
            String id = recipe.getString("id");
            popularFoodList.add(new PopularFoods(tittle, "4.57", imageLink, id));
        }
    }

    //Parsing breakfast food
    protected void parseBreakfast(JSONArray result) throws JSONException{
        breakfastFoodList = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            JSONObject recipe = result.getJSONObject(i);
            String tittle = recipe.getString("title");
            String imageLink = recipe.getString("image");
            String id = recipe.getString("id");
            breakfastFoodList.add(new BreakfastFood(tittle, "4.5", imageLink, id));
        }
    }

    //Parsing lunch food
    protected void parseLunch(JSONArray result) throws JSONException{
        lunchFoodList = new ArrayList<>();
        for (int i = 0; i <  result.length(); i++) {
            JSONObject recipe = result.getJSONObject(i);
            String tittle = recipe.getString("title");
            String imageLink = recipe.getString("image");
            String id = recipe.getString("id");
            lunchFoodList.add(new LunchFood(tittle, "4.5", imageLink,id));
        }

    }

    //Parsing dinner food
    protected  void parseDinner(JSONArray result) throws JSONException{
        dinnerFoodList = new ArrayList<>();
        for (int i = 0; i <result.length(); i++) {
            JSONObject recipe = result.getJSONObject(i);
            String tittle = recipe.getString("title");
            String imageLink = recipe.getString("image");
            String id = recipe.getString("id");
            dinnerFoodList.add(new DinnerFood(tittle, "4.5", imageLink,id));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //Container for food lists
        popularFoodList = new ArrayList<>();
        breakfastFoodList = new ArrayList<>();
        lunchFoodList = new ArrayList<>();
        dinnerFoodList = new ArrayList<>();
        fetchIngredients();

        button = (Button) findViewById(R.id.back_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();

            }
        });

        query="meat";
        type="popular";
        number="10";
        String fullApi = randomRecipe +query+"&type="+type+"&number="+number+ apiKey;
        //popular food
        try {
            requestPopular(fullApi);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Funcction redirect to Recipe
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Set card like view for popular food in horizontal view
    private void setPopularRecycler(List<PopularFoods> popularFoodList) {
        popularRecycler = findViewById(R.id.popular_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        popularRecycler.setLayoutManager(layoutManager);
        PopularFoodAdapter = new popularFoodAdapter(this, popularFoodList);
        popularRecycler.setAdapter(PopularFoodAdapter);

    }

    //Set card like view for breakfast food in horizontal view
    private void setBreakfastRecycler(List<BreakfastFood> breakfastFoodList) {
        breakfastRecycler = findViewById(R.id.breakfast_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        breakfastRecycler.setLayoutManager(layoutManager);
        breakfastFoodAdapter = new BreakfastFoodAdapter(this, breakfastFoodList);
        breakfastRecycler.setAdapter(breakfastFoodAdapter);

    }

    //Set card like view for lunch food in horizontal view
    private void setLunchRecycler(List<LunchFood> lunchFoodList) {
        lunchRecycler = findViewById(R.id.lunch_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        lunchRecycler.setLayoutManager(layoutManager);
        lunchFoodAdapter = new LunchFoodAdapter(this, lunchFoodList);
        lunchRecycler.setAdapter(lunchFoodAdapter);
        int size = lunchFoodList.size();
        int lol = size;


    }

    //Set card like view for dinner food in horizontal view
    private void setDinnerRecycler(List<DinnerFood> dinnerFoodList) {
        dinnerRecycler = findViewById(R.id.dinner_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        dinnerRecycler.setLayoutManager(layoutManager);
        dinnerFoodAdapter = new DinnerFoodAdapter(this, dinnerFoodList);
        dinnerRecycler.setAdapter(dinnerFoodAdapter);
    }

    //Internet Access Permission
    private final OkHttpClient client = new OkHttpClient();

    //Function to retrieve title and picture in Popular section from API
    public void requestPopular(final String fullApi) throws Exception {

        Request request = new Request.Builder()
                .url(fullApi)
                .build();

        client.newCall(request).enqueue(new Callback() {
            //Respone if fail requet
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            //Response ada tapi tak semsstinya content tu ada (error404)
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try (ResponseBody responseBody = response.body()) {
                    {
                        String responseString = responseBody.string();
                        //JSON parsing
                        try {
                            query = "";
                            JSONObject reader = new JSONObject(responseString);
                            JSONArray result = reader.getJSONArray("results");
                            parsePopular(result);
                            if(!ingredientsList.isEmpty()){
                                for(int i=0;i<ingredientsList.size();i++)
                                {
                                    query=query+ingredientsList.get(i).toString()+",+";
                                }
                            }
                            else{
                                query="cereal";
                            }

                            type="breakfast";
                            number="3";
                            String fullApi = randomRecipe +query+"&type="+type+"&number="+number+ apiKey;
                            try {
                                requestBreakfast(fullApi);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                }
            }
        });
    }

    //Function to retrieve title and picture in Breakfast section from API
    public void requestBreakfast(final String fullApi) throws Exception {

        Request request = new Request.Builder()
                .url(fullApi)
                .build();

        client.newCall(request).enqueue(new Callback() {
            //Respone if fail requet
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            //Response ada tapi tak semsstinya content tu ada (error404)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    {
                        String responseString = responseBody.string();
                        //JSON parsing
                        try {
                            query = "";
                            JSONObject reader = new JSONObject(responseString);
                            JSONArray result = reader.getJSONArray("results");
                            parseBreakfast(result);
                            if(!ingredientsList.isEmpty()){
                                for(int i=0;i<ingredientsList.size();i++)
                                {
                                    query=query+ingredientsList.get(i).toString()+",+";
                                }
                            }
                            else {
                                query = "meat";
                            }
                            type="lunch";
                            number="3";
                            String fullApi = randomRecipe +query+"&type="+type+"&number="+number+ apiKey;
                            try {
                                requestLunch(fullApi);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                }
            }
        });
    }

    //Function to retrieve title and picture in Lunch section from API
    public void requestLunch(final String fullApi) throws Exception {

        Request request = new Request.Builder()
                .url(fullApi)
                .build();

        client.newCall(request).enqueue(new Callback() {
            //Respone if fail requet
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            //Response ada tapi tak semsstinya content tu ada (error404)
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try (ResponseBody responseBody = response.body()) {
                    {
                        String responseString = responseBody.string();
                        //JSON parsing
                        try {
                            query = "";
                            JSONObject reader = new JSONObject(responseString);
                            JSONArray result = reader.getJSONArray("results");
                            Log.v("response", responseString);

                            parseLunch(result);
                            if(!ingredientsList.isEmpty()){
                                for(int i=0;i<ingredientsList.size();i++)
                                {
                                    query=query+ingredientsList.get(i).toString()+",+";
                                }
                            }
                            else {
                                query = "chicken";
                            }
                            type="dinner";
                            number="3";
                            String fullApi = randomRecipe +query+"&type="+type+"&number="+number+ apiKey;


                            try {
                                requestDinner(fullApi);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                }
            }
        });
    }

    //Function to retrieve title and picture in Dinner section from API
    public void requestDinner(String fullApi) throws Exception {

        Request request = new Request.Builder()
                .url(fullApi)
                .build();

        client.newCall(request).enqueue(new Callback() {
            //Respone if fail requet
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try (ResponseBody responseBody = response.body()) {
                    {
                        String responseString = responseBody.string();
                        //JSON parsing
                        try {
                            JSONObject reader = new JSONObject(responseString);
                            JSONArray result = reader.getJSONArray("results");
                            parseDinner(result);

                            //Entering UI Thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDinnerRecycler(dinnerFoodList);
                                    setBreakfastRecycler(breakfastFoodList);
                                    setLunchRecycler(lunchFoodList);
                                    setPopularRecycler(popularFoodList);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                }
            }
        });
    }

    //Fetching ingridients list from database
    public void fetchIngredients(){
        ingredientsList = new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grocery_list");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                final grocery gro_list = dataSnapshot.getValue(grocery.class);
                                String txt = gro_list.getName();
                                ingredientsList.add(txt);
                                //GroceryItems = new grocery(txt, ty, Integer.valueOf(quan), exp, img);
                            }
                        } catch (Exception e) {
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



}
