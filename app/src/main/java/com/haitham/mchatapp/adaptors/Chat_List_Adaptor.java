package com.haitham.mchatapp.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haitham.mchatapp.R;
import com.haitham.mchatapp.models.Chat_Text_Item;

import java.util.ArrayList;

public class Chat_List_Adaptor extends ArrayAdapter<Chat_Text_Item> {

    String currentUser;

    public Chat_List_Adaptor(@NonNull Context context, ArrayList<Chat_Text_Item> arrayList, String currentUSer) {

        super(context, R.layout.chatlist_item_layout, arrayList);
        this.currentUser = currentUSer;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (currentUser.equals(getItem(position).getUsername())) {

            View view2 = inflater.inflate(R.layout.chatlist_item_layout_friend, parent, false);

            TextView tv_text = view2.findViewById(R.id.tv_msgText);
            tv_text.setText(getItem(position).text);

            TextView tv_time = view2.findViewById(R.id.tv_time);
            tv_time.setText(getItem(position).getTime());

            return view2;
        } else {

            View view = inflater.inflate(R.layout.chatlist_item_layout, parent, false);

            TextView tv_text = view.findViewById(R.id.tv_msgText);
            tv_text.setText(getItem(position).text);

            TextView tv_time = view.findViewById(R.id.tv_time);
            tv_time.setText(getItem(position).getTime());

            return view;
        }
    }
}
