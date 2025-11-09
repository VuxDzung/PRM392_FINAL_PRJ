package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.TourBookingAdmin.BookingListInTourActivity;
import com.example.prm392_final_prj.entity.TourEntity;

import java.util.List;

public class TourBookingListAdapter extends RecyclerView.Adapter<TourBookingListAdapter.TourViewHolder> {

    private final Context context;
    private final List<TourEntity> tourList;


    public TourBookingListAdapter(Context context, List<TourEntity> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_booking, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        holder.bind(tourList.get(position));
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    class TourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tourTitle, tourDate, tourBookingCount;

        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            tourTitle = itemView.findViewById(R.id.tour_title);
            tourDate = itemView.findViewById(R.id.tour_date);
            tourBookingCount = itemView.findViewById(R.id.tour_booking_count);
            itemView.setOnClickListener(this);
        }

        void bind(TourEntity tour) {
            tourTitle.setText(tour.location + " " + tour.duration);
            tourDate.setText("Duration: " + tour.duration);
            tourBookingCount.setText("Booked: " + 1);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            TourEntity tour = tourList.get(position);

            // Directly open next screen
            Intent intent = new Intent(context, BookingListInTourActivity.class);
            intent.putExtra("tourId", tour.id);
            intent.putExtra("tourName", tour.location);
            intent.putExtra("tourDate", tour.duration);
            context.startActivity(intent);
        }
    }
}
