package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtRecipeName;
    private TextView txtRecipeTime;
    private TextView txtRecipeDescription;
    private TextView txtRecipeInstructions;

    private LinearLayout layoutRecipeIngredients;

    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Connect Java variables to the XML views.
        txtRecipeName = findViewById(
                R.id.txtDetailRecipeName
        );

        txtRecipeTime = findViewById(
                R.id.txtDetailRecipeTime
        );

        txtRecipeDescription = findViewById(
                R.id.txtDetailRecipeDescription
        );

        txtRecipeInstructions = findViewById(
                R.id.txtDetailRecipeInstructions
        );

        layoutRecipeIngredients = findViewById(
                R.id.layoutRecipeIngredients
        );

        databaseHelper = new PantryDatabaseHelper(this);

        // Connect the back button.
        TextView btnBackToRecipes = findViewById(
                R.id.btnBackToRecipes
        );

        btnBackToRecipes.setOnClickListener(v -> finish());

        // Retrieve the selected recipe's ID.
        int recipeId = getIntent().getIntExtra(
                "RECIPE_ID",
                -1
        );

        if (recipeId == -1) {
            Toast.makeText(
                    this,
                    "Recipe could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Load the selected recipe.
        loadRecipeDetails(recipeId);
    }

    private void loadRecipeDetails(int recipeId) {

        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(
                    this,
                    "Recipe could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Display the recipe information.
        txtRecipeName.setText(recipe.getName());

        txtRecipeTime.setText(
                recipe.getPreparationTime() + " minutes"
        );

        txtRecipeDescription.setText(
                recipe.getDescription()
        );

        txtRecipeInstructions.setText(
                recipe.getInstructions()
        );

        // Load the ingredients required for this recipe.
        displayRecipeIngredients(recipeId);
    }

    private void displayRecipeIngredients(int recipeId) {

        layoutRecipeIngredients.removeAllViews();

        try (Cursor cursor =
                     databaseHelper.getRecipeIngredients(recipeId)) {

            int nameColumn = cursor.getColumnIndexOrThrow(
                    "ingredient_name"
            );

            int quantityColumn = cursor.getColumnIndexOrThrow(
                    "required_quantity"
            );

            int unitColumn = cursor.getColumnIndexOrThrow(
                    "required_unit"
            );

            while (cursor.moveToNext()) {

                String ingredientName = cursor.getString(
                        nameColumn
                );

                double quantity = cursor.getDouble(
                        quantityColumn
                );

                String unit = cursor.getString(
                        unitColumn
                );

                // Create a card for each required ingredient.
                LinearLayout ingredientCard =
                        new LinearLayout(this);

                ingredientCard.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                ingredientCard.setGravity(
                        android.view.Gravity.CENTER_VERTICAL
                );

                ingredientCard.setPadding(
                        16, 14, 16, 14
                );

                ingredientCard.setBackgroundColor(
                        android.graphics.Color.WHITE
                );

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                cardParams.bottomMargin = 8;

                ingredientCard.setLayoutParams(cardParams);

                // Ingredient name.
                TextView nameView = new TextView(this);

                nameView.setText("•  " + ingredientName);
                nameView.setTextSize(15);
                nameView.setTextColor(
                        android.graphics.Color.rgb(32, 53, 45)
                );

                nameView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        )
                );

                // Required quantity.
                TextView quantityView = new TextView(this);

                quantityView.setText(
                        formatQuantity(quantity) + " " + unit
                );

                quantityView.setTextSize(14);

                quantityView.setTextColor(
                        android.graphics.Color.rgb(36, 107, 82)
                );

                quantityView.setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                );

                ingredientCard.addView(nameView);
                ingredientCard.addView(quantityView);

                layoutRecipeIngredients.addView(
                        ingredientCard
                );
            }

            if (layoutRecipeIngredients.getChildCount() == 0) {

                TextView emptyMessage = new TextView(this);

                emptyMessage.setText(
                        "No ingredient information is available."
                );

                emptyMessage.setTextSize(14);

                emptyMessage.setTextColor(
                        android.graphics.Color.rgb(104, 118, 110)
                );

                emptyMessage.setPadding(12, 12, 12, 12);

                layoutRecipeIngredients.addView(emptyMessage);
            }
        }
    }

    private String formatQuantity(double quantity) {

        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
    }

    // Bottom navigation: return to Home.
    public void openHome(View view) {
        Intent intent = new Intent(
                this,
                MainActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    // Bottom navigation: open Pantry.
    public void openPantry(View view) {
        startActivity(
                new Intent(this, PantryActivity.class)
        );
    }

    // Bottom navigation: return to recipe suggestions.
    public void openRecipes(View view) {
        finish();
    }

    // Bottom navigation: open Settings.
    public void openSettings(View view) {
        startActivity(
                new Intent(this, SettingsActivity.class)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
