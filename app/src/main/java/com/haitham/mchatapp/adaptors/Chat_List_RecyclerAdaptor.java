package com.haitham.mchatapp.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.haitham.mchatapp.R;
import com.haitham.mchatapp.models.Chat_Text_Item;

import java.util.ArrayList;

public class Chat_List_RecyclerAdaptor extends RecyclerView.Adapter<Chat_List_RecyclerAdaptor.Chat_List_ViewHolder> {


    Context context;
    ArrayList<Chat_Text_Item> Chat_List;

    public Chat_List_RecyclerAdaptor(Context context, ArrayList<Chat_Text_Item> chat_List) {
        this.context = context;
        Chat_List = chat_List;
    }

    @NonNull
    @Override
    public Chat_List_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chatlist_item_layout, parent, false);
        Chat_List_ViewHolder holder = new Chat_List_ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull Chat_List_ViewHolder holder, int position) {

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.tv_chatText.setText(Chat_List.get(position).getText());
        holder.tv_time.setText(Chat_List.get(position).getTime());
        if (Chat_List.get(position).getUsername().equals(currentUser)) {

            holder.linearLayout.setBackgroundResource(R.drawable.reciever);

            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            holder.linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        }
    }

    @Override
    public int getItemCount() {
        return Chat_List.size();
    }


    public class Chat_List_ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_chatText;
        LinearLayout linearLayout;
        TextView tv_time;

        public Chat_List_ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_chatText = itemView.findViewById(R.id.tv_msgText);
            tv_time = itemView.findViewById(R.id.tv_time);
            linearLayout = itemView.findViewById(R.id.linear_chat2);
        }
    }
}
