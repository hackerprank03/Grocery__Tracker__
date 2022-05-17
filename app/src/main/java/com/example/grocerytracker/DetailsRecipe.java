package com.example.grocerytracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class DetailsRecipe extends AppCompatActivity {
    //Declaring Variables
    private Button button;
    String recipeIngredients = "https://api.spoonacular.com/recipes/637876/ingredientWidget.json?apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String recipeIngredients1 = "https://api.spoonacular.com/recipes/";
    //String recipeIngredients2 = "/ingredientWidget.json?apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String recipeIngredients2 = "/ingredientWidget.json?apiKey=b07a52c682e244109a54eb58858235d3";
    String recipeSummary = "https://api.spoonacular.com/recipes/637876/summary?apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String recipeSummary1 = "https://api.spoonacular.com/recipes/";
    //String recipeSummary2 = "/summary?apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String recipeSummary2 = "/summary?apiKey=b07a52c682e244109a54eb58858235d3";
    String recipeInstructions = "https://api.spoonacular.com/recipes/637876/analyzedInstructions?apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String recipeInstruction1 = "https://api.spoonacular.com/recipes/";
    //String recipeInstruction2 = "/analyzedInstructions?apiKey=fde780bf140e4dbbaefd90ab69e3d459";
    String recipeInstruction2 = "/analyzedInstructions?apiKey=b07a52c682e244109a54eb58858235d3";
    OkHttpClient client = new OkHttpClient();
    String unit, nama, namaBahan, id, summary,namaBarang,cara,pic;
    int number;
    Double value;
    TextView title,summaryContent,ingridientContent,instructionContent;
    ImageView food_pic;
    String caraIncrement, bahanIncrement;




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_recipe);

        title = (TextView) findViewById(R.id.nama_recipe);
        food_pic = (ImageView) findViewById(R.id.main_image);

        summaryContent = (TextView) findViewById(R.id.summary_recipe);
        ingridientContent = (TextView) findViewById(R.id.ingridient_recipe);
        instructionContent = (TextView) findViewById(R.id.instrction_recipe);

        //Back button to redirect to Recipe
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();

            }
        });

        //Recieving recipe id & pic url from adapter
        Intent intent = getIntent();
        id = intent.getStringExtra("id_recipe");
        pic = intent.getStringExtra("food_pic");

        //concatenation
        String fullApiRecipeIngredients = recipeIngredients1 + id + recipeIngredients2;
        try {
            Log.v("fullApi","Successful");
            run(fullApiRecipeIngredients);
            Log.v("fullApi","run full api successful");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Funcction redirect to Recipe
    public void openMainActivity() {
        Intent intent = new Intent(this, Recipe.class);
        startActivity(intent);
    }

    //Retrieving information from API from first URL
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String responseString = responseBody.string();

                    //JSON parsing
                    try {
                        JSONObject balas = new JSONObject(responseString);
                        JSONArray ingredients = balas.getJSONArray("ingredients");

                        //Retrieving JSON Object from API
                        for (int i = 0; i < ingredients.length(); i++) {
                            JSONObject singleIngredient = ingredients.getJSONObject(i);
                            namaBahan = singleIngredient.getString("name");
                            JSONObject amount = singleIngredient.getJSONObject("amount");
                            JSONObject calcMalaysia = amount.getJSONObject("metric");
                            value = calcMalaysia.getDouble("value");
                            unit = calcMalaysia.getString("unit");
                            bahanIncrement = bahanIncrement+namaBahan + "\n";


                        }

                        String fullApiRecipeSummary = recipeSummary1 + id + recipeSummary2;
                        run1(fullApiRecipeSummary);

                    } catch (JSONException e) {
                        e.printStackTrace();
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

    //Retrieving information from API from second URL
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void run1(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String responseString = responseBody.string();
                    //JSON parsing
                    try {
                        //Retrieving JSON Object from API
                        JSONObject balas = new JSONObject(responseString);
                        nama = balas.getString("title");
                        summary = balas.getString("summary");
                        String fullApiRecipeInstruction = recipeInstruction1 + id + recipeInstruction2;
                        run2(fullApiRecipeInstruction);

                    } catch (JSONException e) {
                        e.printStackTrace();
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

    //Retrieving information from API from third URL
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void run2(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Cari API lain siot";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String responseString = responseBody.string();
                    //JSON parsing
                    try {
                        //Retrieving JSON Object from API
                        JSONArray balas = new JSONArray(responseString);
                        JSONObject balasObject = balas.getJSONObject(0);
                        JSONArray steps = balasObject.getJSONArray("steps");

                        for (int i = 0; i < steps.length(); i++) {
                            JSONObject singleStep = steps.getJSONObject(i);
                            number = singleStep.getInt("number");
                            cara = singleStep.getString("step");
                            JSONArray barang = singleStep.getJSONArray("equipment");

                            //JSONObject barangObject = barang.getJSONObject(0);
                            //namaBarang = barangObject.getString("name");
                            // Log.v("Recipe Instrcution",namaBarang);

                            //Making new line for each information recieved from API
                            caraIncrement = caraIncrement+cara+ "\n";
                        }

                        //Entering UI Thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                title.setText(nama);
                                Picasso.get().load(pic).into(food_pic);
                                //summaryContent.setText("summary test");
                                //summaryContent.setText(summary);
                                ingridientContent.setText(bahanIncrement);
                                instructionContent.setText(caraIncrement);

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
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
}




