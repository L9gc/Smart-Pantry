package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface IngredientListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> ingredientList;
    private final IngredientListener listener;

    public IngredientAdapter(
            List<Ingredient> ingredientList,
            IngredientListener listener) {

        this.ingredientList = ingredientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull IngredientViewHolder holder,
            int position) {

        Ingredient ingredient = ingredientList.get(position);

        holder.txtName.setText(ingredient.getName());

        holder.txtQuantity.setText(
                String.format(
                        Locale.getDefault(),
                        "Quantity: %s %s",
                        formatQuantity(ingredient.getQuantity()),
                        ingredient.getUnit()
                )
        );

        String expiryDate = ingredient.getExpiryDate();

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            expiryDate = "Not specified";
        }

        holder.txtExpiry.setText("Expiry date: " + expiryDate);

        holder.btnEdit.setOnClickListener(v ->
                listener.onEdit(ingredient));

        holder.btnDelete.setOnClickListener(v ->
                listener.onDelete(ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.format(
                    Locale.getDefault(),
                    "%.0f",
                    quantity
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.2f",
                quantity
        );
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtQuantity;
        TextView txtExpiry;

        Button btnEdit;
        Button btnDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtIngredientName);
            txtQuantity = itemView.findViewById(R.id.txtIngredientQuantity);
            txtExpiry = itemView.findViewById(R.id.txtIngredientExpiry);

            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }

}
