package com.haitham.mchatapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfile_Activity extends AppCompatActivity {

    String FRIEND_USERID;
    String OwnUserID;
    String REQUEST_STATE;
    Button btn_sendMessage, btn_cancel_request;

    TextView tv_status, tv_userName;
    CircleImageView circleImageView;

    DatabaseReference db_ref;
    StorageReference store_ref;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db_ref = FirebaseDatabase.getInstance().getReference();
        store_ref = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        FRIEND_USERID = getIntent().getStringExtra("USERID");
        OwnUserID = auth.getCurrentUser().getUid();
        REQUEST_STATE = "new";

        btn_sendMessage = findViewById(R.id.btn_sendMessage);
        btn_cancel_request = findViewById(R.id.btn_cancel);
        tv_status = findViewById(R.id.tv_FrStatus);
        tv_userName = findViewById(R.id.tv_FrUsername);
        circleImageView = findViewById(R.id.circleImage_FrProfile);

        showDetails();

    }

    private void showDetails() {

        //Check request status is sent or no(sent or new) request...

        check_Request_Status();

        db_ref.child("Users").child(FRIEND_USERID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("IMAGE").getValue().toString();
                String name = dataSnapshot.child("NAME").getValue().toString();
                String status = dataSnapshot.child("STATUS").getValue().toString();

                System.out.println(name + " %% " + status);

                tv_status.setText(status);
                tv_userName.setText(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        store_ref.child("PROFILE_IMAGES").child(FRIEND_USERID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(circleImageView);

            }
        });

    }


    private void check_Request_Status() {

        //check if frined profile is same the owner profile
        // will cancel chat request button.

        if (FRIEND_USERID.equals(OwnUserID)) {

            btn_sendMessage.setVisibility(View.INVISIBLE);

        }

        //retrive request chat from sender and receiver and check it is sent or no.

        db_ref.child("CHAT_REQUEST").child(OwnUserID).child(FRIEND_USERID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrive request type from sender to receiver and check if it sent or no

                if (dataSnapshot.child("REQUEST_TYPE").exists()) {

                    String REQUEST_TYEP = dataSnapshot.child("REQUEST_TYPE").getValue().toString();

                    System.out.println("Request typeis" + REQUEST_TYEP);

                    if (REQUEST_TYEP.equals("SENT")) {

                        // request type status is sent will change send button text & request state,

                        btn_sendMessage.setText("Cancel chat request");
                        REQUEST_STATE = "SENT";

                    } else if (REQUEST_TYEP.equals("RECEIVED")) {

                        REQUEST_STATE = "RECEIVED";
                        btn_sendMessage.setText("Accept request");
                        btn_cancel_request.setVisibility(View.VISIBLE);

                    }
                } else {

                    db_ref.child("CONTACT").child(OwnUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(FRIEND_USERID)) {

                                btn_sendMessage.setText("remove friend");
                                btn_cancel_request.setVisibility(View.INVISIBLE);
                                REQUEST_STATE = "FRIENDS";

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void cancel_Request() {

        //will do this if you want to cancel the chat request,
        // will delete the request from both side(sender & receiver).

        db_ref.child("CHAT_REQUEST").child(OwnUserID).child(FRIEND_USERID).removeValue().
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            db_ref.child("CHAT_REQUEST").child(FRIEND_USERID).child(OwnUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {


                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void sendChat_Request() {

        db_ref.child("CHAT_REQUEST").child(OwnUserID).child(FRIEND_USERID)
                .child("REQUEST_TYPE").setValue("SENT")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            //if chat request has been sent successfully from sender side(change request type to sent)

                            db_ref.child("CHAT_REQUEST").child(FRIEND_USERID).child(OwnUserID)
                                    .child("REQUEST_TYPE").setValue("RECEIVED")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            //change REQUEST_TYPE for receiver to received.

                                            if (task.isSuccessful()) {

                                                Toast.makeText(FriendProfile_Activity.this, "Chat request sent", Toast.LENGTH_SHORT).show();

                                                btn_sendMessage.setText("Cancel chat request");
                                            } else {

                                                // if change request type for receiver failed

                                            }
                                        }
                                    });

                        } else {

                            // if chat request sent failed (change request type for sender failed)

                        }

                    }
                });

    }


    public void select_Btn_Action(View view) {

        if (REQUEST_STATE.equals("new")) {

            sendChat_Request();

        } else if (REQUEST_STATE.equals("SENT")) {

            cancel_Request();

            Toast.makeText(FriendProfile_Activity.this,
                    "Chat request cancelled", Toast.LENGTH_SHORT).show();

            btn_sendMessage.setText("Add Friend");

            REQUEST_STATE = "new";

        } else if (REQUEST_STATE.equals("RECEIVED")) {

            accept_Request();

        } else if (REQUEST_STATE.equals("FRIENDS")) {

            remove_Friend();
        }
    }


    private void remove_Friend() {

        //remove friend from contact list from both side owner user and friend user

        db_ref.child("CONTACT").child(OwnUserID).child(FRIEND_USERID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    db_ref.child("CONTACT").child(FRIEND_USERID).child(OwnUserID)
                            .removeValue();
                    btn_sendMessage.setText("Add Friend");
                    REQUEST_STATE = "new";

                }
            }
        });
    }


    public void cancel_Btn_Action(View view) {

        cancel_Request();
        btn_cancel_request.setVisibility(View.INVISIBLE);
        btn_sendMessage.setText("Add friend");

    }


    public void accept_Request() {

        db_ref.child("CONTACT").child(OwnUserID)
                .child(FRIEND_USERID).child("CONTATCT").setValue("SAVED")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            db_ref.child("CONTACT").child(FRIEND_USERID)
                                    .child(OwnUserID).child("CONTACT").setValue("SAVED")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            cancel_Request();

                                            REQUEST_STATE = "FRIENDS";
                                            btn_sendMessage.setText("remove friend");
                                            btn_cancel_request.setVisibility(View.INVISIBLE);

                                        }
                                    });
                        }
                    }
                });
    }
}
