package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PantryActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etExpiryDate;

    private Spinner spinnerUnit;
    private LinearLayout ingredientsContainer;

    private Button btnAddIngredient;

    private PantryDatabaseHelper databaseHelper;

    private final String[] units = {
            "pieces",
            "grams",
            "kilograms",
            "millilitres",
            "litres"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // Connect Java variables to XML views.
        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        spinnerUnit = findViewById(R.id.spinnerUnit);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);

        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        // Initialise the database.
        databaseHelper = new PantryDatabaseHelper(this);

        // Populate the unit dropdown.
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

        // Connect the Add button.
        btnAddIngredient.setOnClickListener(v -> addIngredient());

        // Load saved ingredients.
        displayIngredients();
    }

    // CREATE: Save a new ingredient.
    private void addIngredient() {

        String name = etIngredientName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (name.isEmpty()) {
            etIngredientName.setError("Enter an ingredient name");
            etIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etQuantity.setError("Enter a quantity");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            etQuantity.setError("Quantity must be greater than zero");
            etQuantity.requestFocus();
            return;
        }

        if (!isValidExpiryDate(expiryDate)) {
            etExpiryDate.setError("Use a valid date in YYYY-MM-DD format");
            etExpiryDate.requestFocus();
            return;
        }

        boolean success = databaseHelper.addIngredient(
                name,
                quantity,
                unit,
                expiryDate.isEmpty() ? null : expiryDate
        );

        if (success) {
            Toast.makeText(
                    this,
                    "Ingredient added successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            clearForm();
            displayIngredients();

        } else {
            Toast.makeText(
                    this,
                    "Failed to save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // READ: Retrieve and display all saved ingredients.
    private void displayIngredients() {

        ingredientsContainer.removeAllViews();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {

            if (!cursor.moveToFirst()) {

                TextView emptyMessage = new TextView(this);
                emptyMessage.setText("No ingredients added yet.");
                emptyMessage.setTextSize(16);

                ingredientsContainer.addView(emptyMessage);
                return;
            }

            do {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_UNIT
                        )
                );

                String expiry = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_EXPIRY
                        )
                );

                // Create a vertical row for the ingredient.
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.VERTICAL);
                row.setPadding(0, 12, 0, 12);

                // Display the ingredient information.
                TextView details = new TextView(this);

                String text = name + " — " + quantity + " " + unit;

                if (expiry != null && !expiry.isEmpty()) {
                    text += "\nExpires: " + expiry;
                }

                details.setText(text);
                details.setTextSize(16);

                row.addView(details);

                // Create a horizontal row for the action buttons.
                LinearLayout buttons = new LinearLayout(this);
                buttons.setOrientation(LinearLayout.HORIZONTAL);

                // Edit button.
                Button editButton = new Button(this);
                editButton.setText("Edit");

                editButton.setOnClickListener(v ->
                        editIngredient(id, name, quantity, unit, expiry)
                );

                // Delete button.
                Button deleteButton = new Button(this);
                deleteButton.setText("Delete");

                deleteButton.setOnClickListener(v ->
                        deleteIngredient(id, name)
                );

                buttons.addView(editButton);
                buttons.addView(deleteButton);

                row.addView(buttons);

                // Add the completed row to the screen.
                ingredientsContainer.addView(row);

            } while (cursor.moveToNext());
        }
    }

    // UPDATE: Display a dialog to edit an existing ingredient.
    private void editIngredient(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        // Ingredient name field.
        EditText nameInput = new EditText(this);
        nameInput.setHint("Ingredient name");
        nameInput.setText(name);
        nameInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS
        );
        layout.addView(nameInput);

        // Quantity field.
        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setText(String.valueOf(quantity));
        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );
        layout.addView(quantityInput);

        // Unit dropdown.
        Spinner unitInput = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitInput.setAdapter(adapter);
        unitInput.setSelection(adapter.getPosition(unit));

        layout.addView(unitInput);

        // Expiry date field.
        EditText expiryInput = new EditText(this);
        expiryInput.setHint("Expiry date (optional: YYYY-MM-DD)");
        expiryInput.setText(expiryDate == null ? "" : expiryDate);
        expiryInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(expiryInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Ingredient")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {

                    String newName = nameInput.getText()
                            .toString().trim();

                    String newQuantityText = quantityInput.getText()
                            .toString().trim();

                    String newUnit = unitInput.getSelectedItem()
                            .toString();

                    String newExpiry = expiryInput.getText()
                            .toString().trim();

                    if (newName.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Ingredient name is required",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (newQuantityText.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Quantity is required",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    double newQuantity;

                    try {
                        newQuantity = Double.parseDouble(newQuantityText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(
                                this,
                                "Enter a valid quantity",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (!Double.isFinite(newQuantity)
                            || newQuantity <= 0) {
                        Toast.makeText(
                                this,
                                "Quantity must be greater than zero",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (!isValidExpiryDate(newExpiry)) {
                        Toast.makeText(
                                this,
                                "Enter a valid date in YYYY-MM-DD format",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    boolean updated = databaseHelper.updateIngredient(
                            id,
                            newName,
                            newQuantity,
                            newUnit,
                            newExpiry.isEmpty() ? null : newExpiry
                    );

                    Toast.makeText(
                            this,
                            updated
                                    ? "Ingredient updated successfully!"
                                    : "Failed to update ingredient",
                            Toast.LENGTH_SHORT
                    ).show();

                    displayIngredients();
                })
                .show();
    }

    // DELETE: Remove an ingredient after confirmation.
    private void deleteIngredient(int id, String name) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + name + "?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    boolean deleted =
                            databaseHelper.deleteIngredient(id);

                    Toast.makeText(
                            this,
                            deleted
                                    ? "Ingredient deleted successfully!"
                                    : "Failed to delete ingredient",
                            Toast.LENGTH_SHORT
                    ).show();

                    displayIngredients();
                })
                .show();
    }

    // Validate the optional expiry date.
    private boolean isValidExpiryDate(String date) {

        if (date.isEmpty()) {
            return true;
        }

        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Clear the Add Ingredient form.
    private void clearForm() {
        etIngredientName.setText("");
        etQuantity.setText("");
        etExpiryDate.setText("");
        spinnerUnit.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

}
