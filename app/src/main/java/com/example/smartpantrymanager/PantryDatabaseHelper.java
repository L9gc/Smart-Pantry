package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Ingredients table
    public static final String TABLE_INGREDIENTS = "ingredients";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";

    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_PREPARATION_TIME = "preparation_time";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String COL_RECIPE_INGREDIENT_ID =
            "recipe_ingredient_id";

    public static final String COL_REQUIRED_NAME = "ingredient_name";
    public static final String COL_REQUIRED_QUANTITY = "required_quantity";
    public static final String COL_REQUIRED_UNIT = "required_unit";


    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Create the database tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createIngredientsTable =
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_QUANTITY + " REAL NOT NULL, " +
                        COL_UNIT + " TEXT NOT NULL, " +
                        COL_EXPIRY + " TEXT" +
                        ")";

        String createRecipesTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_RECIPE_NAME + " TEXT NOT NULL, " +
                        COL_DESCRIPTION + " TEXT, " +
                        COL_INSTRUCTIONS + " TEXT, " +
                        COL_PREPARATION_TIME + " INTEGER NOT NULL" +
                        ")";

        String createRecipeIngredientsTable =
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        COL_RECIPE_INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COL_RECIPE_ID + " INTEGER NOT NULL, " +

                        COL_REQUIRED_NAME + " TEXT NOT NULL, " +

                        COL_REQUIRED_QUANTITY + " REAL NOT NULL, " +

                        COL_REQUIRED_UNIT + " TEXT NOT NULL, " +

                        "FOREIGN KEY (" + COL_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + COL_RECIPE_ID + ")" +
                        ")";

        db.execSQL(createIngredientsTable);
        db.execSQL(createRecipesTable);
        db.execSQL(createRecipeIngredientsTable);

        seedRecipes(db);
    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        // Database migrations will be implemented if the schema changes.
    }


    // ---------------------------------------------------------
    // INGREDIENT CRUD OPERATIONS
    // ---------------------------------------------------------

    // Add an ingredient
    public boolean addIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY, expiryDate);

        long result = db.insert(TABLE_INGREDIENTS, null, values);

        return result != -1;
    }


    // Retrieve all ingredients
    public Cursor getAllIngredients() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " COLLATE NOCASE ASC"
        );
    }


    // Update an ingredient
    public boolean updateIngredient(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY, expiryDate);

        int result = db.update(
                TABLE_INGREDIENTS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }


    // Delete an ingredient
    public boolean deleteIngredient(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_INGREDIENTS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // Delete all ingredients from the pantry.
    public int clearPantry() {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_INGREDIENTS,
                null,
                null
        );
    }



    // ---------------------------------------------------------
    // RECIPE OPERATIONS
    // ---------------------------------------------------------

    // Retrieve all recipes
    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COL_RECIPE_NAME + " COLLATE NOCASE ASC"
        );

        try {

            while (cursor.moveToNext()) {

                Recipe recipe = new Recipe(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_RECIPE_ID)
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_DESCRIPTION)
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        COL_PREPARATION_TIME
                                )
                        )
                );

                recipes.add(recipe);
            }

        } finally {
            cursor.close();
        }

        return recipes;
    }


    // Retrieve one recipe using its ID
    public Recipe getRecipeById(int recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        );

        try {

            if (cursor.moveToFirst()) {

                return new Recipe(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_RECIPE_ID)
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_DESCRIPTION)
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        COL_PREPARATION_TIME
                                )
                        )
                );
            }

        } finally {
            cursor.close();
        }

        return null;
    }


    // Retrieve the ingredients required for a recipe
    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                COL_REQUIRED_NAME + " COLLATE NOCASE ASC"
        );
    }


    // ---------------------------------------------------------
    // STRICT RECIPE MATCHING
    // ---------------------------------------------------------

    public List<Recipe> getSuggestedRecipes() {

        List<Recipe> suggestions = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        // Store pantry quantities using normalized ingredient names
        Map<String, Double> pantryQuantities = new HashMap<>();

        // Track the measurement dimension for each pantry ingredient
        Map<String, String> pantryUnits = new HashMap<>();

        Cursor pantryCursor = getAllIngredients();

        try {

            while (pantryCursor.moveToNext()) {

                String name = pantryCursor.getString(
                        pantryCursor.getColumnIndexOrThrow(COL_NAME)
                );

                double quantity = pantryCursor.getDouble(
                        pantryCursor.getColumnIndexOrThrow(COL_QUANTITY)
                );

                String unit = pantryCursor.getString(
                        pantryCursor.getColumnIndexOrThrow(COL_UNIT)
                );

                String key = normalizeName(name);
                String dimension = getDimension(unit);

                if (dimension == null) {
                    continue;
                }

                // Convert to a common base unit before comparing quantities
                double normalizedQuantity =
                        convertToBaseUnit(quantity, unit);

                if (pantryUnits.containsKey(key)) {

                    // Do not combine incompatible measurements
                    if (!pantryUnits.get(key).equals(dimension)) {
                        continue;
                    }

                    pantryQuantities.put(
                            key,
                            pantryQuantities.get(key) + normalizedQuantity
                    );

                } else {

                    pantryQuantities.put(key, normalizedQuantity);
                    pantryUnits.put(key, dimension);
                }
            }

        } finally {
            pantryCursor.close();
        }


        // Check each recipe against the pantry
        List<Recipe> allRecipes = getAllRecipes();

        for (Recipe recipe : allRecipes) {

            boolean canPrepare = true;

            Cursor requiredCursor =
                    getRecipeIngredients(recipe.getId());

            try {

                while (requiredCursor.moveToNext()) {

                    String requiredName = requiredCursor.getString(
                            requiredCursor.getColumnIndexOrThrow(
                                    COL_REQUIRED_NAME
                            )
                    );

                    double requiredQuantity = requiredCursor.getDouble(
                            requiredCursor.getColumnIndexOrThrow(
                                    COL_REQUIRED_QUANTITY
                            )
                    );

                    String requiredUnit = requiredCursor.getString(
                            requiredCursor.getColumnIndexOrThrow(
                                    COL_REQUIRED_UNIT
                            )
                    );

                    String key = normalizeName(requiredName);

                    String requiredDimension = getDimension(requiredUnit);

                    // Unsupported or incompatible units cannot match
                    if (requiredDimension == null ||
                            !pantryUnits.containsKey(key) ||
                            !pantryUnits.get(key).equals(requiredDimension)) {

                        canPrepare = false;
                        break;
                    }

                    double normalizedRequired =
                            convertToBaseUnit(
                                    requiredQuantity,
                                    requiredUnit
                            );

                    double available = pantryQuantities.get(key);

                    // Allow a tiny tolerance for floating-point calculations
                    if (available + 0.000001 < normalizedRequired) {

                        canPrepare = false;
                        break;
                    }
                }

            } finally {
                requiredCursor.close();
            }

            if (canPrepare) {
                suggestions.add(recipe);
            }
        }

        return suggestions;
    }


    // Normalize names so "Flour" and "flour" match
    private String normalizeName(String name) {
        String normalized = name.trim().toLowerCase(Locale.ROOT);

        // Convert common "-ies" plurals to "-y".
        if (normalized.endsWith("ies") && normalized.length() > 4) {
            return normalized.substring(0, normalized.length() - 3) + "y";
        }

        // Handle common plurals such as dishes, boxes and glasses.
        if ((normalized.endsWith("ches")
                || normalized.endsWith("shes")
                || normalized.endsWith("sses")
                || normalized.endsWith("xes")
                || normalized.endsWith("zes"))
                && normalized.length() > 4) {
            return normalized.substring(0, normalized.length() - 2);
        }

        // Handle tomatoes and potatoes.
        if (normalized.endsWith("oes") && normalized.length() > 4) {
            return normalized.substring(0, normalized.length() - 2);
        }

        // Handle most other regular plurals.
        if (normalized.endsWith("s")
                && !normalized.endsWith("ss")
                && normalized.length() > 3) {
            return normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }



    // Determine which measurement category a unit belongs to
    private String getDimension(String unit) {

        String normalized = unit.trim().toLowerCase(Locale.ROOT);

        switch (normalized) {

            case "gram":
            case "grams":
            case "g":
            case "kilogram":
            case "kilograms":
            case "kg":
                return "weight";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "volume";

            case "piece":
            case "pieces":
            case "egg":
            case "eggs":
            case "unit":
            case "units":
                return "count";

            default:
                return null;
        }
    }


    // Convert compatible quantities to a base unit
    private double convertToBaseUnit(double quantity, String unit) {

        String normalized = unit.trim().toLowerCase(Locale.ROOT);

        switch (normalized) {

            case "kilogram":
            case "kilograms":
            case "kg":
                return quantity * 1000.0;

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return quantity * 1000.0;

            default:
                return quantity;
        }
    }


    // ---------------------------------------------------------
    // INITIAL RECIPE DATA
    // ---------------------------------------------------------

    private void seedRecipes(SQLiteDatabase db) {

        db.beginTransaction();

        try {

            // Recipe 1
            addRecipe(db,
                    "Pancakes",
                    "Soft homemade pancakes.",
                    "1. Mix flour, milk, egg and sugar. " +
                            "2. Pour batter into a lightly oiled pan. " +
                            "3. Cook both sides until golden.",
                    20,
                    new String[][]{
                            {"Flour", "200", "grams"},
                            {"Milk", "250", "millilitres"},
                            {"Egg", "2", "pieces"},
                            {"Sugar", "20", "grams"}
                    });

            // Recipe 2
            addRecipe(db,
                    "Scrambled Eggs",
                    "A simple egg breakfast.",
                    "1. Beat the eggs. " +
                            "2. Cook in a lightly oiled pan while stirring.",
                    10,
                    new String[][]{
                            {"Egg", "2", "pieces"},
                            {"Milk", "30", "millilitres"},
                            {"Butter", "10", "grams"}
                    });

            // Recipe 3
            addRecipe(db,
                    "French Toast",
                    "Golden egg-coated bread.",
                    "1. Beat eggs with milk. " +
                            "2. Dip bread in the mixture. " +
                            "3. Fry until golden.",
                    15,
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Egg", "2", "pieces"},
                            {"Milk", "100", "millilitres"}
                    });

            // Recipe 4
            addRecipe(db,
                    "Tomato Pasta",
                    "Pasta with a simple tomato sauce.",
                    "1. Boil pasta until tender. " +
                            "2. Cook chopped tomatoes with oil and garlic. " +
                            "3. Combine and serve.",
                    25,
                    new String[][]{
                            {"Pasta", "200", "grams"},
                            {"Tomato", "200", "grams"},
                            {"Olive Oil", "15", "millilitres"},
                            {"Garlic", "5", "grams"}
                    });

            // Recipe 5
            addRecipe(db,
                    "Garlic Bread",
                    "Toasted bread with garlic butter.",
                    "1. Mix softened butter with crushed garlic. " +
                            "2. Spread on bread. " +
                            "3. Bake until toasted.",
                    15,
                    new String[][]{
                            {"Bread", "4", "pieces"},
                            {"Butter", "30", "grams"},
                            {"Garlic", "5", "grams"}
                    });

            // Recipe 6
            addRecipe(db,
                    "Omelette",
                    "A quick cooked egg dish.",
                    "1. Beat the eggs. " +
                            "2. Melt butter in a pan. " +
                            "3. Cook the eggs until set.",
                    10,
                    new String[][]{
                            {"Egg", "3", "pieces"},
                            {"Butter", "10", "grams"},
                            {"Milk", "20", "millilitres"}
                    });

            // Recipe 7
            addRecipe(db,
                    "Banana Smoothie",
                    "A creamy banana drink.",
                    "1. Peel the banana. " +
                            "2. Blend with milk and honey.",
                    5,
                    new String[][]{
                            {"Banana", "2", "pieces"},
                            {"Milk", "250", "millilitres"},
                            {"Honey", "15", "grams"}
                    });

            // Recipe 8
            addRecipe(db,
                    "Mashed Potatoes",
                    "Creamy mashed potatoes.",
                    "1. Boil potatoes until soft. " +
                            "2. Mash with butter and milk.",
                    25,
                    new String[][]{
                            {"Potato", "400", "grams"},
                            {"Butter", "30", "grams"},
                            {"Milk", "60", "millilitres"}
                    });

            // Recipe 9
            addRecipe(db,
                    "Simple Fried Rice",
                    "Rice stir-fried with egg and vegetables.",
                    "1. Cook the rice. " +
                            "2. Scramble the egg. " +
                            "3. Stir-fry rice with peas and egg.",
                    25,
                    new String[][]{
                            {"Rice", "200", "grams"},
                            {"Egg", "1", "pieces"},
                            {"Peas", "100", "grams"},
                            {"Olive Oil", "10", "millilitres"}
                    });

            // Recipe 10
            addRecipe(db,
                    "Tomato Omelette",
                    "An omelette with fresh tomatoes.",
                    "1. Beat the eggs. " +
                            "2. Add chopped tomatoes. " +
                            "3. Cook in a pan until set.",
                    12,
                    new String[][]{
                            {"Egg", "2", "pieces"},
                            {"Tomato", "100", "grams"},
                            {"Butter", "10", "grams"}
                    });

            // Recipe 11
            addRecipe(db,
                    "Peanut Butter Toast",
                    "A quick toasted snack.",
                    "1. Toast the bread. " +
                            "2. Spread peanut butter on each slice.",
                    5,
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Peanut Butter", "30", "grams"}
                    });

            // Recipe 12
            addRecipe(db,
                    "Apple Oatmeal",
                    "Warm oats with apple.",
                    "1. Cook oats with milk. " +
                            "2. Chop the apple and add it. " +
                            "3. Cook until soft.",
                    12,
                    new String[][]{
                            {"Oats", "50", "grams"},
                            {"Milk", "200", "millilitres"},
                            {"Apple", "1", "pieces"}
                    });

            // Recipe 13
            addRecipe(db,
                    "Simple Salad",
                    "A fresh tomato and cucumber salad.",
                    "1. Chop the vegetables. " +
                            "2. Add olive oil. " +
                            "3. Mix and serve.",
                    10,
                    new String[][]{
                            {"Tomato", "150", "grams"},
                            {"Cucumber", "150", "grams"},
                            {"Olive Oil", "10", "millilitres"}
                    });

            // Recipe 14
            addRecipe(db,
                    "Boiled Eggs on Toast",
                    "Boiled eggs served with toast.",
                    "1. Boil the eggs until cooked. " +
                            "2. Toast the bread. " +
                            "3. Serve together.",
                    12,
                    new String[][]{
                            {"Egg", "2", "pieces"},
                            {"Bread", "2", "pieces"}
                    });

            // Recipe 15
            addRecipe(db,
                    "Simple Biscuits",
                    "Basic homemade biscuits.",
                    "1. Mix flour, sugar and butter. " +
                            "2. Shape the dough. " +
                            "3. Bake until lightly golden.",
                    30,
                    new String[][]{
                            {"Flour", "200", "grams"},
                            {"Sugar", "80", "grams"},
                            {"Butter", "100", "grams"}
                    });

            // Recipe 16
            addRecipe(db,
                    "Potato Wedges",
                    "Oven-baked potato wedges.",
                    "1. Cut potatoes into wedges. " +
                            "2. Toss with olive oil. " +
                            "3. Bake until tender and golden.",
                    35,
                    new String[][]{
                            {"Potato", "400", "grams"},
                            {"Olive Oil", "20", "millilitres"}
                    });

            // Recipe 17
            addRecipe(db,
                    "Honey Banana Toast",
                    "Toast topped with banana and honey.",
                    "1. Toast the bread. " +
                            "2. Slice the banana over it. " +
                            "3. Drizzle with honey.",
                    5,
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Banana", "1", "pieces"},
                            {"Honey", "15", "grams"}
                    });

            // Recipe 18
            addRecipe(db,
                    "Creamy Pasta",
                    "A simple creamy pasta dish.",
                    "1. Boil the pasta. " +
                            "2. Warm the milk with butter. " +
                            "3. Mix with pasta and serve.",
                    20,
                    new String[][]{
                            {"Pasta", "200", "grams"},
                            {"Milk", "100", "millilitres"},
                            {"Butter", "20", "grams"}
                    });

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }


    // Insert a recipe and its required ingredients
    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String description,
            String instructions,
            int preparationTime,
            String[][] ingredients
    ) {

        ContentValues recipeValues = new ContentValues();

        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_DESCRIPTION, description);
        recipeValues.put(COL_INSTRUCTIONS, instructions);
        recipeValues.put(COL_PREPARATION_TIME, preparationTime);

        long recipeId = db.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        if (recipeId == -1) {
            throw new IllegalStateException(
                    "Could not insert recipe: " + name
            );
        }

        for (String[] ingredient : ingredients) {

            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put(COL_RECIPE_ID, recipeId);
            ingredientValues.put(COL_REQUIRED_NAME, ingredient[0]);
            ingredientValues.put(
                    COL_REQUIRED_QUANTITY,
                    Double.parseDouble(ingredient[1])
            );
            ingredientValues.put(COL_REQUIRED_UNIT, ingredient[2]);

            long result = db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );

            if (result == -1) {
                throw new IllegalStateException(
                        "Could not insert ingredient for recipe: " + name
                );
            }
        }
    }
}