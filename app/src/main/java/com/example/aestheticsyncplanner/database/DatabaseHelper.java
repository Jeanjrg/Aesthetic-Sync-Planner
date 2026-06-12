package com.example.aestheticsyncplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.aestheticsyncplanner.model.Event;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "aesthetic_sync.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_COMPLETED = "completed";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_START_DATE + " TEXT, " +
                    COLUMN_END_DATE + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_COMPLETED + " INTEGER DEFAULT 0);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, event.getId());
        values.put(COLUMN_TITLE, event.getTitle());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_START_DATE, event.getStartDate());
        values.put(COLUMN_END_DATE, event.getEndDate());
        values.put(COLUMN_CATEGORY, event.getCategory());
        values.put(COLUMN_COMPLETED, event.isCompleted() ? 1 : 0);
        db.insertWithOnConflict(TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, event.getTitle());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_START_DATE, event.getStartDate());
        values.put(COLUMN_END_DATE, event.getEndDate());
        values.put(COLUMN_CATEGORY, event.getCategory());
        values.put(COLUMN_COMPLETED, event.isCompleted() ? 1 : 0);
        db.update(TABLE_EVENTS, values, COLUMN_ID + " = ?", new String[]{event.getId()});
        db.close();
    }

    public void deleteEvent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " ORDER BY " + COLUMN_START_DATE + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6) == 1
                );
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventList;
    }

    public void deleteOldEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, "date(substr(" + COLUMN_START_DATE + ", 1, 10)) < date('now', '-30 days')", null);
        db.close();
    }
}
