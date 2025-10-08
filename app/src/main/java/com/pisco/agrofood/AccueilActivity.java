package com.pisco.agrofood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccueilActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductAdapter adapter;
    private ArrayList<ProductModel> productList;
    private static final String PRODUCTS_URL = "https://agrofood.deydem.pro/get_products.php"; // 🔵 Ton lien PHP

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        progressBar = findViewById(R.id.progressBar);

        // ✅ Initialisation AVANT l’adapter
        productList = new ArrayList<>();

        // ✅ Configuration du RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        // ✅ Chargement des produits
        fetchProducts();

        // ✅ Navigation en bas
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_home);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home) {
                    // Reste sur la même page
                    return true;
                } else if (item.getItemId() == R.id.menu_add) {
                    startActivity(new Intent(AccueilActivity.this, HomeActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchProducts() {
        progressBar.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(PRODUCTS_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AccueilActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("API_RESPONSE", jsonResponse);

                    try {
                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        productList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ProductModel product = new ProductModel(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getDouble("price"),
                                    obj.optString("description", ""),
                                    obj.optString("images", "")
                            );
                            productList.add(product);
                        }

                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AccueilActivity.this, "Erreur lors du traitement JSON", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AccueilActivity.this, "Erreur serveur : " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
