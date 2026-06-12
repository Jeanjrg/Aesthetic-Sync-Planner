package com.example.aestheticsyncplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import android.widget.CompoundButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.CalendarView;
import android.widget.ImageButton;
import com.example.aestheticsyncplanner.AddEditEventActivity;
import com.example.aestheticsyncplanner.DetailActivity;
import com.example.aestheticsyncplanner.R;
import com.example.aestheticsyncplanner.adapter.DateAdapter;
import com.example.aestheticsyncplanner.adapter.EventAdapter;
import com.example.aestheticsyncplanner.database.DatabaseHelper;
import com.example.aestheticsyncplanner.model.Event;
import com.example.aestheticsyncplanner.network.EventsResponse;
import com.example.aestheticsyncplanner.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingFragment extends Fragment {

    private static final String TAG = "UpcomingFragment";
    private RecyclerView rvUpcoming;
    private CalendarView calendarView;
    private EventAdapter eventAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private DatabaseHelper dbHelper;
    private TextView tvCurrentMonth, tvCurrentDateFull;
    private ImageButton btnRefresh;
    private MaterialSwitch switchDarkMode;
    private Calendar currentCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        calendarView = view.findViewById(R.id.calendarView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        tvCurrentDateFull = view.findViewById(R.id.tvCurrentDateFull);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddEvent);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        dbHelper = new DatabaseHelper(getContext());

        setupCalendar();
        setupEventList();

        swipeRefresh.setOnRefreshListener(this::fetchEvents);
        btnRefresh.setOnClickListener(v -> fetchEvents());
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditEventActivity.class);
            startActivity(intent);
        });

        setupDarkModeSwitch();

        view.findViewById(R.id.btnToday).setOnClickListener(v -> {
            long today = System.currentTimeMillis();
            calendarView.setDate(today);
            currentCalendar.setTimeInMillis(today);
            updateHeader(new Date(today));
            filterEventsByDate(new Date(today));
        });

        updateHeader(new Date());
        loadLocalData();
        
        // Only fetch events if this is NOT an activity recreation (like theme change)
        if (savedInstanceState == null) {
            fetchEvents();
        }

        return view;
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentCalendar.set(year, month, dayOfMonth);
            Date selectedDate = currentCalendar.getTime();
            updateHeader(selectedDate);
            filterEventsByDate(selectedDate);
        });
    }

    private void filterEventsByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(date);
        List<Event> allEvents = dbHelper.getAllEvents();
        List<Event> filtered = new ArrayList<>();
        for (Event e : allEvents) {
            if (e.getStartDate() != null && e.getStartDate().startsWith(selectedDate)) {
                filtered.add(e);
            }
        }
        eventAdapter.setEvents(filtered);
    }

    private void setupEventList() {
        eventAdapter = new EventAdapter(new ArrayList<>(), new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent = new Intent(getActivity(), AddEditEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }

            @Override
            public void onCompleteChanged(Event event, boolean isCompleted) {
                dbHelper.updateEvent(event);
            }
        });
        rvUpcoming.setAdapter(eventAdapter);
    }

    private void updateHeader(Date date) {
        SimpleDateFormat monthFmt = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        SimpleDateFormat fullDateFmt = new SimpleDateFormat("'Hari ini', dd MMM", Locale.getDefault());
        tvCurrentMonth.setText(monthFmt.format(date));
        tvCurrentDateFull.setText(fullDateFmt.format(date));
    }

    private void setupDarkModeSwitch() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);
        
        switchDarkMode.setChecked(isDarkMode);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("is_dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            if (getActivity() != null) {
                getActivity().recreate();
            }
        });
    }

    private void loadLocalData() {
        Date selectedDate = currentCalendar.getTime();
        filterEventsByDate(selectedDate);
    }

    private void fetchEvents() {
        swipeRefresh.setRefreshing(true);

        String calendarId = "id.indonesian%23holiday@group.v.calendar.google.com";
        String apiKey = "AIzaSyDnr_H5Fj_OxmXafrvOB5_U5gtC1tQFlGo";

        RetrofitClient.getApiService().getEvents(calendarId, apiKey).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body().getItems();
                    Log.d(TAG, "API Response Successful. Items count: " + (events != null ? events.size() : 0));
                    
                    if (events != null && !events.isEmpty()) {
                        for (Event event : events) {
                            Log.d(TAG, "Inserting event: " + event.getTitle() + " (" + event.getStartDate() + ")");
                            dbHelper.insertEvent(event);
                        }
                        loadLocalData();
                        // Removed Toast to avoid lag and annoyance during theme change
                    } else {
                        Log.w(TAG, "API returned successful response but empty items list");
                        Toast.makeText(getContext(), "Tidak ada acara ditemukan di kalender", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) errorBody = response.errorBody().string();
                    } catch (Exception e) { e.printStackTrace(); }
                    
                    Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                    
                    String message = "Gagal memuat data: " + response.code();
                    if (response.code() == 403) {
                        message = "Error 403: Pastikan API Key benar dan Google Calendar API sudah diaktifkan di Google Cloud Console.";
                    } else if (response.code() == 404) {
                        message = "Error 404: Endpoint tidak ditemukan. Memperbaiki URL...";
                    }
                    
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.e(TAG, "Network Failure: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadLocalData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocalData();
    }
}
