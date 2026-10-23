package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

public class IngredientFormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "ingredient_id";
    public static final String EXTRA_NAME = "ingredient_name";
    public static final String EXTRA_QUANTITY = "ingredient_quantity";
    public static final String EXTRA_UNIT = "ingredient_unit";
    public static final String EXTRA_EXPIRY = "ingredient_expiry";

    private EditText edtName;
    private EditText edtQuantity;
    private EditText edtExpiry;

    private Spinner spinnerUnit;

    private PantryDatabaseHelper databaseHelper;

    private int ingredientId = -1;

    private final String[] units = {
            "pieces",
            "grams",
            "kilograms",
            "millilitres",
            "litres"
    };
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    edtExpiry.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        databaseHelper = new PantryDatabaseHelper(this);

        TextView txtTitle = findViewById(R.id.txtFormTitle);

        edtName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtIngredientQuantity);
        edtExpiry = findViewById(R.id.edtIngredientExpiry);

        edtExpiry.setFocusable(false);
        edtExpiry.setClickable(true);

        edtExpiry.setOnClickListener(v -> showDatePicker());

        spinnerUnit = findViewById(R.id.spinnerIngredientUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

        // Load existing ingredient information when editing
        if (getIntent().hasExtra(EXTRA_ID)) {

            ingredientId = getIntent().getIntExtra(EXTRA_ID, -1);

            txtTitle.setText("Edit Ingredient");
            btnSave.setText("Update Ingredient");

            edtName.setText(
                    getIntent().getStringExtra(EXTRA_NAME)
            );

            double quantity = getIntent().getDoubleExtra(
                    EXTRA_QUANTITY,
                    0
            );

            edtQuantity.setText(
                    quantity == Math.floor(quantity)
                            ? String.valueOf((long) quantity)
                            : String.valueOf(quantity)
            );

            String expiryDate = getIntent().getStringExtra(EXTRA_EXPIRY);

            if (expiryDate != null) {
                edtExpiry.setText(expiryDate);
            }

            String existingUnit = getIntent().getStringExtra(EXTRA_UNIT);

            if (existingUnit != null) {
                for (int i = 0; i < units.length; i++) {
                    if (units[i].equalsIgnoreCase(existingUnit)) {
                        spinnerUnit.setSelection(i);
                        break;
                    }
                }
            }
        }

        btnSave.setOnClickListener(v -> saveIngredient());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveIngredient() {

        String name = edtName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String expiryDate = edtExpiry.getText().toString().trim();

        // Validate ingredient name
        if (name.isEmpty()) {
            edtName.setError("Enter an ingredient name");
            edtName.requestFocus();
            return;
        }

        // Validate quantity input
        if (quantityText.isEmpty()) {
            edtQuantity.setError("Enter a quantity");
            edtQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid quantity");
            edtQuantity.requestFocus();
            return;
        }

        // Prevent zero, negative, NaN and infinite quantities
        if (quantity <= 0
                || Double.isInfinite(quantity)
                || Double.isNaN(quantity)) {

            edtQuantity.setError(
                    "Quantity must be greater than zero"
            );

            edtQuantity.requestFocus();
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        boolean success;

        // Insert a new ingredient or update an existing one
        if (ingredientId == -1) {

            success = databaseHelper.addIngredient(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

        } else {

            success = databaseHelper.updateIngredient(
                    ingredientId,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );
        }

        // Only display success when the database operation succeeds
        if (success) {

            String message = ingredientId == -1
                    ? "Ingredient added successfully"
                    : "Ingredient updated successfully";

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to save ingredient. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
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
