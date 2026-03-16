package com.example.zadanie1;

public class Recipe {
    private int nameStringId;
    private int ingredientsStringId;
    private int stepsStringId;
    private String type;
    private int imageResourceId;

    public Recipe(int nameStringId, int ingredientsStringId, int stepsStringId, String type, int imageResourceId) {
        this.nameStringId = nameStringId;
        this.ingredientsStringId = ingredientsStringId;
        this.stepsStringId = stepsStringId;
        this.type = type;
        this.imageResourceId = imageResourceId;
    }

    public int getNameStringId() {
        return nameStringId;
    }

    public int getIngredientsStringId() {
        return ingredientsStringId;
    }

    public int getStepsStringId() {
        return stepsStringId;
    }

    public String getType() {
        return type;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}