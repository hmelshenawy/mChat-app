package com.haitham.mchatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    EditText ed_status, ed_setUserName;
    Button btn_updateProfile;
    CircleImageView profilePic;

    DatabaseReference db_ref;
    FirebaseAuth auth;
    StorageReference storageReference;

    String userID;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ed_setUserName = findViewById(R.id.ed_SetuserName);
        ed_status = findViewById(R.id.ed_status);
        btn_updateProfile = findViewById(R.id.btn_updateProfile);
        profilePic = findViewById(R.id.profile_image);

        auth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        retirve_UserData();


        btn_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = auth.getCurrentUser().getUid();
                String status = ed_status.getText().toString().trim();
                String setUserName = ed_setUserName.getText().toString().trim();

                if (userID != null && status != null) {

                    HashMap<String, Object> profileMap = new HashMap<>();
                    profileMap.put("USERID", userID);
                    profileMap.put("NAME", setUserName);
                    profileMap.put("STATUS", status);

                    db_ref.child("Users").child(userID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                if (imageUri != null) {

                                    upload_Image(imageUri);

                                }
                                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                                finish();

                                Toast.makeText(SettingActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                            } else {

                                String msg = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                } else {
                    Toast.makeText(SettingActivity.this, "Please put missing data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retirve_UserData() {

        final String userID = auth.getCurrentUser().getUid();

        db_ref.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("NAME") && dataSnapshot.hasChild("STATUS")) {

                    String status = dataSnapshot.child("STATUS").getValue().toString();
                    String userName = dataSnapshot.child("NAME").getValue().toString();

                    ed_setUserName.setText(userName);
                    ed_status.setText(status);

                    if (dataSnapshot.hasChild("IMAGE")) {

                        final String imgUri = dataSnapshot.child("IMAGE").getValue().toString();

                        storageReference.child("PROFILE_IMAGES").child(userID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Picasso.get().load(uri).into(profilePic);
                                // profilePic.setImageURI(uri);
                                System.out.println("uri is " + uri);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                                System.out.println("My error is: " + e.toString());
                                System.out.println(userID);
                                System.out.println(imgUri);
                            }
                        });


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void select_ProfilePhoto(View view) {

        Intent in = new Intent();
        in.setType("image/*");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(in, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {

            imageUri = data.getData();

            profilePic.setImageURI(imageUri);


        }
    }

    private void upload_Image(Uri imageUri) {

        final String userID = auth.getCurrentUser().getUid();

        storageReference.child("PROFILE_IMAGES").child(userID + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // String uri = taskSnapshot.getStorage().child(userID).getDownloadUrl().toString();
                //String uri =  taskSnapshot.getStorage().getDownloadUrl().toString();

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        db_ref.child("Users").child(userID).child("IMAGE").setValue(uri.toString());

                        Toast.makeText(SettingActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();


                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SettingActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
