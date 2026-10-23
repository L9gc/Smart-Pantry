package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        databaseHelper = new PantryDatabaseHelper(this);

        TextView txtTitle = findViewById(R.id.txtFormTitle);

        edtName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtIngredientQuantity);
        edtExpiry = findViewById(R.id.edtIngredientExpiry);

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

            edtExpiry.setText(
                    getIntent().getStringExtra(EXTRA_EXPIRY)
            );

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

        if (name.isEmpty()) {
            edtName.setError("Enter an ingredient name");
            edtName.requestFocus();
            return;
        }

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

        if (quantity <= 0 || Double.isInfinite(quantity)
                || Double.isNaN(quantity)) {
            edtQuantity.setError("Quantity must be greater than zero");
            edtQuantity.requestFocus();
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        if (ingredientId == -1) {

            databaseHelper.addIngredient(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            Toast.makeText(
                    this,
                    "Ingredient added successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            databaseHelper.updateIngredient(
                    ingredientId,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            Toast.makeText(
                    this,
                    "Ingredient updated successfully",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

}
