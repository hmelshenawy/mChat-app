package com.haitham.mchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    EditText ed_user, ed_password;
    Button btn_register;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference db_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_user = findViewById(R.id.ed_Reg_user);
        ed_password = findViewById(R.id.ed_reg_password);
        btn_register = findViewById(R.id.btn_reg);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db_ref = FirebaseDatabase.getInstance().getReference();
    }

    public void create_User(View view) {

        String email = ed_user.getText().toString().trim();
        String password = ed_password.getText().toString().trim();

        if (email != null && password != null) {


            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    String userID = auth.getCurrentUser().getUid();

                    db_ref.child("Users").child(userID).setValue("NA");

                    Toast.makeText(RegisterActivity.this, "New user created", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(RegisterActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                }
            });

        } else
            Toast.makeText(this, "Please enter valid username and password", Toast.LENGTH_SHORT).show();
    }
}
