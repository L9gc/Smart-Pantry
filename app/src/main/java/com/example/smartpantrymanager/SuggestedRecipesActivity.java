package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeListener {

    private RecyclerView recyclerRecipes;
    private TextView txtEmptyRecipes;

    private RecipeAdapter recipeAdapter;
    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        BottomNavigationHelper.setup(this);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        txtEmptyRecipes = findViewById(R.id.txtEmptyRecipes);

        databaseHelper = new PantryDatabaseHelper(this);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeAdapter = new RecipeAdapter(
                new ArrayList<>(),
                this
        );

        recyclerRecipes.setAdapter(recipeAdapter);

        loadSuggestedRecipes();
    }


    private void loadSuggestedRecipes() {

        android.util.Log.e(
                "RECIPE_DEBUG",
                "loadSuggestedRecipes() WAS CALLED"
        );

        List<Recipe> suggestedRecipes =
                databaseHelper.getSuggestedRecipes();

        android.util.Log.e(
                "RECIPE_DEBUG",
                "Number of suggestions returned: "
                        + suggestedRecipes.size()
        );

        recipeAdapter.updateRecipes(suggestedRecipes);

        if (suggestedRecipes.isEmpty()) {
            txtEmptyRecipes.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);
        } else {
            txtEmptyRecipes.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        Intent intent = new Intent(
                SuggestedRecipesActivity.this,
                RecipeDetailActivity.class
        );

        intent.putExtra("RECIPE_ID", recipe.getId());

        startActivity(intent);
    }

    // Return to the home screen.
    public void openHome(View view) {
        finish();
    }

    // Open the pantry screen.
    public void openPantry(View view) {
        startActivity(
                new Intent(this, PantryActivity.class)
        );
    }

    // Open settings.
    public void openSettings(View view) {
        startActivity(
                new Intent(this, SettingsActivity.class)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recipeAdapter != null) {
            loadSuggestedRecipes();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}