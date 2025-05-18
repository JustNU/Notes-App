package com.example.notesapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>  {
    private final ArrayList<CardData> data;
    private Activity activity;

    private int menuPosition;

    public int getMenuPosition() {
        return menuPosition;
    }

    public CardAdapter(ArrayList<CardData> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView innerText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.listItem_titleText);
            innerText = itemView.findViewById(R.id.listItem_insideText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent secondScreen = new Intent(v.getContext(), ViewNoteActivity.class);
                    secondScreen.putExtra("KEY_TITLE", title.getText().toString());
                    secondScreen.putExtra("KEY_INNER", innerText.getText().toString());
                    activity.startActivity(secondScreen);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    menuPosition = getLayoutPosition();
                    itemView.showContextMenu(10,10);
                    return true;
                }
            });

            activity.registerForContextMenu(itemView);
        }

        public void setData(CardData cardData) {
            title.setText(cardData.getCardViewTitle());
            innerText.setText(cardData.getCardViewInner());
        }
    }
}
