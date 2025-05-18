package com.example.notesapp;

import com.example.notesapp.databinding.ActivityMainBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    // binding
    private ActivityMainBinding binding;

    // keys
    private final String notesKey = "NOTES_KEY";

    // create basic array list and load notes from prefs
    private final ArrayList<CardData> data = new ArrayList<>();
    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // if any data is saved, load it from prefs
        CardData[] savedData = loadNotes(notesKey);

        if (savedData != null) {
            Collections.addAll(data, savedData);
        }

        initRecyclerView();
        initMainButtons();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.mainRecyclerView;

        // for optimization purposes
        recyclerView.setHasFixedSize(true);

        // doing this with linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CardAdapter(data, this);
        recyclerView.setAdapter(adapter);
    }

    private void initMainButtons() {
        FloatingActionButton addNote = binding.mainFobAdd;
        FloatingActionButton removeAll = binding.mainFobRemoveAll;

        addNote.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogview = inflater.inflate(R.layout.dialog_addnote, null);

            // create new alert dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                    .setView(dialogview)
                    .setTitle(R.string.activityMain_add)
                    .setPositiveButton(R.string.create, (dialog, which) -> {
                        EditText title = dialogview.findViewById(R.id.dialog_noteTitle);
                        EditText inner = dialogview.findViewById(R.id.dialog_noteInner);

                        addNote(title.getText().toString(), inner.getText().toString());
                    })
                    .setNegativeButton(R.string.cancel, null);

            // now show it
            AlertDialog alertDialogShower = alertDialogBuilder.create();
            alertDialogShower.show();
        });

        removeAll.setOnClickListener(v -> {
            // create new alert dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                    .setTitle(R.string.activityMain_deleteAll)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        clearAllNotes();
                    })
                    .setNegativeButton(R.string.cancel, null);

            // now show it
            AlertDialog alertDialogShower = alertDialogBuilder.create();
            alertDialogShower.show();
        });
    }

    private void saveNotes(ArrayList<CardData> data, String key) {
        Gson gson = new Gson();

        // get app prefs
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.notesapp.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();

        // convert data to json string
        String dataJson = gson.toJson(data);

        // now put string into prefs and apply
        prefsEditor.putString(key, dataJson);
        prefsEditor.apply();
    }

    private CardData[] loadNotes(String key) {
        Gson gson = new Gson();

        // get app prefs
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.notesapp.prefs", Context.MODE_PRIVATE);

        //Retrieve the values
        String prefText = sharedPref.getString(key, null);

        return gson.fromJson(prefText, CardData[].class);
    }

    // this clears all notes, so no need for lint to yell at me for using dataSetChanged :p
    @SuppressLint("NotifyDataSetChanged")
    private void clearAllNotes() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.notesapp.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        data.clear();
        prefsEditor.clear();
        prefsEditor.apply();
        adapter.notifyDataSetChanged();
    }

    public void addNote(String title, String inner) {
        CardData note = new CardData(inner, title);
        data.add(note);
        saveNotes(data, notesKey);
        adapter.notifyItemInserted(adapter.getItemCount());
    }

    public void editNoteAt(int position) {
        CardData card = data.get(position);

        LayoutInflater inflater = getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.dialog_addnote, null);
        EditText title = dialogview.findViewById(R.id.dialog_noteTitle);
        EditText inner = dialogview.findViewById(R.id.dialog_noteInner);
        title.setText(card.getCardViewTitle());
        inner.setText(card.getCardViewInner());

        // create new alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogview)
                .setTitle(R.string.listItem_EditNote)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    card.setCardViewTitle(title.getText().toString());
                    card.setCardViewInner(inner.getText().toString());

                    data.set(position, card);

                    saveNotes(data, notesKey);
                    adapter.notifyItemChanged(position);
                })
                .setNegativeButton(R.string.cancel, null);

        // now show it
        AlertDialog alertDialogShower = alertDialogBuilder.create();
        alertDialogShower.show();
    }

    public void deleteNoteAt(int position) {
        data.remove(position);
        saveNotes(data, notesKey);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getMenuPosition();

        if (item.getItemId() == R.id.menu_editNote) {
            editNoteAt(position);
            return true;
        } else if (item.getItemId() == R.id.menu_deleteNote) {
            // create new alert dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                    .setTitle(R.string.listItem_DeleteNote)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        deleteNoteAt(position);
                    })
                    .setNegativeButton(R.string.cancel, null);

            // now show it
            AlertDialog alertDialogShower = alertDialogBuilder.create();
            alertDialogShower.show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}