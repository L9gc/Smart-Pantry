package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch expiryAlertsSwitch;
    private Button btnClearPantry;

    private PantryDatabaseHelper databaseHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        expiryAlertsSwitch = findViewById(R.id.expiryAlertsSwitch);
        btnClearPantry = findViewById(R.id.btnClearPantry);

        databaseHelper = new PantryDatabaseHelper(this);

        preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        // Load the saved expiry-alert preference.
        boolean alertsEnabled = preferences.getBoolean(
                "expiry_alerts_enabled",
                true
        );

        expiryAlertsSwitch.setChecked(alertsEnabled);

        // Save changes whenever the switch is toggled.
        expiryAlertsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit()
                    .putBoolean("expiry_alerts_enabled", isChecked)
                    .apply();

            String message = isChecked
                    ? "Expiry alert preference enabled."
                    : "Expiry alert preference disabled.";

            Toast.makeText(
                    SettingsActivity.this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Confirm before deleting pantry ingredients.
        btnClearPantry.setOnClickListener(v -> showClearPantryDialog());
    }

    private void showClearPantryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Pantry?")
                .setMessage(
                        "Are you sure you want to remove all ingredients " +
                                "from your pantry? This action cannot be undone."
                )
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Clear", (dialog, which) -> clearPantry())
                .show();
    }

    private void clearPantry() {
        Cursor cursor = databaseHelper.getAllIngredients();
        int deletedCount = 0;

        if (cursor != null) {
            try {
                int idColumn = cursor.getColumnIndexOrThrow("id");

                while (cursor.moveToNext()) {
                    int ingredientId = cursor.getInt(idColumn);

                    if (databaseHelper.deleteIngredient(ingredientId)) {
                        deletedCount++;
                    }
                }
            } finally {
                cursor.close();
            }
        }

        Toast.makeText(
                this,
                deletedCount > 0
                        ? "Pantry cleared successfully."
                        : "Your pantry is already empty.",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Bottom navigation methods

    public void openHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void openPantry(View view) {
        startActivity(new Intent(this, PantryActivity.class));
        finish();
    }

    public void openRecipes(View view) {
        startActivity(new Intent(this, SuggestedRecipesActivity.class));
        finish();
    }
}