package com.pisco.agrofood;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected boolean checkInternetConnection() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            showNoInternetDialog();
            return false;
        }
        return true;
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connexion Internet requise");
        builder.setMessage("Votre connexion Internet semble désactivée. Activez-la pour continuer.");

        builder.setCancelable(false);

        builder.setPositiveButton("Réessayer", (dialog, which) -> {
            dialog.dismiss();
            // 🔁 Revérifie la connexion
            if (!NetworkUtil.isInternetAvailable(this)) {
                showNoInternetDialog(); // relance si toujours coupé
            }
        });

        builder.setNegativeButton("Paramètres", (dialog, which) -> {
            // 🔗 Ouvre les paramètres Wi-Fi / données mobiles
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        });

        builder.show();
    }
}
