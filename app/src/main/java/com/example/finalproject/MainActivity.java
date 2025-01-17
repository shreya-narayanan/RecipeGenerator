package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    ListView recipesLV;
    String[] recipesList;
    Button getRecipesBtn;
    EditText ingredientsTyped;
    public static final String EXTRA_TEXT = "com.example.application.example.EXTRA_TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linearLayout);
        recipesLV = findViewById(R.id.listView);
        recipesList = new String[10];
        getRecipesBtn = findViewById(R.id.button);
        ingredientsTyped = findViewById(R.id.editText);
        linearLayout.setBackgroundColor(Color.CYAN);
        getRecipesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredients = ingredientsTyped.getText().toString();
                if(ingredients.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please enter your ingredients", LENGTH_SHORT).show();
                }
                RecipeInfo recipeInfo = new RecipeInfo();
                recipeInfo.execute(ingredients);
            }
        });
        if(savedInstanceState!=null)
        {
            ingredientsTyped.setText(savedInstanceState.getString("EditText"));
        }

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("EditText",ingredientsTyped.getText().toString());
    }
    public class RecipeInfo extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String bR = "";
            try {
                String recipeUrl = "https://api.spoonacular.com/recipes/findByIngredients?ingredients="+strings[0]+"&apiKey=3fd9db153aeb4356aa39afb1e7245c3f";
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
                final JSONArray recipesInfo = new JSONArray(s);
                recipesList = new String[10];
                for(int i = 0; i < 10; i++)
                {
                    String recipeName = recipesInfo.getJSONObject(i).getString("title");
                    recipesList[i] = recipeName;
                }
                recipesLV = findViewById(R.id.listView);
                ArrayAdapter<String> recipeAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,recipesList);
                recipesLV.setAdapter(recipeAdapter);
                recipesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            int recipeId = recipesInfo.getJSONObject(position).getInt("id");
                            String strId = Integer.toString(recipeId);
                            Intent intent = new Intent(getApplicationContext(),RecipeInformation.class);
                            intent.putExtra(EXTRA_TEXT,strId);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

}