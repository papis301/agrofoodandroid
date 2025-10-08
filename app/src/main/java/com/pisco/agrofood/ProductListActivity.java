package com.pisco.agrofood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<ProductModel> productList;
    private ProductAdapter adapter;

    // 🔵 Remplace par ton URL PHP
    private static final String FETCH_URL = "https://agrofood.deydem.pro/get_products.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        fetchProducts();
    }

    private void fetchProducts() {
        progressBar.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(FETCH_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductListActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductListActivity.this, "Erreur JSON", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
}
