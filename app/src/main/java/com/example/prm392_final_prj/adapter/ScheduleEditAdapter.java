package com.example.prm392_final_prj.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleEditAdapter extends RecyclerView.Adapter<ScheduleEditAdapter.ScheduleEditViewHolder> {

    private final List<TourScheduleEntity> mSchedules;
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private final OnMapPickListener mapPickListener;

    public interface OnMapPickListener {
        void onPickLocationClick(int position, TourScheduleEntity schedule);
    }

    interface OnTextChanged {
        void onChanged(String s);
    }

    public ScheduleEditAdapter(List<TourScheduleEntity> schedules, OnMapPickListener listener) {
        this.mSchedules = new ArrayList<>(schedules);
        this.mapPickListener = listener;
    }

    @NonNull
    @Override
    public ScheduleEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_edit, parent, false);
        return new ScheduleEditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleEditViewHolder holder, int position) {
        holder.bind(mSchedules.get(position));
    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mSchedules.size()) {
            mSchedules.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mSchedules.size());
        }
    }

    public void addItem(TourScheduleEntity schedule) {
        mSchedules.add(schedule);
        notifyItemInserted(mSchedules.size() - 1);
    }

    public void updateScheduleCoordinates(int position, double latitude, double longitude) {
        if (position >= 0 && position < mSchedules.size()) {
            TourScheduleEntity schedule = mSchedules.get(position);
            schedule.setLatitude(latitude);
            schedule.setLongitude(longitude);
            notifyItemChanged(position);
        }
    }

    public List<TourScheduleEntity> getSchedules() {
        return mSchedules;
    }

    public void setSchedules(List<TourScheduleEntity> schedules) {
        this.mSchedules.clear();
        this.mSchedules.addAll(schedules);
        notifyDataSetChanged();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ScheduleEditViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        int pos = holder.getBindingAdapterPosition();
        if (pos != RecyclerView.NO_POSITION && pos < mSchedules.size()) {
            TourScheduleEntity s = mSchedules.get(pos);
            s.setPlace(holder.etPlace.getText().toString().trim());
            s.setDescription(holder.etDescription.getText().toString().trim());
            String timeText = holder.etTime.getText().toString().trim();
            try {
                if (!timeText.isEmpty()) {
                    s.setDepartTime(dateTimeFormat.parse(timeText));
                }
            } catch (Exception ignored) {}
        }
    }

    class ScheduleEditViewHolder extends RecyclerView.ViewHolder {

        private final EditText etPlace;
        private final EditText etDescription;
        private final EditText etTime;
        private final TextView tvCoordinates;
        private final MaterialButton btnPickLocation;
        private final ImageButton btnDelete;

        private final Calendar mCalendar = Calendar.getInstance();
        private TextWatcher placeWatcher;
        private TextWatcher descWatcher;

        public ScheduleEditViewHolder(@NonNull View itemView) {
            super(itemView);
            etPlace = itemView.findViewById(R.id.etSchedulePlace);
            etDescription = itemView.findViewById(R.id.etScheduleDescription);
            etTime = itemView.findViewById(R.id.etScheduleTime);
            tvCoordinates = itemView.findViewById(R.id.tvScheduleCoordinates);
            btnPickLocation = itemView.findViewById(R.id.btnPickScheduleLocation);
            btnDelete = itemView.findViewById(R.id.btnDeleteScheduleItem);

            btnDelete.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position);
                }
            });
        }

        public void bind(TourScheduleEntity schedule) {
            if (placeWatcher != null) etPlace.removeTextChangedListener(placeWatcher);
            if (descWatcher != null) etDescription.removeTextChangedListener(descWatcher);
            etTime.setOnClickListener(null);

            etPlace.setText(schedule.getPlace());
            etDescription.setText(schedule.getDescription());

            if (schedule.getDepartTime() != null) {
                etTime.setText(dateTimeFormat.format(schedule.getDepartTime()));
                mCalendar.setTime(schedule.getDepartTime());
            } else {
                etTime.setText("");
                mCalendar.setTime(new Date());
            }

            updateCoordinatesDisplay(schedule);

            placeWatcher = createWatcher(text -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    mSchedules.get(pos).setPlace(text);
                }
            });

            descWatcher = createWatcher(text -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    mSchedules.get(pos).setDescription(text);
                }
            });

            etPlace.addTextChangedListener(placeWatcher);
            etDescription.addTextChangedListener(descWatcher);

            etTime.setOnClickListener(v -> showDateTimePicker(schedule));

            btnPickLocation.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && mapPickListener != null) {
                    mapPickListener.onPickLocationClick(pos, mSchedules.get(pos));
                }
            });
        }

        private void showDateTimePicker(TourScheduleEntity schedule) {
            Context context = itemView.getContext();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                context,
                                (timeView, hourOfDay, minute) -> {
                                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    mCalendar.set(Calendar.MINUTE, minute);

                                    Date selectedDateTime = mCalendar.getTime();
                                    schedule.setDepartTime(selectedDateTime);
                                    etTime.setText(dateTimeFormat.format(selectedDateTime));
                                },
                                mCalendar.get(Calendar.HOUR_OF_DAY),
                                mCalendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        }

        private void updateCoordinatesDisplay(TourScheduleEntity schedule) {
            if (schedule.hasValidCoordinates()) {
                tvCoordinates.setText(String.format(Locale.US, "📍 %.6f, %.6f",
                        schedule.getLatitude(), schedule.getLongitude()));
            } else {
                tvCoordinates.setText("No coordinates selected");
            }
            tvCoordinates.setVisibility(View.VISIBLE);
        }

        private TextWatcher createWatcher(OnTextChanged listener) {
            return new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onChanged(s.toString());
                    }
                }
            };
        }
    }
}
