package com.pisco.agrofood;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MesProduitsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapterMes productAdapter;
    private ArrayList<ProductModel> productList;
    private SharedPreferences sharedPreferences;

    private static final String TAG = "MesProduitsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_produits);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapterMes(productList, this);
        recyclerView.setAdapter(productAdapter);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        loadUserProducts();
    }

    private void loadUserProducts() {
        String firebaseId = sharedPreferences.getString("firebase_id", null);
        if (firebaseId == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = "https://agrofood.deydem.pro/get_products_by_user.php?firebase_id=" + firebaseId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray productsArray = jsonResponse.getJSONArray("data");
                            productList.clear();
                            for (int i = 0; i < productsArray.length(); i++) {
                                JSONObject productObj = productsArray.getJSONObject(i);

                                productList.add(new ProductModel(
                                        productObj.getInt("id"),
                                        productObj.getString("name"),
                                        productObj.getString("telephone"),
                                        productObj.getDouble("price"),
                                        productObj.getString("images")
                                ));
                            }
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erreur JSON", e);
                    }
                },
                error -> Log.e(TAG, "Erreur API", error));

        queue.add(request);
    }
}
