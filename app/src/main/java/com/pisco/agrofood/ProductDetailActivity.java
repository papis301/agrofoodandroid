package com.pisco.agrofood;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private TextView textProductName, textProductPrice, textProductDescription, textTelephone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        textProductName = findViewById(R.id.textProductName);
        textProductPrice = findViewById(R.id.textProductPrice);
        textProductDescription = findViewById(R.id.textProductDescription);
        textTelephone = findViewById(R.id.textTelephone);

        // ✅ Récupération des données depuis l’intent
        String productName = getIntent().getStringExtra("name");
        String telephone = getIntent().getStringExtra("telephone");
        String productDescription = getIntent().getStringExtra("description");
        double price = getIntent().getDoubleExtra("price", 0.0);
        ArrayList<String> images = getIntent().getStringArrayListExtra("images");

        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();
            // Valeur par défaut si aucune image
            images.add("https://via.placeholder.com/300x300.png?text=Pas+d'image");
        }

        // ✅ Affichage des infos
        textProductName.setText(productName != null ? productName : "Produit inconnu");
        //textProductDescription.setText(productDescription != null ? productDescription : "Aucune description");
        textProductPrice.setText(price + " FCFA");
        textTelephone.setText("Telephone : "+telephone);

        // ✅ Adapter pour afficher toutes les images dans le ViewPager2
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        viewPagerImages.setAdapter(adapter);
    }
}
