package com.example.edcan3vacationproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edcan3vacationproject.databinding.RowMessageReceiveBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter {

    private List<Message> list = new ArrayList<>();

    void setItem(List<Message> list) {
        this.list = list;

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        private RowMessageReceiveBinding binding;


        public MessageHolder(RowMessageReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        void bind(Message model) {
            binding.setMsg(model);
        }

    }
}
