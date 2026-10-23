package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class BottomNavigationHelper {

    public static void setup(Activity activity) {

        View navHome = activity.findViewById(R.id.navHome);
        View navPantry = activity.findViewById(R.id.navPantry);
        View navRecipes = activity.findViewById(R.id.navRecipes);
        View navSettings = activity.findViewById(R.id.navSettings);

        // Home navigation
        if (navHome != null) {
            navHome.setOnClickListener(v ->
                    navigateTo(activity, MainActivity.class)
            );
        }

        // Pantry navigation
        if (navPantry != null) {
            navPantry.setOnClickListener(v ->
                    navigateTo(activity, PantryActivity.class)
            );
        }

        // Recipes navigation
        if (navRecipes != null) {
            navRecipes.setOnClickListener(v ->
                    navigateTo(activity, SuggestedRecipesActivity.class)
            );
        }

        // Settings navigation
        if (navSettings != null) {
            navSettings.setOnClickListener(v ->
                    navigateTo(activity, SettingsActivity.class)
            );
        }
    }

    private static void navigateTo(
            Activity currentActivity,
            Class<?> destination
    ) {
        // Don't reopen the screen the user is already viewing
        if (currentActivity.getClass().equals(destination)) {
            return;
        }

        Intent intent = new Intent(currentActivity, destination);

        // Avoid creating duplicate copies of screens already in the stack
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        currentActivity.startActivity(intent);
    }
}