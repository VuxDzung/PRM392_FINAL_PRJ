package com.example.prm392_final_prj.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.TourEntity;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminTourListAdapter extends RecyclerView.Adapter<AdminTourListAdapter.AdminTourViewHolder> {

    private List<TourEntity> mTours = new ArrayList<>();
    private final OnAdminTourItemClickListener mListener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface OnAdminTourItemClickListener {
        void onEditClick(TourEntity tour);
        void onDeleteClick(TourEntity tour);
    }

    public AdminTourListAdapter(OnAdminTourItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminTourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_tour, parent, false);
        return new AdminTourViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTourViewHolder holder, int position) {
        TourEntity currentTour = mTours.get(position);
        holder.bind(currentTour, mListener);
    }

    @Override
    public int getItemCount() {
        return mTours.size();
    }

    public void setTours(List<TourEntity> tours) {
        this.mTours = tours;
        notifyDataSetChanged();
    }

    class AdminTourViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivTourItemImage;
        private final TextView tvTourLocation;
        private final TextView tvTourPrice;
        private final TextView tvTourDuration;
        private final MaterialButton btnDelete;
        private final MaterialButton btnEdit;

        public AdminTourViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTourItemImage = itemView.findViewById(R.id.ivTourItemImage);
            tvTourLocation = itemView.findViewById(R.id.tvTourLocation);
            tvTourPrice = itemView.findViewById(R.id.tvTourPrice);
            tvTourDuration = itemView.findViewById(R.id.tvTourDuration);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }

        public void bind(TourEntity tour, OnAdminTourItemClickListener listener) {
            tvTourLocation.setText(tour.getLocation());
            tvTourPrice.setText(currencyFormatter.format(tour.getPrice()));
            tvTourDuration.setText(tour.getDuration());

            String imagePath = tour.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ivTourItemImage.setImageBitmap(bitmap);
                    ivTourItemImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    ivTourItemImage.setVisibility(View.GONE);
                }
            } else {
                ivTourItemImage.setVisibility(View.GONE);
            }

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(tour);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(tour);
                }
            });
        }
    }
}