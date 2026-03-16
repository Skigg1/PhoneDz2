package com.example.zadanie1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context context;
    private List<Recipe> recipes;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, R.layout.item_recipe, recipes);
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        }

        Recipe recipe = recipes.get(position);

        TextView textViewName = convertView.findViewById(R.id.textViewRecipeName);
        textViewName.setText(recipe.getNameStringId());

        return convertView;
    }
}