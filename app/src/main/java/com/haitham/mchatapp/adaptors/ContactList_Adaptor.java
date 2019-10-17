package com.haitham.mchatapp.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haitham.mchatapp.ChatActivity;
import com.haitham.mchatapp.R;
import com.haitham.mchatapp.models.Friends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactList_Adaptor extends RecyclerView.Adapter<ContactList_Adaptor.ContactList_VeiwHolder> {

    Context context;
    ArrayList<Friends> Friends_List;


    public ContactList_Adaptor(Context context, ArrayList<Friends> Friends_List) {

        this.context = context;
        this.Friends_List = Friends_List;

    }

    @NonNull
    @Override
    public ContactList_VeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friends_items_layout, parent
                , false);

        ContactList_VeiwHolder veiwHolder = new ContactList_VeiwHolder(view);

        return veiwHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactList_VeiwHolder holder, final int position) {

        holder.tv_FriendName.setText(Friends_List.get(position).getNAME());
        holder.tv_FriendStatus.setText(Friends_List.get(position).getSTATUS());
        Picasso.get().load(Friends_List.get(position).getIMAGE()).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.iv_FriendImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = Friends_List.get(position).getNAME();
                String id = Friends_List.get(position).getUSERID();
                String imgUri = Friends_List.get(position).getIMAGE();

                Intent in = new Intent(context, ChatActivity.class);
                in.putExtra("NAME", name);
                in.putExtra("ID", id);
                in.putExtra("IMG_URI", imgUri);

                context.startActivity(in);
            }
        });

    }


    @Override
    public int getItemCount() {
        return Friends_List.size();
    }

    class ContactList_VeiwHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_FriendImg;
        TextView tv_FriendName, tv_FriendStatus;

        public ContactList_VeiwHolder(@NonNull View itemView) {
            super(itemView);

            tv_FriendName = itemView.findViewById(R.id.tv_FriendName);
            tv_FriendStatus = itemView.findViewById(R.id.tv_FrinedStatus);
            iv_FriendImg = itemView.findViewById(R.id.iv_findFriend_img);

        }
    }
}
