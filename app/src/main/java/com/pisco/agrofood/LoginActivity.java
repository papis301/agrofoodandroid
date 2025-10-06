package com.pisco.agrofood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private ImageView togglePassword;
    private Button btnLogin;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;

    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        togglePassword = findViewById(R.id.togglePassword);
        btnLogin = findViewById(R.id.btnLogin);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // ✅ Vérifier si l'utilisateur est déjà connecté
        checkIfUserIsLoggedIn();

        togglePassword.setOnClickListener(v -> togglePasswordVisibility());
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void checkIfUserIsLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedPhone = prefs.getString(KEY_PHONE, null);

        if (savedPhone != null) {
            // Utilisateur déjà connecté → redirection directe
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePassword.setImageResource(R.drawable.invisible100);
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePassword.setImageResource(R.drawable.visible100);
        }
        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void loginUser() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Numéro requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Mot de passe requis");
            return;
        }

        db.collection("usersagrofoof")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean userFound = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String storedPassword = document.getString("password");
                            if (storedPassword != null && storedPassword.equals(password)) {
                                userFound = true;
                                saveSession(phone);
                                Toast.makeText(LoginActivity.this, "Connexion réussie ✅", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }

                        if (!userFound) {
                            Toast.makeText(LoginActivity.this, "Numéro ou mot de passe incorrect ❌", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Erreur Firestore", task.getException());
                        Toast.makeText(LoginActivity.this, "Erreur de connexion Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveSession(String phone) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }
}
