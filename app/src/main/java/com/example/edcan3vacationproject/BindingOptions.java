package com.example.edcan3vacationproject;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class BindingOptions {

    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @BindingAdapter("overFlowIcon")
    public static void bindOverflowIcon(Toolbar toolbar, Drawable drawable){
        toolbar.setOverflowIcon(drawable);
    }
    @BindingAdapter("memoItem")
    public static void bindMemoItem(RecyclerView recyclerView, ObservableArrayList<Message> items) {
        ChatAdapter adapter = (ChatAdapter)recyclerVi   ew.getAdapter();
        if(adapter!=null) adapter.setItem(items);
    }

}
