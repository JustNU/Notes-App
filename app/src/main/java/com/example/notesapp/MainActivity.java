package com.example.notesapp;

import com.example.notesapp.databinding.ActivityMainBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // mainBinding
    private ActivityMainBinding mainBinding;

    // keys
    private final String notesKey = "NOTES_KEY";

    // create basic array list and load notes from prefs
    private final ArrayList<CardData> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.main, (v, insets) -> {
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
        RecyclerView recyclerView = mainBinding.mainRecyclerView;

        // for optimization purposes
        recyclerView.setHasFixedSize(true);

        // doing this with linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        CardAdapter adapter = new CardAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    private void initMainButtons() {
        FloatingActionButton addNote = mainBinding.mainFobAdd;
        FloatingActionButton removeAll = mainBinding.mainFobRemoveAll;

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

        removeAll.setOnClickListener(v -> clearAllNotes());
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
        Objects.requireNonNull(mainBinding.mainRecyclerView.getAdapter()).notifyDataSetChanged();
    }

    public void addNote(String title, String inner) {
        CardData note = new CardData(inner, title);
        data.add(note);
        saveNotes(data, notesKey);
        Objects.requireNonNull(mainBinding.mainRecyclerView.getAdapter()).notifyItemInserted(mainBinding.mainRecyclerView.getAdapter().getItemCount());
    }
}