package com.example.smartpantrymanager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    private Switch expiryAlertsSwitch;
    private Button btnClearPantry;

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    private PantryDatabaseHelper databaseHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the notification permission launcher once.
        notificationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        granted -> {

                            if (granted) {

                                preferences.edit()
                                        .putBoolean("expiry_alerts_enabled", true)
                                        .apply();

                                Toast.makeText(
                                        this,
                                        "Expiry notifications enabled.",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                expiryAlertsSwitch.setChecked(false);

                                preferences.edit()
                                        .putBoolean("expiry_alerts_enabled", false)
                                        .apply();

                                Toast.makeText(
                                        this,
                                        "Notification permission was not granted.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

        setContentView(R.layout.activity_settings);

        BottomNavigationHelper.setup(this);

        // Connect the XML views.
        expiryAlertsSwitch = findViewById(R.id.expiryAlertsSwitch);
        btnClearPantry = findViewById(R.id.btnClearPantry);

        // Initialize the database and preferences.
        databaseHelper = new PantryDatabaseHelper(this);

        preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        // Load the saved preference without triggering the listener.
        boolean alertsEnabled = preferences.getBoolean(
                "expiry_alerts_enabled",
                false
        );

        expiryAlertsSwitch.setChecked(alertsEnabled);

        // Handle changes to the expiry-alert switch.
        expiryAlertsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            && ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED) {

                        notificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                        );

                        return;
                    }

                    preferences.edit()
                            .putBoolean("expiry_alerts_enabled", isChecked)
                            .apply();

                    String message = isChecked
                            ? "Expiry alerts enabled."
                            : "Expiry alerts disabled.";

                    Toast.makeText(
                            SettingsActivity.this,
                            message,
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        // Handle the Clear Pantry button.
        btnClearPantry.setOnClickListener(
                v -> showClearPantryDialog()
        );
    }

    private void showClearPantryDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Clear Pantry?")
                .setMessage(
                        "Are you sure you want to remove all ingredients "
                                + "from your pantry? This action cannot be undone."
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss()
                )
                .setPositiveButton(
                        "Clear",
                        (dialog, which) -> clearPantry()
                )
                .show();
    }

    private void clearPantry() {
        int deletedCount = databaseHelper.clearPantry();

        Toast.makeText(
                this,
                deletedCount > 0
                        ? "Pantry cleared successfully."
                        : "Your pantry is already empty.",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Bottom navigation methods.

    public void openHome(View view) {

        startActivity(
                new Intent(this, MainActivity.class)
        );

        finish();
    }

    public void openPantry(View view) {

        startActivity(
                new Intent(this, PantryActivity.class)
        );

        finish();
    }

    public void openRecipes(View view) {

        startActivity(
                new Intent(this, SuggestedRecipesActivity.class)
        );

        finish();
    }
}