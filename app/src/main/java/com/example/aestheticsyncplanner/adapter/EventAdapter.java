package com.example.aestheticsyncplanner.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aestheticsyncplanner.R;
import com.example.aestheticsyncplanner.model.Event;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private OnItemClickListener listener;
    private boolean showCheckbox = true;

    public interface OnItemClickListener {
        void onItemClick(Event event);
        void onCompleteChanged(Event event, boolean isCompleted);
    }

    public EventAdapter(List<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    public void setShowCheckbox(boolean showCheckbox) {
        this.showCheckbox = showCheckbox;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvTitle.setText(event.getTitle());
        
        String timeDisplay;
        if (event.getStartDate().equals(event.getEndDate()) && event.getStartDate().endsWith("T00:00:00Z")) {
            timeDisplay = "All Day";
        } else {
            timeDisplay = formatTime(event.getStartDate()) + " - " + formatTime(event.getEndDate());
        }
        holder.tvTimeRange.setText(timeDisplay);

        holder.tvDescription.setText(event.getDescription());
        holder.cbCompleted.setChecked(event.isCompleted());

        // Hide checkbox if required (for PassedFragment)
        holder.cbCompleted.setVisibility(showCheckbox ? View.VISIBLE : View.GONE);

        // Initially hide description
        holder.tvDescription.setVisibility(View.GONE);

        // Category specific styling
        if ("Work".equalsIgnoreCase(event.getCategory())) {
            holder.viewCategoryIndicator.setBackgroundResource(R.color.primary);
            holder.tvCategoryTag.setBackgroundResource(R.drawable.bg_tag);
        } else {
            holder.viewCategoryIndicator.setBackgroundColor(Color.GRAY);
            // ... more categories
        }

        // Toggle description when clicking outside checkbox
        holder.itemView.setOnClickListener(v -> {
            if (holder.tvDescription.getVisibility() == View.VISIBLE) {
                holder.tvDescription.setVisibility(View.GONE);
            } else {
                holder.tvDescription.setVisibility(View.VISIBLE);
            }
            listener.onItemClick(event);
        });

        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(event.isCompleted());
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            event.setCompleted(isChecked);
            listener.onCompleteChanged(event, isChecked);
        });
    }

    private String formatTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "00:00";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return timeFmt.format(date);
        } catch (Exception e) {
            return "00:00";
        }
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTimeRange, tvDescription, tvCategoryTag;
        View viewCategoryIndicator;
        CheckBox cbCompleted;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategoryTag = itemView.findViewById(R.id.tvCategoryTag);
            viewCategoryIndicator = itemView.findViewById(R.id.viewCategoryIndicator);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
        }
    }
}
