package com.pisco.agrofood;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGES_REQUEST = 1;
    private LinearLayout imageContainer;
    private EditText edtName, edtPrice;
    private Button btnChoose, btnUpload;
    private List<Uri> imageUris = new ArrayList<>();

    private static final String UPLOAD_URL = "https://agrofood.deydem.pro/upload_product.php"; // 🔵 Remplace par ton URL PHP

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        imageContainer = findViewById(R.id.imageContainer);
        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);

        btnChoose.setOnClickListener(v -> chooseImages());
        btnUpload.setOnClickListener(v -> uploadProduct());
    }

    private void chooseImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choisir des images"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            imageUris.clear();
            imageContainer.removeAllViews();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    addImageToLayout(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                addImageToLayout(imageUri);
            }
        }
    }

    private void addImageToLayout(Uri uri) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
        params.setMargins(8, 8, 8, 8);
        imageView.setLayoutParams(params);
        imageView.setImageURI(uri);
        imageContainer.addView(imageView);
    }

    private void uploadProduct() {
        String name = edtName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || imageUris.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs et choisir des images", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Envoi en cours...");
        progressDialog.show();

        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        multipartBuilder.addFormDataPart("name", name);
        multipartBuilder.addFormDataPart("price", price);

        try {
            for (int i = 0; i < imageUris.size(); i++) {
                Uri uri = imageUris.get(i);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                multipartBuilder.addFormDataPart(
                        "images[]",
                        "image_" + i + ".jpg",
                        RequestBody.create(imageBytes, MediaType.parse("image/jpeg"))
                );
            }
        } catch (IOException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors du traitement des images", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                runOnUiThread(() ->
                        Toast.makeText(AddProductActivity.this, "Erreur d'envoi : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.d("erreur", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(AddProductActivity.this, "Produit enregistré avec succès ✅", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(AddProductActivity.this, "Erreur du serveur ⚠️", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
