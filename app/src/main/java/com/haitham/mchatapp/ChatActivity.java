package com.haitham.mchatapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haitham.mchatapp.adaptors.Chat_List_Adaptor;
import com.haitham.mchatapp.models.Chat_Text_Item;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String name, ID, imgUri;
    String currentUser;

    EditText ed_messageText;
    TextView ed_ChatBody;
    ImageView btn_Send;
    CircleImageView chat_Img;

    ListView lv_chat;
    Chat_List_Adaptor adaptor;
    ArrayList<Chat_Text_Item> chat_text_items;

    DatabaseReference db_ref;
    DatabaseReference db_root;
    FirebaseAuth auth;

    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = getIntent().getStringExtra("NAME");
        ID = getIntent().getStringExtra("ID");
        imgUri = getIntent().getStringExtra("IMG_URI");

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        chat_text_items = new ArrayList<>();
        //chat_text_items.add(new Chat_Text_Item("hello" , "Haitham" , "Time"));

        btn_Send = findViewById(R.id.btn_groupSend2);
        ed_ChatBody = findViewById(R.id.ed_GroupChat2);
        ed_messageText = findViewById(R.id.ed_messageText2);
        lv_chat = findViewById(R.id.listView_Chat);
        chat_Img = findViewById(R.id.chat_Img);

        Picasso.get().load(imgUri).into(chat_Img);
        getSupportActionBar().setTitle(name);

        db_ref = FirebaseDatabase.getInstance().getReference().child("CHAT");
        db_root = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser().getUid();

        adaptor = new Chat_List_Adaptor(this, chat_text_items, currentUser);
        lv_chat.setAdapter(adaptor);
        //lv_chat.setSelection(chat_text_items.size() - 1);

        Diplay_Msgs();

        keyBoard_Listner();

    }

    public void send_message(View view) {

        final String msg_Text = ed_messageText.getText().toString();

        final HashMap<String, Object> map = new HashMap<>();

        map.put("MESSAGE_TEXT", msg_Text);
        map.put("MSG_TYP", "TEXT");
        map.put("SENDER", currentUser);
        map.put("TIME", getTime());
        map.put("MSG_COND", "SENT");

        db_ref.child(currentUser).child(ID).child(System.currentTimeMillis() + "")
                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    map.remove("MSG_COND");
                    map.put("MSG_COND", "RECEIVED");

                    db_ref.child(ID).child(currentUser).child(System.currentTimeMillis() + "")
                            .updateChildren(map);
                    ed_messageText.setText("");

                }

            }
        });

    }

    private String getTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");

        String time = timeformat.format(calendar.getTimeInMillis());
        return time;
    }

    private void retirve_Msgs(DataSnapshot dataSnapshot) {

        if (dataSnapshot.exists()) {

            Iterator iterator = dataSnapshot.getChildren().iterator();

            while (iterator.hasNext()) {

                final String msg_Text = ((DataSnapshot) iterator.next()).getValue().toString();
                String msg_Cond = ((DataSnapshot) iterator.next()).getValue().toString();
                String msg_Type = ((DataSnapshot) iterator.next()).getValue().toString();
                final String senderID = ((DataSnapshot) iterator.next()).getValue().toString();
                String msg_time = ((DataSnapshot) iterator.next()).getValue().toString();


                if (msg_Cond.equals("RECEIVED")) {


                    db_root.child("Users").child(senderID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                    String senderName = dataSnapshot1.child("NAME").getValue().toString();

                                    System.out.println("sender name is " + senderName);

                                    Intent in = new Intent(getApplicationContext(), myNotificationBroadcast.class);
                                    in.setAction("my.Custom.Action");
                                    in.addCategory(Intent.CATEGORY_DEFAULT);
                                    in.putExtra("MSG_TXT", msg_Text);
                                    in.putExtra("MSG_SENDER", senderName);
                                    sendBroadcast(in);
                                    System.out.println("receiver here");


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    dataSnapshot.child("MSG_COND").getRef().setValue("OPENED");
                }

                chat_text_items.add(new Chat_Text_Item(msg_Text, msg_time, senderID));
                adaptor.notifyDataSetChanged();
                System.out.println(msg_Cond);
                scrollDown();


            }

        }

    }

    private void scrollDown() {
        lv_chat.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_chat.smoothScrollToPosition(chat_text_items.size() - 1);
            }
        }, 200);
    }

    public void keyBoard_Listner() {

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                scrollDown();

            }
        });

    }

    private void Diplay_Msgs() {

        db_ref.child(currentUser).child(ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                retirve_Msgs(dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //retirve_Msgs(dataSnapshot);

                //dataSnapshot.child("MSG_COND").getRef().setValue("OPENED");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                retirve_Msgs(dataSnapshot);

                dataSnapshot.child("MSG_COND").getRef().setValue("OPENED");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
