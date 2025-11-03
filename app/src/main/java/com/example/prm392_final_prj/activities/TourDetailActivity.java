package com.example.prm392_final_prj.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;

import java.util.Locale;

public class TourDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TOUR_ID = "tour_id";
    public static final String EXTRA_TOUR_LOCATION = "tour_location";
    public static final String EXTRA_TOUR_DURATION = "tour_duration";
    public static final String EXTRA_TOUR_PRICE = "tour_price";
    public static final String EXTRA_TOUR_ROUTE = "tour_route";
    public static final String EXTRA_TOUR_TRANSPORT = "tour_transport";
    public static final String EXTRA_TOUR_MAX_CAPACITY = "tour_max_capacity";
    public static final String EXTRA_TOUR_AVAILABLE_SEAT = "tour_available_seat";
    public static final String EXTRA_TOUR_IMAGE_BYTES = "tour_image_bytes";

    // Khai báo Views
    private ImageView detailImage;
    private TextView detailLocation;
    private TextView detailDuration;
    private TextView detailPrice;
    private TextView detailRoute;
    private TextView detailTransport;
    private TextView detailSeatsAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        detailImage = findViewById(R.id.detail_tour_image);
        detailLocation = findViewById(R.id.detail_tour_location);
        detailDuration = findViewById(R.id.detail_duration);
        detailPrice = findViewById(R.id.detail_price);
        detailRoute = findViewById(R.id.detail_route);
        detailTransport = findViewById(R.id.detail_transport);
        detailSeatsAvailable = findViewById(R.id.detail_seats_available);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            detailLocation.setText(extras.getString(EXTRA_TOUR_LOCATION));
            detailDuration.setText(extras.getString(EXTRA_TOUR_DURATION));
            detailPrice.setText(extras.getString(EXTRA_TOUR_PRICE));
            detailRoute.setText(extras.getString(EXTRA_TOUR_ROUTE));
            detailTransport.setText(extras.getString(EXTRA_TOUR_TRANSPORT));



            int maxCapacity = extras.getInt(EXTRA_TOUR_MAX_CAPACITY, 0);
            int availableSeat = extras.getInt(EXTRA_TOUR_AVAILABLE_SEAT, 0);
            detailSeatsAvailable.setText(String.format(Locale.US, "%d/%d seats", availableSeat, maxCapacity));

            // Xử lý Image
            byte[] imageBytes = extras.getByteArray(EXTRA_TOUR_IMAGE_BYTES);
            if (imageBytes != null && imageBytes.length > 0) {
                detailImage.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            } else {
                detailImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}