package com.pisco.agrofood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeTemplate extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductAdapter adapter;
    private EditText editSearch;
    private ArrayList<ProductModel> productList;
    private static final String PRODUCTS_URL = "https://agrofood.deydem.pro/get_products.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_template);

         editSearch = findViewById(R.id.editSearch);

        // ✅ Initialisation des vues
        recyclerView = findViewById(R.id.recyclerViewProducts);
        progressBar = findViewById(R.id.progressBar);

        // ✅ Initialisation des données
        productList = new ArrayList<>();

        // ✅ Configuration du RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        // ✅ Chargement des produits

        if(checkInternetConnection()){
            fetchProducts();
        }

        // 🔍 Recherche en temps réel
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ✅ Navigation en bas
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.menu_home);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home) {
                    return true;
                } else if (item.getItemId() == R.id.menu_add) {
                    startActivity(new Intent(HomeTemplate.this, HomeActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void filterProducts(String text) {
        ArrayList<ProductModel> filteredList = new ArrayList<>();
        String query = text.toLowerCase(Locale.ROOT);

        for (ProductModel product : productList) {
            if (product.getName().toLowerCase(Locale.ROOT).contains(query)) {
                filteredList.add(product);
            }
        }

        adapter.updateList(filteredList);
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
                    Toast.makeText(HomeTemplate.this, "Erreur DE CONNEXION " , Toast.LENGTH_SHORT).show();
                    Log.d("ERREUR", e.getMessage());
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
                            Toast.makeText(HomeTemplate.this, "Erreur JSON", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeTemplate.this, "Erreur serveur : " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
