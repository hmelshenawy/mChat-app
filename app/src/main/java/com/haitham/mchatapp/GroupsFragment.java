package com.haitham.mchatapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {


    View groupFragmentView;
    ListView lv_Groups;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> groups_List = new ArrayList<>();

    DatabaseReference db_Groups_ref;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //groups_List.add("frineds");

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        retrive_Groups();


        lv_Groups = groupFragmentView.findViewById(R.id.ListView_Groups);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, groups_List);
        lv_Groups.setAdapter(arrayAdapter);

        groupItem_onClick();


        return groupFragmentView;
    }

    public void retrive_Groups() {

        db_Groups_ref = FirebaseDatabase.getInstance().getReference().child("GROUPS");

        db_Groups_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.getChildren().iterator();
                Set<String> set = new HashSet<>();

                groups_List.clear();

                while (iterator.hasNext()) {

                    groups_List.add(((DataSnapshot) iterator.next()).getKey());

                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void groupItem_onClick() {

        lv_Groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String groupName = adapterView.getItemAtPosition(i).toString();

                Intent in = new Intent(getContext(), GroupChatActivity.class);
                in.putExtra("GROUP_NAME", groupName);
                startActivity(in);

            }
        });

    }


}
