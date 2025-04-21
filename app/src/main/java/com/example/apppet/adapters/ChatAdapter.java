package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messages;
    private int loggedUserId; // ID del usuario logueado

    public ChatAdapter(List<Message> messages, int loggedUserId) {
        this.messages = messages;
        this.loggedUserId = loggedUserId;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();  // MUY IMPORTANTE
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        return (msg.getSender_id() == loggedUserId) ? 1 : 0;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getMessage_text());
        holder.timestampText.setText(message.getTimestamp());

        // Nuevo: mostrar el nombre correcto
        if (holder.senderName != null && message.getSender_name() != null) {
            holder.senderName.setText(message.getSender_name());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText, senderName;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tvMessage);
            timestampText = itemView.findViewById(R.id.tvTimestamp);
            senderName = itemView.findViewById(R.id.tvSenderName); // ¡Esto es clave!
        }
    }
}
