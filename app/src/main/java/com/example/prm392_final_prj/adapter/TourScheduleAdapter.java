package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.example.prm392_final_prj.utils.ImageUtils;
import com.example.prm392_final_prj.utils.TimeConverter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TourScheduleAdapter extends RecyclerView.Adapter<TourScheduleAdapter.ScheduleViewHolder> {

    private final LayoutInflater mInflater;
    private List<TourScheduleEntity> mSchedules = Collections.emptyList(); // Cached copy
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public TourScheduleAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        TourScheduleEntity current = mSchedules.get(position);

        holder.locationName.setText(current.place);

        holder.locationAddress.setText(current.address);

        String timeDisplay = TimeConverter.getFormattedSchedule(current.departTime);
        holder.scheduleTime.setText(timeDisplay);

        if (current.image != null && current.image.length > 0) {
//            holder.locationPhoto.setImageBitmap(ImageUtils.getBitmapFromBytes(current.image));
        } else {
            holder.locationPhoto.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    public void setSchedules(List<TourScheduleEntity> schedules) {
        mSchedules = schedules;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final ImageView locationPhoto;
        private final TextView locationName;
        private final TextView locationAddress;
        private final TextView scheduleTime;

        private ScheduleViewHolder(View itemView) {
            super(itemView);
            locationPhoto = itemView.findViewById(R.id.schedule_location_photo);
            locationName = itemView.findViewById(R.id.schedule_location_name);
            locationAddress = itemView.findViewById(R.id.schedule_location_address);
            scheduleTime = itemView.findViewById(R.id.schedule_time);
        }
    }
}