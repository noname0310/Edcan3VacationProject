package com.example.edcan3vacationproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edcan3vacationproject.databinding.RowMessageReceiveBinding;
import com.example.edcan3vacationproject.databinding.RowMessageSendBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder>{

    private List<Message> list = new ArrayList<>();
    private String email;
    public ChatAdapter(String email){
        this.email = email;
    }
    void setItem(List<Message> list) {
        this.list = list;

    }
    @Override
    public int getItemViewType(int position) {
        if (list.get(position).ChatClient.UserEmail.equals(email)) return 0;
        else return 1;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new MessageHolder(RowMessageSendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        else
            return new MessageHolder(RowMessageReceiveBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message model = list.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MessageHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;


        public MessageHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        void bind(Message model) {
            if (binding instanceof RowMessageSendBinding) ((RowMessageSendBinding) binding).setMsg(model);
            else ((RowMessageReceiveBinding) binding).setMsg(model);
        }

    }
}
