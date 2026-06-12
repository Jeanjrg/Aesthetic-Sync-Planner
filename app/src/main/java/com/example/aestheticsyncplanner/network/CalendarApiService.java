package com.example.aestheticsyncplanner.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CalendarApiService {
    @GET("calendars/{calendarId}/events")
    Call<EventsResponse> getEvents(
            @Path(value = "calendarId", encoded = true) String calendarId,
            @Query("key") String apiKey
    );
}
