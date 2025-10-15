package com.pisco.agrofood;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textName, textPrice, textDescription, textTelephone, textFirebase, textDate;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imageProduct = findViewById(R.id.imageProduct);
        textName = findViewById(R.id.textName);
        textPrice = findViewById(R.id.textPrice);
        textDescription = findViewById(R.id.textDescription);
        btnBack = findViewById(R.id.btnBack);

        // ✅ Récupération des données passées depuis l’intent
        String name = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("image");

        // ✅ Affichage
        textName.setText(name);
        textPrice.setText(String.format("%.2f FCFA", price));
        textDescription.setText(description);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageProduct);
        } else {
            imageProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        // ✅ Retour
        btnBack.setOnClickListener(v -> finish());
    }
}