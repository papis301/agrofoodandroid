package com.pisco.agrofood;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<ProductModel> productList;
    private Context context;

    public ProductAdapter(ArrayList<ProductModel> productList, Context context) {
        this.context = context;
        this.productList = (productList != null) ? productList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        holder.txtName.setText(product.getName());
        holder.txtPrice.setText(product.getPrice() + " FCFA");
        holder.txtDescription.setText(product.getDescription());



        String imagesRaw = product.getImages();
        String firstImageUrl = null;

        if (imagesRaw != null && !imagesRaw.isEmpty()) {
            try {
                // 🔹 Si c’est un tableau JSON (["uploads/img.jpg"])
                if (imagesRaw.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(imagesRaw);
                    if (jsonArray.length() > 0) {
                        firstImageUrl = jsonArray.getString(0);
                    }
                } else {
                    // 🔹 Si c’est juste une chaîne simple séparée par des virgules
                    String[] paths = imagesRaw.split(",");
                    firstImageUrl = paths[0];
                }

                if (firstImageUrl != null) {
                    // Nettoyage de l’URL
                    firstImageUrl = firstImageUrl
                            .replace("[", "")
                            .replace("]", "")
                            .replace("\"", "")
                            .replace("\\", "")
                            .trim();

                    // Ajouter le domaine complet si nécessaire
                    if (!firstImageUrl.startsWith("http")) {
                        firstImageUrl = "https://agrofood.deydem.pro/" + firstImageUrl;
                    }

                    // Chargement de l’image avec Glide
                    Glide.with(context)
                            .load(firstImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imageView);

                    String finalFirstImageUrl = firstImageUrl;
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra("name", product.getName());
                        intent.putExtra("price", product.getPrice());
                        intent.putExtra("telephone", product.getTelephone());
                        intent.putExtra("image", finalFirstImageUrl);
                        context.startActivity(intent);
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateList(ArrayList<ProductModel> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtDescription;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imageView = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}
