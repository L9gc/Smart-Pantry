# Smart Pantry Manager

## 1. Project Overview

Smart Pantry Manager is an Android application developed in Java for the Mobile App Development 700 module. The application helps users manage their pantry ingredients and discover recipes they can prepare using the ingredients they already have available.

The main objective is to reduce food waste by tracking pantry quantities and recommending recipes only when all required ingredients are available in sufficient quantities.

## 2. Features

* **Pantry Management:** Add, view, update, and delete pantry ingredients.
* **Ingredient Tracking:** Store ingredient names, quantities, units of measurement, and optional expiry dates.
* **Recipe Suggestions:** Display recipes that meet the application's ingredient availability requirements.
* **Strict Recipe Matching:** Only suggest a recipe when every required ingredient is available in the pantry in a sufficient quantity.
* **Recipe Details:** View recipe descriptions, preparation times, required ingredients, quantities, and cooking instructions.
* **Settings:** Access application preferences and pantry management options.
* **Persistent Storage:** Store pantry ingredients and recipe information locally using SQLite.
* **Navigation:** Move between the application's main screens using bottom navigation.

## 3. Technologies Used

| Technology       | Purpose                                               |
| ---------------- | ----------------------------------------------------- |
| Java             | Application logic and Android Activities              |
| Android Studio   | Development environment                               |
| XML              | User interface layouts                                |
| SQLite           | Local database and persistent storage                 |
| SQLiteOpenHelper | Database creation and management                      |
| RecyclerView     | Displaying pantry ingredients and recipe suggestions  |
| Intents          | Navigation and passing information between Activities |
| Git and GitHub   | Version control and source code hosting               |

## 4. Database Choice

Smart Pantry Manager uses SQLite for local data storage.

SQLite was selected because it provides a lightweight relational database that runs locally on the device without requiring a separate database server or an internet connection.

It is suitable for this application because pantry ingredients and recipe information can be stored in related tables and retrieved when needed. It also allows pantry changes to persist when the application is closed and reopened.

The database is managed using Android's `SQLiteOpenHelper` class.

### Database Structure

The application uses three main tables:

**ingredients**

* `id` — unique ingredient identifier.
* `name` — ingredient name.
* `quantity` — quantity available.
* `unit` — unit of measurement.
* `expiry_date` — optional expiry date.

**recipes**

* `recipe_id` — unique recipe identifier.
* `recipe_name` — recipe name.
* `description` — short recipe description.
* `instructions` — preparation instructions.
* `preparation_time` — estimated preparation time.

**recipe_ingredients**

* `recipe_ingredient_id` — unique record identifier.
* `recipe_id` — identifies the associated recipe.
* `ingredient_name` — required ingredient.
* `required_quantity` — quantity needed.
* `required_unit` — required unit of measurement.

The `recipe_ingredients` table associates recipes with their required ingredients.

## 5. Recipe Matching

The recipe suggestion feature compares the ingredients required by each recipe against the ingredients stored in the pantry.

A recipe qualifies as a suggestion only when every required ingredient is available in a sufficient quantity.

If even one required ingredient is missing or the available quantity is insufficient, the recipe is excluded from the suggested recipes list.

The application also accounts for supported unit conversions when comparing quantities.

This approach ensures that recipe suggestions reflect what the user can actually prepare with their current pantry inventory.

## 6. Project Structure

The project is organised into Activities, database functionality, data models, adapters, and XML layouts.

| Component                       | Responsibility                                                          |
| ------------------------------- | ----------------------------------------------------------------------- |
| `MainActivity.java`             | Main application screen                                                 |
| `PantryActivity.java`           | Displays pantry ingredients                                             |
| `IngredientFormActivity.java`   | Adds and edits ingredients                                              |
| `SuggestedRecipesActivity.java` | Displays recipes that match pantry contents                             |
| `RecipeDetailActivity.java`     | Displays recipe information and instructions                            |
| `SettingsActivity.java`         | Application settings and pantry management options                      |
| `PantryDatabaseHelper.java`     | Database creation, CRUD operations, recipe seeding, and recipe matching |
| `Ingredient.java`               | Ingredient data model                                                   |
| `Recipe.java`                   | Recipe data model                                                       |
| `IngredientAdapter.java`        | Binds ingredient data to the pantry list                                |
| `RecipeAdapter.java`            | Binds recipe data to the suggested recipes list                         |
| `BottomNavigationHelper.java`   | Configures navigation between application screens                       |

The user interface layouts are defined in XML resource files, while Java Activities handle application behaviour and user interactions.

## 7. Requirements

To open and run the project, you will need:

* Android Studio.
* A compatible Android SDK.
* A compatible Android emulator or Android device.
* Git, if cloning the repository through the command line.

## 8. Setup and Installation

### Option 1: Clone the Repository

1. Open a terminal.

2. Clone the repository:

   ```bash
   git clone YOUR_GITHUB_REPOSITORY_URL
   ```

3. Open Android Studio.

4. Select **Open** and choose the cloned project folder.

5. Allow Gradle to synchronise and download any required dependencies.

6. Start an Android emulator or connect a compatible Android device.

7. Run the application using the Android Studio Run button.

Replace `YOUR_GITHUB_REPOSITORY_URL` with the actual repository URL before publishing the README.

### Option 2: Open an Existing Project

1. Open Android Studio.
2. Select the existing Smart Pantry Manager project folder.
3. Allow Gradle synchronisation to finish.
4. Select an emulator or connected Android device.
5. Run the application.

## 9. Version Control

The project is maintained using Git and hosted on GitHub.

Version control is used to track development changes, maintain the source code, and document improvements made throughout the project.

## 10. Project Purpose

Smart Pantry Manager demonstrates the practical application of Android development concepts, including Activities, the Activity lifecycle, XML layouts, Intents, adapters, RecyclerView, and persistent database storage.

The project combines these concepts to create a functional pantry management application with recipe suggestions based on ingredient availability.
