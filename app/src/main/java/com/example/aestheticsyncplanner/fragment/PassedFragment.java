package com.example.aestheticsyncplanner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aestheticsyncplanner.R;
import com.example.aestheticsyncplanner.adapter.EventAdapter;
import com.example.aestheticsyncplanner.database.DatabaseHelper;
import java.util.ArrayList;

public class PassedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passed, container, false);
        RecyclerView rvPassed = view.findViewById(R.id.rvPassed);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        EventAdapter adapter = new EventAdapter(new ArrayList<>(), new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.aestheticsyncplanner.model.Event event) {
                // Logic for passed event click if needed
            }

            @Override
            public void onCompleteChanged(com.example.aestheticsyncplanner.model.Event event, boolean isCompleted) {
                dbHelper.updateEvent(event);
            }
        });
        rvPassed.setAdapter(adapter);

        // For simplicity, just load all from DB and filter if needed
        adapter.setEvents(dbHelper.getAllEvents());

        return view;
    }
}
