package com.pisco.agrofood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // 🔹 Récupération des vues
        TextView textVersion = findViewById(R.id.textVersion);
        LinearLayout layoutPhone = findViewById(R.id.layoutPhone);
        LinearLayout layoutFacebook = findViewById(R.id.layoutFacebook);
        LinearLayout layoutTikTok = findViewById(R.id.layoutTikTok);
        LinearLayout layoutInstagram = findViewById(R.id.layoutInstagram);
        LinearLayout layoutWebsite = findViewById(R.id.layoutWebsite);

        // 🔹 Version de l'application
        String versionName = "1.0"; // tu peux aussi utiliser BuildConfig.VERSION_NAME
        textVersion.setText("Version " + versionName);

        // 🔹 Numéro de téléphone
        layoutPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+221767741008")); // ton numéro
            startActivity(intent);
        });

        // 🔹 Facebook
//        layoutFacebook.setOnClickListener(v -> openLink("https://www.facebook.com/AgroFood"));
//
//        // 🔹 TikTok
//        layoutTikTok.setOnClickListener(v -> openLink("https://www.tiktok.com/@AgroFood"));
//
//        // 🔹 Instagram
//        layoutInstagram.setOnClickListener(v -> openLink("https://www.instagram.com/AgroFood"));
//
//        // 🔹 Site web
//        layoutWebsite.setOnClickListener(v -> openLink("https://www.agrofood.sn"));
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
