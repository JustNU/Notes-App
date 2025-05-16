package com.example.notesapp;

import com.example.notesapp.databinding.ActivityMainBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
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
    // binding
    private ActivityMainBinding binding;

    // keys
    private final String notesKey = "NOTES_KEY";

    // create basic array list and load notes from prefs
    private static ArrayList<CardData> data = new ArrayList<>();

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

        initRecyclerView(binding, data);
        initMainButtons(binding);
    }

    private void initRecyclerView(ActivityMainBinding binding, ArrayList<CardData> data) {
        RecyclerView recyclerView = binding.mainRecyclerView;

        // for optimization purposes
        recyclerView.setHasFixedSize(true);

        // doing this with linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // hook up adapter
        final CardAdapter adapter = new CardAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    private void initMainButtons(ActivityMainBinding binding) {
        FloatingActionButton addNote = binding.mainFobAdd;
        FloatingActionButton removeAll = binding.mainFobRemoveAll;

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote("test", "testy");
                Objects.requireNonNull(binding.mainRecyclerView.getAdapter()).notifyItemInserted(binding.mainRecyclerView.getAdapter().getItemCount());
            }
        });

        removeAll.setOnClickListener(new View.OnClickListener() {
            // this clears all notes, so no need for lint to yell at me for using dataSetChanged :p
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                clearAllNotes();
                Objects.requireNonNull(binding.mainRecyclerView.getAdapter()).notifyDataSetChanged();
            }
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

    private void clearAllNotes() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.notesapp.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        data.clear();
        prefsEditor.clear();
        prefsEditor.apply();
    }

    private void addNote(String title, String inner) {
        CardData note = new CardData(title, inner);
        data.add(note);
        saveNotes(data, notesKey);
    }
}