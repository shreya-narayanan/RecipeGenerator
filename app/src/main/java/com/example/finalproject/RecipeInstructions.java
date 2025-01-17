package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RecipeInstructions extends AppCompatActivity {
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_instructions);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setBackgroundColor(Color.CYAN);
        Intent intent = getIntent();
        String stepsId = intent.getStringExtra(RecipeInformation.ID_TEXT);
        String picUrl = intent.getStringExtra(RecipeInformation.URL_TEXT);
        new ConvertUrlToImage((ImageView) findViewById(R.id.imageView)).execute(picUrl);
        RecipeSteps recipeSteps = new RecipeSteps();
        recipeSteps.execute(stepsId);

    }
    public class ConvertUrlToImage extends AsyncTask<String, Void, Bitmap> {
        ImageView recipeImage;

        public ConvertUrlToImage(ImageView bmImage) {
            this.recipeImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            recipeImage.setImageBitmap(result);
        }
    }
    public class RecipeSteps extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String bR = "";
            try {
                String recipeUrl = "https://api.spoonacular.com/recipes/"+strings[0]+"/analyzedInstructions?stepBreakdown=true&apiKey=3fd9db153aeb4356aa39afb1e7245c3f";
                URL url1 = new URL(recipeUrl);
                URLConnection urlConnection = url1.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                bR = bufferedReader.readLine();
                Log.d("TAG", bR);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bR;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONArray steps = jsonArray.getJSONObject(0).getJSONArray("steps");
                for(int i = 0; i < steps.length();i++)
                {
                    int num = steps.getJSONObject(i).getInt("number");
                    String step = steps.getJSONObject(i).getString("step");
                    Log.d("STEP",step);
                    View linearLayout =  findViewById(R.id.linearLayout);
                    TextView stepsText = new TextView(getApplicationContext());
                    stepsText.setTextColor(Color.BLACK);
                    stepsText.setText(num+". "+step);
                    stepsText.setId(View.generateViewId());
                    stepsText.setGravity(Gravity.CENTER);
                    stepsText.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout) linearLayout).addView(stepsText);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
