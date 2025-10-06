package com.pisco.agrofood;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword, edtConfirmPassword;
    private ImageView togglePassword, toggleConfirmPassword;
    private Button btnRegister;
    private FirebaseFirestore db;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        togglePassword = findViewById(R.id.togglePassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        togglePassword.setOnClickListener(v -> togglePasswordVisibility(edtPassword, true));
        toggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(edtConfirmPassword, false));

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void togglePasswordVisibility(EditText editText, boolean isMainPassword) {
        if (isMainPassword) {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.invisible100); // 👁️ icône cachée
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.visible100); // 👁️ icône visible
            }
            editText.setSelection(editText.getText().length());
        } else {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.invisible100);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.visible100);
            }
            editText.setSelection(editText.getText().length());
        }
    }

    private void registerUser() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Numéro requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Mot de passe requis");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Confirmez le mot de passe");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas ⚠️", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("phone", phone);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Utilisateur ajouté avec ID: " + documentReference.getId());
                    Toast.makeText(this, "Inscription réussie ✅", Toast.LENGTH_SHORT).show();

                    edtPhone.setText("");
                    edtPassword.setText("");
                    edtConfirmPassword.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Erreur lors de l'ajout", e);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
