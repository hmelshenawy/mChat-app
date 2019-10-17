package com.haitham.mchatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haitham.mchatapp.adaptors.TabsPager_Adaptor;

public class MainActivity extends AppCompatActivity {

    public final static String CHANNEL_ID = "Channel_ID";
    Toolbar mtoolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabsPager_Adaptor tabs_adaptor;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference db_ref;
    NotificationChannel notificationChannel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtoolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("mChat App");

        viewPager = findViewById(R.id.mPAger);
        tabLayout = findViewById(R.id.mTab_Layout);
        tabs_adaptor = new TabsPager_Adaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(tabs_adaptor);

        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    protected void onStart() {
        super.onStart();

        check_CurrentUser();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {

            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (item.getItemId() == R.id.setting) {

            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (item.getItemId() == R.id.findFriend) {

            Intent in = new Intent(this, FindFriendActivity.class);
            startActivity(in);

        } else if (item.getItemId() == R.id.add_Group) {

            final EditText editText = new EditText(this);
            editText.setHint("e.g mChat Group");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Create new Group");

            builder.setView(editText);

            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    String groupName = editText.getText().toString().trim();


                    db_ref.child("GROUPS").child(groupName).setValue(" ").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(MainActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, "Check Internet Connection!!" + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }).show();
        }


        return true;
    }


    void notificatin_channel() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, "mchannel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("mNoteChannel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

            System.out.println(CHANNEL_ID);
        }

    }


    public void mUser_data_verfication() {

        String userID = auth.getCurrentUser().getUid();

        System.out.println("data verfication " + userID);

        db_ref.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("NAME").exists()) {

                    // Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {

                    Intent in = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(in);
                    finish();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void check_CurrentUser() {

        db_ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {

            System.out.println("if loop");

            Intent in = new Intent(this, LoginActivity.class);
            startActivity(in);

            finish();

        } else {

            System.out.println("check user else loop");
            notificatin_channel();

            mUser_data_verfication();

        }
    }
}
