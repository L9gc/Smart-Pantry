package com.example.smartpantrymanager;

public class Recipe {

    private int id;
    private String name;
    private String description;
    private String instructions;
    private int preparationTime;

    public Recipe(
            int id,
            String name,
            String description,
            String instructions,
            int preparationTime
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.preparationTime = preparationTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructions() {
        return instructions;
    }

    public int getPreparationTime() {
        return preparationTime;
    }
}