package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RecipeInformation extends AppCompatActivity {
    RadioGroup radioGroup;
    LinearLayout linearLayout;
    public static final String ID_TEXT = "com.example.application.example.NAME_TEXT";
    public static final String URL_TEXT = "com.example.application.example.URL_TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setBackgroundColor(Color.CYAN);
        Intent intentReceived = getIntent();
        final String recipeId = intentReceived.getStringExtra(MainActivity.EXTRA_TEXT);
        IngredientInfo ingredientInfo = new IngredientInfo();
        ingredientInfo.execute(recipeId);
    }
    public class IngredientInfo extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String bR = "";
            try {
                String recipeUrl = "https://api.spoonacular.com/recipes/"+strings[0]+"/information?includeNutrition=false&apiKey=3fd9db153aeb4356aa39afb1e7245c3f";
                URL url1 = new URL(recipeUrl);
                URLConnection urlConnection = url1.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                bR = bufferedReader.readLine();
                //Log.d("TAG", bR);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bR;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                final JSONObject object = new JSONObject(s);
                JSONArray ingredients = object.getJSONArray("extendedIngredients");
                for(int i = 0; i < ingredients.length();i++)
                {
                    String strIngredient = ingredients.getJSONObject(i).getString("originalString");
                    View linearLayout =  findViewById(R.id.linearLayout);
                    TextView ingredientsText = new TextView(getApplicationContext());
                    ingredientsText.setTextColor(Color.BLACK);
                    ingredientsText.setTextSize(18);
                    ingredientsText.setText(strIngredient);
                    ingredientsText.setId(View.generateViewId());
                    ingredientsText.setGravity(Gravity.CENTER);
                    ingredientsText.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout) linearLayout).addView(ingredientsText);
                }
                final String picUrl = object.getString("image");
                final String idToSend = Integer.toString(object.getInt("id"));
                radioGroup = findViewById(R.id.radioGroup);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged (RadioGroup group,int checkedId){
                        if(checkedId==R.id.radioButton)
                        {
                            Intent intent = new Intent(getApplicationContext(),RecipeInstructions.class);
                            intent.putExtra(ID_TEXT,idToSend);
                            intent.putExtra(URL_TEXT,picUrl);
                            startActivity(intent);
                        }
                        if(checkedId==R.id.radioButton2)
                        {
                            Toast.makeText(getApplicationContext(),"Click on the back arrow",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
