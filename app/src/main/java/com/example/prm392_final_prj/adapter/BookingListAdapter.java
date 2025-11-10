package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import com.example.prm392_final_prj.repository.BookingRepository;
import com.example.prm392_final_prj.utils.BookingStatus;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder> {

    private final Context context;
    private final List<BookListData> bookingList;

    private final BookingRepository bookingRepository;

    public BookingListAdapter(Context context, List<BookListData> bookingList, BookingRepository bookingRepository) {
        this.context = context;
        this.bookingList = bookingList;
        this.bookingRepository = bookingRepository;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_booking, parent, false);
        return new BookingViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookingList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, bookingInfo;

        MaterialSwitch switchStatus;

        BookingListAdapter adapter;

        Button deleteButton;

        public BookingViewHolder(@NonNull View itemView, BookingListAdapter adapters) {
            super(itemView);
            adapter = adapters;
            customerName = itemView.findViewById(R.id.tv_booking_user);
            bookingInfo = itemView.findViewById(R.id.tv_booking_info);
            switchStatus = itemView.findViewById(R.id.switch_status);
            deleteButton = itemView.findViewById(R.id.btn_delete_booking);
        }

        void bind(BookListData booking) {
            customerName.setText(booking.customerName);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(booking.bookingData.startTime);

            bookingInfo.setText(
                    "Adults: " + booking.bookingData.adultAmount + " | "
                    + "Children: " + booking.bookingData.childAmount + " | "
                    + "Date: " + formattedDate
            );

            setSwitchStatus(booking);
            setDeleteButton();
        }

        private void setSwitchStatus(BookListData booking){
            // Remove previous listener to avoid recycled view issues
            switchStatus.setOnCheckedChangeListener(null);

            int status = booking.bookingData.status;
            String statusString = BookingStatus.getStatusString(status);

            if (status == 1) {
                switchStatus.setChecked(false);
                switchStatus.setText(statusString);
                switchStatus.setEnabled(true);
            } else if (status == 2) {
                switchStatus.setChecked(true);
                switchStatus.setText(statusString);
                switchStatus.setEnabled(true);
            } else {
                // Disable the switch for other status values
                switchStatus.setChecked(false);
                switchStatus.setText(statusString);
                switchStatus.setEnabled(false);
            }

            if (switchStatus.isEnabled()) {
                switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // Update text
                    switchStatus.setText(isChecked ? "Active" : "Inactive");

                    // Update status in database
                    int newStatus = isChecked ? 2 : 1;
                    new Thread(() -> {
                        adapter.bookingRepository.updateBookingStatus(booking.bookingData.id, newStatus);
                    }).start();
                });
            }
        }

        private void setDeleteButton(){
            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BookListData booking = adapter.bookingList.get(position);

                    // Delete from database in a background thread
                    new Thread(() -> {
                        adapter.bookingRepository.deleteBooking(booking.bookingData.id);

                        // Update UI on main thread
                        itemView.post(() -> {
                            adapter.bookingList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, adapter.bookingList.size());
                        });
                    }).start();
                }
            });
        }
    }

    public static class BookListData
    {
        public BookingOrderEntity bookingData;
        public String customerName;

        public BookListData(BookingOrderEntity bookingData, String customerName) {
            this.bookingData = bookingData;
            this.customerName = customerName;
        }

        public BookListData() {
        }
    }
}


