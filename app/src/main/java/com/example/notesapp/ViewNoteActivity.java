package com.example.notesapp;

import com.example.notesapp.databinding.ActivityViewnoteBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewNoteActivity extends AppCompatActivity {
    // binding
    private ActivityViewnoteBinding binding;

    // text view
    TextView titleView;
    TextView innerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewnoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleView = binding.viewnoteTitle;
        innerView = binding.viewnoteInner;
        String titleString = getIntent().getStringExtra("KEY_TITLE");
        String innerString = getIntent().getStringExtra("KEY_INNER");
        titleView.setText(titleString);
        innerView.setText(innerString);
    }
}