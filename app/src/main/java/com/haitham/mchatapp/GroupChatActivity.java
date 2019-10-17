package com.haitham.mchatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    EditText ed_messageBody;
    TextView ed_groupChatBody;
    Button btn_GroupSend;
    ScrollView scrollView;

    String groupName, currentTime, currentDate, currentUser, currentUserName;

    DatabaseReference db_ref;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupName = getIntent().getStringExtra("GROUP_NAME");

        getSupportActionBar().setTitle(groupName);

        ed_groupChatBody = findViewById(R.id.ed_GroupChat2);
        ed_messageBody = findViewById(R.id.ed_messageText2);
        btn_GroupSend = findViewById(R.id.btn_groupSend2);
        scrollView = findViewById(R.id.scroll2);


        db_ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        currentUser = auth.getUid();

        getUserName();

        showMessage();

        keyBoard_Listner();

        scrollDown();
    }

    private void scrollDown() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 200);
    }


    public void send_message(View view) {

        calender();

        HashMap<String, Object> map = new HashMap<>();

        map.put("MESSAGE_BODY", ed_messageBody.getText().toString());
        map.put("TIME", currentTime);
        map.put("DATE", currentDate);
        map.put("USER", currentUserName);

        db_ref.child("GROUPS").child(groupName).child(System.currentTimeMillis() + "").setValue(map);

        ed_messageBody.getText().clear();

    }


    public void keyBoard_Listner() {

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                scrollDown();

            }
        });

    }


    public void calender() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        currentDate = dateFormat.format(calendar.getTimeInMillis());
        currentTime = timeFormat.format(calendar.getTimeInMillis());

    }


    public String getUserName() {


        db_ref.child("Users").child(currentUser).child("NAME").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUserName = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                System.out.println(databaseError.toException().getMessage());

            }
        });

        return currentUserName;
    }

    public void displayMessage(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {

            String date = ((DataSnapshot) iterator.next()).getValue().toString();
            String message = ((DataSnapshot) iterator.next()).getValue().toString();
            String time = ((DataSnapshot) iterator.next()).getValue().toString();
            String user = ((DataSnapshot) iterator.next()).getValue().toString();

            ed_groupChatBody.append(user + " :\n    " + message + "\n\n\n");

            scrollDown();
        }
    }

    public void showMessage() {


        db_ref.child("GROUPS").child(groupName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                displayMessage(dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                displayMessage(dataSnapshot);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
