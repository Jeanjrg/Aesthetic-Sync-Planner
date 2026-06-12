package com.example.aestheticsyncplanner.network;

import com.example.aestheticsyncplanner.model.Event;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventsResponse {
    @SerializedName("items")
    private List<Event> items;

    public List<Event> getItems() {
        return items;
    }
}
