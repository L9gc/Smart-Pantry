package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;

    private RecyclerView recyclerPantry;
    private TextView txtEmptyPantry;

    private IngredientAdapter ingredientAdapter;

    private final List<Ingredient> ingredientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new PantryDatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        ingredientAdapter = new IngredientAdapter(
                ingredientList,
                new IngredientAdapter.IngredientListener() {

                    @Override
                    public void onEdit(Ingredient ingredient) {
                        openEditForm(ingredient);
                    }

                    @Override
                    public void onDelete(Ingredient ingredient) {
                        confirmDelete(ingredient);
                    }
                }
        );

        recyclerPantry.setAdapter(ingredientAdapter);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    IngredientFormActivity.class
            );

            startActivity(intent);
        });

        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && ingredientAdapter != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {

        ingredientList.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow("expiry_date")
                );

                ingredientList.add(
                        new Ingredient(
                                id,
                                name,
                                quantity,
                                unit,
                                expiryDate
                        )
                );
            }
        } finally {
            cursor.close();
        }

        ingredientAdapter.notifyDataSetChanged();

        boolean isEmpty = ingredientList.isEmpty();

        txtEmptyPantry.setVisibility(
                isEmpty ? TextView.VISIBLE : TextView.GONE
        );

        recyclerPantry.setVisibility(
                isEmpty ? RecyclerView.GONE : RecyclerView.VISIBLE
        );
    }

    private void openEditForm(Ingredient ingredient) {

        Intent intent = new Intent(
                PantryActivity.this,
                IngredientFormActivity.class
        );

        intent.putExtra(
                IngredientFormActivity.EXTRA_ID,
                ingredient.getId()
        );

        intent.putExtra(
                IngredientFormActivity.EXTRA_NAME,
                ingredient.getName()
        );

        intent.putExtra(
                IngredientFormActivity.EXTRA_QUANTITY,
                ingredient.getQuantity()
        );

        intent.putExtra(
                IngredientFormActivity.EXTRA_UNIT,
                ingredient.getUnit()
        );

        intent.putExtra(
                IngredientFormActivity.EXTRA_EXPIRY,
                ingredient.getExpiryDate()
        );

        startActivity(intent);
    }

    private void confirmDelete(Ingredient ingredient) {

        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + ingredient.getName()
                                + "?"
                )
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {

                    databaseHelper.deleteIngredient(
                            ingredient.getId()
                    );

                    Toast.makeText(
                            PantryActivity.this,
                            "Ingredient deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadIngredients();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

}
