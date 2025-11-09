package com.example.prm392_final_prj.activities.TourBooking;

import static com.example.prm392_final_prj.activities.TourDetailActivity.EXTRA_TOUR_ID;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.NavigationBaseActivity;
import com.example.prm392_final_prj.activities.TourDetailActivity;
import com.example.prm392_final_prj.adapter.TourScheduleAdapter;
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.example.prm392_final_prj.mockdata.TourMockData;
import com.example.prm392_final_prj.repository.BookingRepository;
import com.example.prm392_final_prj.repository.TourRepository;
import com.example.prm392_final_prj.utils.SessionManager;
import com.example.prm392_final_prj.utils.TimeConverter;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TourPurchaseActivity extends NavigationBaseActivity {

    private TextView tourTitle;

    private AlertDialog qrDialog;

    private LinearLayout layoutDates;
    private String selectedDate = null;

    private TextView totalPrice;

    private TextView originalPrice;
    private Button btnBookNow;
    private double tourPrice;
    private double totalPriceDouble;
    private BookingOrderEntity booking = new BookingOrderEntity();

    private BookingRepository bookingRepository;
    private TourRepository tourRepository;

    private SessionManager sessionManager;


    private TourMockData mockData = new TourMockData();

    private int tourId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tour_purchase);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation(R.id.nav_home);

        bookingRepository = new BookingRepository(getApplication());
        tourRepository = new TourRepository(getApplication());
        sessionManager = new SessionManager(this);

        tourTitle = findViewById(R.id.tv_tour_title);
        layoutDates = findViewById(R.id.layout_dates);
        totalPrice = findViewById(R.id.tv_total_price);
        originalPrice = findViewById(R.id.tv_original_price);

        tourId = getIntent().getIntExtra(EXTRA_TOUR_ID, -1);
        loadTourDetails(tourId);


        //TODO: Add real schedule of it
        List<String> tourDates = Arrays.asList("26/10", "23/11", "06/12");
        addDateButtons(tourDates);

        btnBookNow = findViewById(R.id.btn_book_now);
        btnBookNow.setOnClickListener(v -> {
            if(totalPriceDouble == 0){
                Toast.makeText(this, "Please choose number of people!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(selectedDate == null){
                Toast.makeText(this, "Please choose date!", Toast.LENGTH_SHORT).show();
                return;
            }
            String fullDate = selectedDate + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            booking.tourId = tourId;
            booking.startTime = TimeConverter.parseDateString(fullDate);
            booking.status = 1;
            booking.userId = sessionManager.getUserId();
            showQrPaymentPopup();
        });
    }

    private void loadTourDetails(int id) {
        tourRepository.getTourById(id).observe(this, new Observer<TourEntity>() {
            @Override
            public void onChanged(TourEntity tour) {
                if (tour != null) {
                    displayTourDetails(tour);
                } else {
                    // Mockdata for testing
                    var mockTour = mockData.getTourById(id);
                    if (mockTour != null) {
                        displayTourDetails(mockTour);
                    } else {
                        Toast.makeText(TourPurchaseActivity.this, "Tour not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void displayTourDetails(TourEntity tour) {
        tourTitle.setText(tour.location + " " + tour.duration);
        tourPrice = tour.price;
        personSelection();
    }

//    private void showBookingForm() {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.dialog_booking_form, null);
//
//        Button btnSendRequest = view.findViewById(R.id.btn_send_request);
//
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(view)
//                .create();
//
//        btnSendRequest.setOnClickListener(v -> {
//            dialog.dismiss();
//            showQrPaymentPopup(); // Move to QR payment
//        });
//
//        dialog.show();
//    }

    private void showQrPaymentPopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_qr_payment, null);

        TextView tvAccountName = view.findViewById(R.id.tv_account_name);
        TextView tvAccountNumber = view.findViewById(R.id.tv_account_number);
        TextView tvPaymentAmount = view.findViewById(R.id.tv_payment_amount);
        TextView tvPaymentContent = view.findViewById(R.id.tv_payment_content);
        ImageView imgQr = view.findViewById(R.id.img_qr);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);

        // Optional: Dynamically set payment info
        tvAccountName.setText("Tài khoản: MINH ĐẸP ZAI");
        tvAccountNumber.setText("STK: 01234686868");
        tvPaymentAmount.setText("Số tiền: " + String.format("%.2f", totalPriceDouble) + " $");
        tvPaymentContent.setText("Nội dung: Tour du lịch " + tourTitle.getText());
        imgQr.setImageResource(R.drawable.ic_qr_placeholder);

        qrDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        btnConfirm.setOnClickListener(v ->{
            bookingRepository.insertBooking(booking);
            bookingRepository.getBookingsForUser(sessionManager.getUserId()).observe(this, bookings -> {
                System.out.println(bookings.isEmpty());
            });
            Toast.makeText(TourPurchaseActivity.this, "Booking confirmed", Toast.LENGTH_SHORT).show();
            qrDialog.dismiss();
        });

        qrDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (qrDialog != null && qrDialog.isShowing()) {
            qrDialog.dismiss();
        }
        super.onDestroy();
    }

    private void addDateButtons(List<String> dates) {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (String date : dates) {
            Button btn = new Button(this);
            btn.setText(date);
            btn.setAllCaps(false);

            // Optional styling
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                selectedDate = date;
                highlightSelectedButton(btn);
            });

            layoutDates.addView(btn);
        }
    }

    /** Highlight the selected date button */
    private void highlightSelectedButton(Button selectedBtn) {
        for (int i = 0; i < layoutDates.getChildCount(); i++) {
            View child = layoutDates.getChildAt(i);
            if (child instanceof Button) {
                child.setBackgroundColor(getColor(
                        child == selectedBtn ? R.color.teal_700 : android.R.color.darker_gray
                ));
            }
        }
    }

    private void personSelection() {
        int[] includeIds = { R.id.item_adult, R.id.item_child, R.id.item_infant };

        for (int id : includeIds) {
            View includeView = findViewById(id);
            String tag;
            boolean isAdult;
            if(id == R.id.item_child) {
                tag = "Kid 2 - 9 yo";
                isAdult = false;
            }
            else if (id == R.id.item_infant){
                tag = "Kid under 2 yo";
                isAdult = false;
            }
            else {
                tag = "Adult > 9 yo";
                isAdult = true;
            }
            if (includeView != null) {
                TextView tvLabel = includeView.findViewById(R.id.tv_label);
                tvLabel.setText(tag);

                Button btnPlus = includeView.findViewById(R.id.btn_plus);
                Button btnMinus = includeView.findViewById(R.id.btn_minus);
                TextView tvCount = includeView.findViewById(R.id.tv_count);

                btnPlus.setOnClickListener(v -> {
                    int count = Integer.parseInt(tvCount.getText().toString());
                    tvCount.setText(String.valueOf(count + 1));
                    updateTotalPrice(includeIds);
                    if(isAdult){
                        booking.adultAmount = count + 1;
                    }else {
                        booking.childAmount += count + 1;
                    }
                });

                btnMinus.setOnClickListener(v -> {
                    int count = Integer.parseInt(tvCount.getText().toString());
                    if (count > 0) {
                        tvCount.setText(String.valueOf(count - 1));
                        updateTotalPrice(includeIds);
                        if(isAdult){
                            booking.adultAmount = count - 1;
                        }else {
                            booking.childAmount += count - 1;
                        }
                    }
                });
            }
        }
    }

    private void updateTotalPrice(int[] includeIds) {
        double total = 0;

        for (int id : includeIds) {
            View includeView = findViewById(id);
            if (includeView != null) {
                int count = Integer.parseInt(
                        ((TextView) includeView.findViewById(R.id.tv_count)).getText().toString()
                );

                double price;
                if (id == R.id.item_child) price = tourPrice / 2;
                else if (id == R.id.item_infant) price = tourPrice / 4;
                else price = tourPrice;

                total += count * price;
            }
        }

        totalPriceDouble = total;
        // Update total and original price TextViews
        totalPrice.setText("Total Price: " + String.format("%.2f", total) + " $");
        originalPrice.setText("Original Price: " + String.format("%.2f", total * 2) + " $");
    }
}