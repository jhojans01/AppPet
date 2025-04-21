package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apppet.R;
import com.example.apppet.models.Conversation;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ConversationViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private List<Conversation> conversationList;
    private OnConversationClickListener listener;

    public ChatListAdapter(List<Conversation> conversationList, OnConversationClickListener listener) {
        this.conversationList = conversationList;
        this.listener = listener;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        Conversation conversation = conversationList.get(position);
        holder.tvOwnerName.setText(conversation.getOwnerName());
        holder.tvLastMessage.setText(conversation.getLastMessage());
        holder.tvTimestamp.setText(conversation.getTimestamp());
        holder.itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView tvOwnerName, tvLastMessage, tvTimestamp;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}

