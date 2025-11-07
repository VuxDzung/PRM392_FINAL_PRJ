package com.example.prm392_final_prj.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.google.android.material.card.MaterialCardView;

public class InformationActivity extends NavigationBaseActivity {

    private MaterialCardView cardCty1, cardCty2, cardCty3, cardTour3, cardTour4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_information);
        
        // Setup Bottom Navigation
        setupBottomNavigation(R.id.nav_more);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardCty1 = findViewById(R.id.cardCty1);
        cardCty2 = findViewById(R.id.cardCty2);
        cardCty3 = findViewById(R.id.cardCty3);
        cardTour3 = findViewById(R.id.cardTour3);
        cardTour4 = findViewById(R.id.cardTour4);

        cardCty1.setOnClickListener(v -> showImageDialog(R.drawable.cty1));
        cardCty2.setOnClickListener(v -> showImageDialog(R.drawable.cty2));
        cardCty3.setOnClickListener(v -> showImageDialog(R.drawable.cty3));
        cardTour3.setOnClickListener(v -> showImageDialog(R.drawable.tour3));
        cardTour4.setOnClickListener(v -> showImageDialog(R.drawable.tour4));
    }

    private void showImageDialog(int imageResource) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_zoom);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView imageView = dialog.findViewById(R.id.zoomedImage);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);

        imageView.setImageResource(imageResource);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
