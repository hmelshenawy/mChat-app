package com.haitham.mchatapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.haitham.mchatapp.adaptors.ContactList_Adaptor;
import com.haitham.mchatapp.models.Friends;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    RecyclerView lv_chatFragment;
    ContactList_Adaptor adaptor;
    ArrayList<Friends> friendsArrayList;
    DatabaseReference db_ref;
    DatabaseReference db_root_ref;
    StorageReference store_ref;
    String currentuser;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db_ref = FirebaseDatabase.getInstance().getReference().child("CHAT").child(currentuser);
        db_root_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        store_ref = FirebaseStorage.getInstance().getReference();

        friendsArrayList = new ArrayList<>();

        lv_chatFragment = view.findViewById(R.id.lv_chatFragment);
        adaptor = new ContactList_Adaptor(getContext(), friendsArrayList);
        lv_chatFragment.setLayoutManager(new LinearLayoutManager(getContext()));
        lv_chatFragment.setAdapter(adaptor);

        call_contacts2();

        return view;
    }


    private void call_contacts2() {

        db_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                friendsArrayList.clear();

                final String contactID = dataSnapshot.getKey();

                db_root_ref.child(contactID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String name = dataSnapshot.child("NAME").getValue().toString();
                        final String status = dataSnapshot.child("STATUS").getValue().toString();

                        Friends friends = new Friends(name, status, contactID);

                        if (dataSnapshot.child("IMAGE").exists()) {

                            friends.setIMAGE(dataSnapshot.child("IMAGE").getValue().toString());

                        }
                        friendsArrayList.add(friends);

                        adaptor.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
