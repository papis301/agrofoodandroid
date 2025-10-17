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
        holder.textTelephone.setText(product.getTelephone());

        String imagesRaw = product.getImages();
        ArrayList<String> imageUrls = new ArrayList<>();
        String firstImageUrl = null;

        if (imagesRaw != null && !imagesRaw.isEmpty()) {
            try {
                // 🔹 Si c’est un tableau JSON (["uploads/img.jpg", "uploads/img2.jpg"])
                if (imagesRaw.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(imagesRaw);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String url = jsonArray.getString(i)
                                .replace("[", "")
                                .replace("]", "")
                                .replace("\"", "")
                                .replace("\\", "")
                                .trim();
                        if (!url.startsWith("http")) {
                            url = "https://agrofood.deydem.pro/" + url;
                        }
                        imageUrls.add(url);
                    }
                } else {
                    // 🔹 Si c’est une chaîne simple séparée par des virgules
                    String[] paths = imagesRaw.split(",");
                    for (String path : paths) {
                        path = path.replace("\"", "").replace("\\", "").trim();
                        if (!path.startsWith("http")) {
                            path = "https://agrofood.deydem.pro/" + path;
                        }
                        imageUrls.add(path);
                    }
                }

                // 🔹 Affiche la première image
                if (!imageUrls.isEmpty()) {
                    firstImageUrl = imageUrls.get(0);
                    Glide.with(context)
                            .load(firstImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imageView);
                }

                ArrayList<String> finalImageUrls = new ArrayList<>(imageUrls);
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("name", product.getName());
                    intent.putExtra("price", product.getPrice());
                    intent.putExtra("telephone", product.getTelephone());
                    intent.putExtra("description", product.getDescription());
                    intent.putStringArrayListExtra("images", finalImageUrls); // 🔹 Toutes les images
                    context.startActivity(intent);
                });

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
        TextView txtName, txtPrice, txtDescription, textTelephone;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            textTelephone = itemView.findViewById(R.id.textTelephone);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imageView = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}
