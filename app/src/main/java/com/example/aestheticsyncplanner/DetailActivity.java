package com.example.aestheticsyncplanner;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aestheticsyncplanner.model.Event;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);

        Event event = (Event) getIntent().getSerializableExtra("event");
        if (event != null) {
            tvTitle.setText(event.getTitle());
            tvDate.setText(event.getStartDate());
            tvDescription.setText(event.getDescription());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
