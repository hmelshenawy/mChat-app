package com.haitham.mchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText ed_username;
    EditText ed_password;
    Button btn_login;
    TextView tv_createAccount;

    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference db_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_username = findViewById(R.id.ed_Login_user);
        ed_password = findViewById(R.id.ed_Login_password);
        btn_login = findViewById(R.id.btn_login);
        tv_createAccount = findViewById(R.id.tv_CreateAccount);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db_ref = FirebaseDatabase.getInstance().getReference();

        progressBar = findViewById(R.id.Login_progressBar);


    }


    public void register_Account(View view) {

        Intent in = new Intent(this, RegisterActivity.class);
        startActivity(in);
    }


    public void log_in_account(View view) {

        String email = ed_username.getText().toString().trim();
        String password = ed_password.getText().toString().trim();


        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();
                    progressBar.setVisibility(View.INVISIBLE);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LoginActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);


                }
            });


        } else
            Toast.makeText(this, "Please enter valid username and password", Toast.LENGTH_SHORT).show();


    }


}
