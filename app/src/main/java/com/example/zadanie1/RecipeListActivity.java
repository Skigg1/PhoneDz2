package com.example.zadanie1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeListActivity extends AppCompatActivity {

    private List<Recipe> allRecipes;
    private List<Recipe> filteredRecipes;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MYAPP", "RecipeListActivity onCreate started");

        applySavedLanguage();
        setContentView(R.layout.activity_recipe_list);

        loadRecipes();
        setupListView();
        setupFilterButtons();
    }

    private void applySavedLanguage() {
        String languageCode = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("app_language", "en");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void loadRecipes() {
        allRecipes = new ArrayList<>();
        allRecipes.add(new Recipe(R.string.dish_breakfast, R.string.ingredients_omelette, R.string.steps_omelette, "breakfast", 0));
        allRecipes.add(new Recipe(R.string.dish_lunch, R.string.ingredients_soup, R.string.steps_soup, "lunch", 0));
        allRecipes.add(new Recipe(R.string.dish_dinner, R.string.ingredients_salmon, R.string.steps_salmon, "dinner", 0));
        filteredRecipes = new ArrayList<>(allRecipes);
        Log.d("MYAPP", "Recipes loaded: " + allRecipes.size());
    }

    private void setupListView() {
        try {
            ListView listView = findViewById(R.id.listViewRecipes);
            if (listView == null) {
                Log.e("MYAPP", "ListView not found!");
                return;
            }
            Log.d("MYAPP", "ListView found");

            if (filteredRecipes == null || filteredRecipes.isEmpty()) {
                Log.e("MYAPP", "Recipes list is empty!");
                return;
            }

            adapter = new RecipeAdapter(this, filteredRecipes);
            listView.setAdapter(adapter);
            Log.d("MYAPP", "Adapter set successfully");

            listView.setOnItemClickListener((parent, view, position, id) -> {
                SoundManager.playSound(this, R.raw.open_recipe_sound);
                try {
                    Recipe selectedRecipe = filteredRecipes.get(position);
                    Intent intent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);
                    intent.putExtra("name_id", selectedRecipe.getNameStringId());
                    intent.putExtra("ingredients_id", selectedRecipe.getIngredientsStringId());
                    intent.putExtra("steps_id", selectedRecipe.getStepsStringId());
                    startActivity(intent);
                    Log.d("MYAPP", "Starting RecipeDetailActivity");
                } catch (Exception e) {
                    Log.e("MYAPP", "Error in click listener: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e("MYAPP", "Error in setupListView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFilterButtons() {
        Button btnBreakfast = findViewById(R.id.btnBreakfast);
        Button btnLunch = findViewById(R.id.btnLunch);
        Button btnDinner = findViewById(R.id.btnDinner);
        Button btnAll = findViewById(R.id.btnAll);

        View.OnClickListener filterListener = v -> {
            filteredRecipes.clear();
            String filterType = "";

            if (v.getId() == R.id.btnBreakfast) {
                filterType = "breakfast";
            } else if (v.getId() == R.id.btnLunch) {
                filterType = "lunch";
            } else if (v.getId() == R.id.btnDinner) {
                filterType = "dinner";
            }

            if (filterType.isEmpty()) {
                filteredRecipes.addAll(allRecipes);
            } else {
                for (Recipe recipe : allRecipes) {
                    if (recipe.getType().equals(filterType)) {
                        filteredRecipes.add(recipe);
                    }
                }
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        };

        btnBreakfast.setOnClickListener(filterListener);
        btnLunch.setOnClickListener(filterListener);
        btnDinner.setOnClickListener(filterListener);
        btnAll.setOnClickListener(filterListener);
    }
}