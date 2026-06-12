package com.example.aestheticsyncplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aestheticsyncplanner.database.DatabaseHelper;
import com.example.aestheticsyncplanner.model.Event;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEditEventActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etCategory, etDate, etStartTime, etEndTime;
    private CheckBox cbAllDay;
    private LinearLayout layoutTimeRange;
    private AutoCompleteTextView autoCompleteReminder;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private Event currentEvent;
    private Calendar selectedDateTime = Calendar.getInstance();
    private Calendar endDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        etDate = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        cbAllDay = findViewById(R.id.cbAllDay);
        layoutTimeRange = findViewById(R.id.layoutTimeRange);
        autoCompleteReminder = findViewById(R.id.autoCompleteReminder);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        dbHelper = new DatabaseHelper(this);

        setupReminderDropdown();
        setupDateTimePickers();

        cbAllDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutTimeRange.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        currentEvent = (Event) getIntent().getSerializableExtra("event");
        if (currentEvent != null) {
            etTitle.setText(currentEvent.getTitle());
            etDescription.setText(currentEvent.getDescription());
            etCategory.setText(currentEvent.getCategory());
            btnDelete.setVisibility(View.VISIBLE);
            
            // Parse existing dates
            parseExistingEventDates();
        } else {
            // Default: 1 hour duration
            endDateTime.setTime(selectedDateTime.getTime());
            endDateTime.add(Calendar.HOUR_OF_DAY, 1);
            updateDateTimeFields();
        }

        btnSave.setOnClickListener(v -> saveEvent());
        btnDelete.setOnClickListener(v -> deleteEvent());
    }

    private void setupReminderDropdown() {
        String[] reminders = {"None", "5 Minutes", "10 Minutes", "30 Minutes", "1 Hour"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, reminders);
        autoCompleteReminder.setAdapter(adapter);
        autoCompleteReminder.setText(reminders[0], false);
    }

    private void setupDateTimePickers() {
        etDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(selectedDateTime.getTimeInMillis())
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(selection);
                selectedDateTime.set(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH));
                endDateTime.set(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH));
                updateDateTimeFields();
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        etStartTime.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(selectedDateTime.get(Calendar.HOUR_OF_DAY))
                    .setMinute(selectedDateTime.get(Calendar.MINUTE))
                    .setTitleText("Select Start Time")
                    .build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                selectedDateTime.set(Calendar.MINUTE, timePicker.getMinute());
                
                // If start time is after end time, adjust end time to be +1 hour
                if (selectedDateTime.after(endDateTime)) {
                    endDateTime.setTime(selectedDateTime.getTime());
                    endDateTime.add(Calendar.HOUR_OF_DAY, 1);
                }
                updateDateTimeFields();
            });
            timePicker.show(getSupportFragmentManager(), "START_TIME_PICKER");
        });

        etEndTime.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(endDateTime.get(Calendar.HOUR_OF_DAY))
                    .setMinute(endDateTime.get(Calendar.MINUTE))
                    .setTitleText("Select End Time")
                    .build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                Calendar tempEnd = (Calendar) endDateTime.clone();
                tempEnd.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                tempEnd.set(Calendar.MINUTE, timePicker.getMinute());

                if (tempEnd.before(selectedDateTime)) {
                    Toast.makeText(this, "End time cannot be before start time", Toast.LENGTH_SHORT).show();
                } else {
                    endDateTime = tempEnd;
                    updateDateTimeFields();
                }
            });
            timePicker.show(getSupportFragmentManager(), "END_TIME_PICKER");
        });
    }

    private void updateDateTimeFields() {
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        etDate.setText(dateFmt.format(selectedDateTime.getTime()));
        etStartTime.setText(timeFmt.format(selectedDateTime.getTime()));
        etEndTime.setText(timeFmt.format(endDateTime.getTime()));
    }

    private void parseExistingEventDates() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            
            String startDate = currentEvent.getStartDate();
            Date dateStart = sdf.parse(startDate);
            if (dateStart != null) {
                selectedDateTime.setTime(dateStart);
            }

            String endDate = currentEvent.getEndDate();
            Date dateEnd = sdf.parse(endDate);
            if (dateEnd != null) {
                endDateTime.setTime(dateEnd);
            }

            // Simple "All Day" detection if exactly 00:00 to 00:00 on same day (not perfect but works for this scope)
            if (startDate.endsWith("T00:00:00Z") && endDate.endsWith("T00:00:00Z")) {
                cbAllDay.setChecked(true);
                layoutTimeRange.setVisibility(View.GONE);
            }

            updateDateTimeFields();
        } catch (Exception ignored) {}
    }

    private void saveEvent() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String category = etCategory.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        String isoStart, isoEnd;

        if (cbAllDay.isChecked()) {
            // Set to start of day for both
            Calendar allDayCal = (Calendar) selectedDateTime.clone();
            allDayCal.set(Calendar.HOUR_OF_DAY, 0);
            allDayCal.set(Calendar.MINUTE, 0);
            allDayCal.set(Calendar.SECOND, 0);
            isoStart = isoFmt.format(allDayCal.getTime());
            isoEnd = isoStart; // Mark as all day
        } else {
            isoStart = isoFmt.format(selectedDateTime.getTime());
            isoEnd = isoFmt.format(endDateTime.getTime());
        }

        if (currentEvent == null) {
            currentEvent = new Event(
                    UUID.randomUUID().toString(),
                    title,
                    description,
                    isoStart,
                    isoEnd,
                    category,
                    false
            );
            dbHelper.insertEvent(currentEvent);
        } else {
            currentEvent.setTitle(title);
            currentEvent.setDescription(description);
            currentEvent.setCategory(category);
            currentEvent.setStartDate(isoStart);
            currentEvent.setEndDate(isoEnd);
            dbHelper.updateEvent(currentEvent);
        }

        scheduleNotification();
        finish();
    }

    private void scheduleNotification() {
        String reminderText = autoCompleteReminder.getText().toString();
        long reminderMillis = 0;
        switch (reminderText) {
            case "5 Minutes": reminderMillis = 5 * 60 * 1000; break;
            case "10 Minutes": reminderMillis = 10 * 60 * 1000; break;
            case "30 Minutes": reminderMillis = 30 * 60 * 1000; break;
            case "1 Hour": reminderMillis = 60 * 60 * 1000; break;
            default: return; // No reminder
        }

        long eventTime = selectedDateTime.getTimeInMillis();
        long triggerTime = eventTime - reminderMillis;

        if (triggerTime < System.currentTimeMillis()) {
            return; // Past time
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", "Reminder: " + currentEvent.getTitle());
        intent.putExtra("description", currentEvent.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                currentEvent.getId().hashCode(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    private void deleteEvent() {
        if (currentEvent != null) {
            dbHelper.deleteEvent(currentEvent.getId());
            finish();
        }
    }
}

