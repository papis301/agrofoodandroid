package com.pisco.agrofood;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductAdapterMes extends RecyclerView.Adapter<ProductAdapterMes.ViewHolder> {

    private ArrayList<ProductModel> productList;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ProductAdapterMes(ArrayList<ProductModel> productList, Context context) {
        this.productList = productList;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mesproduct, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        holder.txtName.setText(product.getName());
        holder.txtPrice.setText(product.getPrice() + " FCFA");
        holder.txtDescription.setText(product.getDescription());

            String imageString = product.getImages();

            if (imageString != null) {
                // Nettoyer le format JSON stringifié
                imageString = imageString
                        .replace("[", "")
                        .replace("]", "")
                        .replace("\"", "")
                        .replace("\\", ""); // retirer les backslashes

                String[] imagePaths = imageString.split(",");

                if (imagePaths.length > 0 && !imagePaths[0].isEmpty()) {
                    Glide.with(context)
                            .load("https://agrofood.deydem.pro/" + imagePaths[0].trim())
                            .into(holder.imageView);
                }
            } else {
                holder.imageView.setImageResource(R.drawable.ic_launcher_background);
            }



//        String[] imagePaths = product.getImages().split(",");
//        if (imagePaths.length > 0) {
//            Glide.with(context)
//                    .load("https://agrofood.deydem.pro/" + imagePaths[0].trim())
//                    .into(holder.imageView);
//        }

        holder.btnDelete.setOnClickListener(v -> deleteProduct(product.getId(), position));
        holder.btnEdit.setOnClickListener(v -> {
            Toast.makeText(context, "Bouton Modifier non implémenté", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void deleteProduct(int productId, int position) {
        String firebaseId = sharedPreferences.getString("firebase_id", null);
        if (firebaseId == null) return;

        String url = "https://agrofood.deydem.pro/delete_product.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(context, "Produit supprimé", Toast.LENGTH_SHORT).show();
                    productList.remove(position);
                    notifyItemRemoved(position);
                },
                error -> Toast.makeText(context, "Erreur suppression", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(productId));
                params.put("firebase_id", firebaseId);
                return params;
            }
        };
        queue.add(request);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtDescription;
        ImageView imageView;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imageView = itemView.findViewById(R.id.imageViewProduct);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
