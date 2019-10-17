package com.haitham.mchatapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haitham.mchatapp.adaptors.ContactList_Adaptor;
import com.haitham.mchatapp.models.Friends;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    RecyclerView recyclerView_ContactList;
    View contactView;

    ArrayList<Friends> friends_List;
    ContactList_Adaptor adaptor;

    DatabaseReference db_ref, db_main_ref;
    FirebaseAuth auth;

    String curr_UserID;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactView = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView_ContactList = contactView.findViewById(R.id.recyler_ContactList);
        recyclerView_ContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        friends_List = new ArrayList<>();

        adaptor = new ContactList_Adaptor(container.getContext(), friends_List);
        recyclerView_ContactList.setAdapter(adaptor);

        auth = FirebaseAuth.getInstance();
        curr_UserID = auth.getCurrentUser().getUid();
        db_ref = FirebaseDatabase.getInstance().getReference().child("CONTACT").child(curr_UserID);
        db_main_ref = FirebaseDatabase.getInstance().getReference().child("Users");

        call_ContactList();

        return contactView;
    }

    public void call_ContactList() {

        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.getChildren().iterator();

                friends_List.clear();

                while (iterator.hasNext()) {

                    final String contact_id = ((DataSnapshot) iterator.next()).getKey();

                    db_main_ref.child(contact_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String name = dataSnapshot.child("NAME").getValue().toString();
                            String status = dataSnapshot.child("STATUS").getValue().toString();

                            final Friends friends = new Friends(name, status, contact_id);

                            if (dataSnapshot.child("IMAGE").exists()) {

                                friends.setIMAGE(dataSnapshot.child("IMAGE").getValue().toString());
                            }

                            friends_List.add(friends);

                            adaptor.notifyDataSetChanged();

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
}
