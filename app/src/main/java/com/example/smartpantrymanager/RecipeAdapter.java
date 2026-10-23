package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface RecipeListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private final RecipeListener listener;

    public RecipeAdapter(
            List<Recipe> recipes,
            RecipeListener listener
    ) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {
        Recipe recipe = recipes.get(position);

        holder.txtRecipeName.setText(recipe.getName());

        holder.txtRecipeDescription.setText(
                recipe.getDescription()
        );

        holder.txtRecipeTime.setText(
                recipe.getPreparationTime() + " minutes"
        );

        holder.btnViewRecipe.setOnClickListener(v ->
                listener.onRecipeClick(recipe)
        );

        holder.itemView.setOnClickListener(v ->
                listener.onRecipeClick(recipe)
        );
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtRecipeName;
        TextView txtRecipeDescription;
        TextView txtRecipeTime;
        Button btnViewRecipe;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRecipeName = itemView.findViewById(
                    R.id.txtRecipeName
            );

            txtRecipeDescription = itemView.findViewById(
                    R.id.txtRecipeDescription
            );

            txtRecipeTime = itemView.findViewById(
                    R.id.txtRecipeTime
            );

            btnViewRecipe = itemView.findViewById(
                    R.id.btnViewRecipe
            );
        }
    }
}