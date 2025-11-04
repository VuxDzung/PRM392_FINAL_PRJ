package com.example.prm392_final_prj.adapter;// package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_final_prj.activities.HomeActivity;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.TourEntity;
import java.util.List;
import java.util.Locale;
public class TourListAdapter
        extends RecyclerView.Adapter<TourListAdapter.TourViewHolder> {

    private List<TourEntity> tourList;
    private final Context context;

    public TourListAdapter(List<TourEntity> tourList, Context context) {
        this.tourList = tourList;
        this.context = context;
    }

    public void setTourList(List<TourEntity> tourList){
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tour_list_item, parent, false);
        return new TourViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        TourEntity tour = tourList.get(position);
        holder.tourLocation.setText(tour.getLocation());
        holder.tourDuration.setText(tour.getDuration());
        holder.tourPrice.setText(String.format(Locale.US, "$%.2f", tour.getPrice()));

        String routeType = tour.isAirway() ? "round-trip" : "one-way";
        String route = String.format("%s -> %s %s",
                tour.getDeparture(), tour.getDestination(), routeType);
        holder.tourRoute.setText(route);

        if (tour.getImage() != null) {
            holder.tourImage.setImageBitmap(BitmapFactory.decodeByteArray(
                    tour.getImage(), 0, tour.getImage().length));
        } else {
            holder.tourImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnBooking.setOnClickListener(v -> {
            ((HomeActivity) context).onBookingClick(tour);
        });

        holder.btnDetail.setOnClickListener(v -> {
            ((HomeActivity) context).onSeeMoreClick(tour);
        });
    }

    @Override
    public int getItemCount() {
        return tourList != null ? tourList.size() : 0;
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        private final TextView tourLocation;
        private final TextView tourDuration;
        private final TextView tourPrice;
        private final TextView tourRoute;
        private final ImageView tourImage;
        private final Button btnBooking;
        private final Button btnDetail;
        private final TourListAdapter adapter;

        public TourViewHolder(@NonNull View itemView, TourListAdapter adapter){
            super(itemView);
            this.adapter = adapter;

            tourLocation = itemView.findViewById(R.id.tour_location);
            tourDuration = itemView.findViewById(R.id.tour_duration);
            tourPrice = itemView.findViewById(R.id.tour_price);
            tourRoute = itemView.findViewById(R.id.tour_route);
            tourImage = itemView.findViewById(R.id.tour_image);
            btnBooking = itemView.findViewById(R.id.btn_booking);
            btnDetail = itemView.findViewById(R.id.btn_detail);

        }
    }
}