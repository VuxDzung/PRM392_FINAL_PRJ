package com.example.prm392_final_prj.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.repository.TourRepository;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<BookingOrderEntity> bookings;
    private Context context;
    private List<TourEntity> tours;

    public BookingAdapter(List<BookingOrderEntity> bookings, List<TourEntity> tours, Context context) {
        this.bookings = bookings;
        this.context = context;
        this.tours = tours;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookingOrderEntity booking = bookings.get(position);
        TourEntity tour = tours.stream()
                .filter(t -> t.id == booking.tourId)
                .findFirst()
                .orElse(null);
        holder.tourName.setText(tour.location + " " + tour.duration);
        holder.tourDate.setText("Start Time: " + tour.duration);
        holder.tourPrice.setText("Price: " + tour.price);
        String status = getStatusString(booking.status);
        holder.tourStatus.setText("Status: " + status);
    }

    private String getStatusString(int status){
        switch (status) {
            case 0:
                return "Pending";
            case 1:
                return "Confirmed";
            case 2:
                return "Cancelled";
            case 3:
                return "Completed";
            default:
                return "Unknown";
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tourName, tourDate, tourPrice, tourStatus;
        ImageView tourImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tourName = itemView.findViewById(R.id.tour_name);
            tourDate = itemView.findViewById(R.id.tour_date);
            tourPrice = itemView.findViewById(R.id.tour_price);
            tourStatus = itemView.findViewById(R.id.tour_status);
            tourImage = itemView.findViewById(R.id.tour_image);
        }
    }
}
