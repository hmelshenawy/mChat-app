package com.haitham.mchatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.haitham.mchatapp.models.Friends;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    DatabaseReference firebaseDatabase_ref;

    String currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase_ref = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions
                .Builder<Friends>().setQuery(firebaseDatabase_ref, Friends.class)
                .build();


        final FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder
                            , final int position, @NonNull Friends model) {

                        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("PROFILE_IMAGES")
                                .child(model.getUSERID() + ".jpg");


                        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Picasso.get().load(uri).placeholder(R.drawable.common_full_open_on_phone)
                                        .into(holder.iv_FriendImg);

                                System.out.println("uir last " + uri);

                            }
                        });

                        holder.tv_FriendName.setText(model.getNAME());
                        holder.tv_FriendStatus.setText(model.getSTATUS());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String itemId = getRef(position).getKey();

                                Intent in = new Intent(FindFriendActivity.this, FriendProfile_Activity.class);
                                in.putExtra("USERID", itemId);
                                startActivity(in);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                        View view = inflater.inflate(R.layout.friends_items_layout, parent, false);
                        View view2 = inflater.inflate(R.layout.friends_items_layout2, parent, false);
                        FriendsViewHolder holder;


                        holder = new FriendsViewHolder(view);


                        return holder;
                    }


                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    class FriendsViewHolder extends RecyclerView.ViewHolder {


        CircleImageView iv_FriendImg;
        TextView tv_FriendName, tv_FriendStatus;
        RelativeLayout relativeLayout;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_FriendName = itemView.findViewById(R.id.tv_FriendName);
            tv_FriendStatus = itemView.findViewById(R.id.tv_FrinedStatus);
            iv_FriendImg = itemView.findViewById(R.id.iv_findFriend_img);
            relativeLayout = itemView.findViewById(R.id.relative);
        }
    }
}
