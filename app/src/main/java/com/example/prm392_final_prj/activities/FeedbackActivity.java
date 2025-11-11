package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.ReviewListAdapter;
import com.example.prm392_final_prj.entity.ReviewEntity;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.repository.ReviewRepository;
import com.example.prm392_final_prj.repository.UserRepository;
import com.example.prm392_final_prj.utils.SessionManager;

import java.util.Date;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private SessionManager sessionManager;

    private RatingBar ratingBarInput;
    private EditText editTextFeedbackContent;
    private Button btnSendFeedback;
    private Button btnCancel;
    private RecyclerView recyclerViewReviews;

    private int currentUserId;
    private int currentTourId;
    private UserEntity currentUser;
    private List<UserEntity> userList;
    private List<ReviewEntity> reviewList;
    private ReviewListAdapter reviewAdapter;
    private ReviewEntity userExistingReview;
    private int editReviewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Feedback");
        }

        userRepository = new UserRepository(getApplication());
        reviewRepository = new ReviewRepository(getApplication());
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        Intent intent = getIntent();
        if (intent != null) {
            currentTourId = intent.getIntExtra(TourDetailActivity.EXTRA_TOUR_ID, -1);
        }

        ratingBarInput = findViewById(R.id.rating_bar_input);
        ratingBarInput.setRating(0);

        editTextFeedbackContent = findViewById(R.id.edit_text_feedback_content);
        btnSendFeedback = findViewById(R.id.btn_send_feedback);
        btnCancel = findViewById(R.id.btn_cancel);
        recyclerViewReviews = findViewById(R.id.recycler_view_reviews);

        btnCancel.setVisibility(View.GONE);
        btnCancel.setOnClickListener(v -> cancelEdit());

        btnSendFeedback.setOnClickListener(v -> sendFeedback());

        loadUsers();
        loadReviews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);

        if (intent != null) {
            intent.putExtra(TourDetailActivity.EXTRA_TOUR_ID, currentTourId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
        } else {
            finish();
        }

        return true;
    }


    public int getCurrentUserId() {
        return currentUserId;
    }

    private void loadUsers() {
        userRepository.getAllUsers().observe(this, users -> {
            if (users != null) {
                userList = users;
            } else {
                Toast.makeText(FeedbackActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                btnSendFeedback.setEnabled(false);
            }
        });

        userRepository.getUserById(currentUserId).observe(this, user -> {
            if (user != null) {
                FeedbackActivity.this.currentUser = user;
            } else {
                btnSendFeedback.setEnabled(false);
            }
        });
    }

    private void loadReviews() {
        if (currentTourId != -1) {
            reviewRepository.getReviewsForTour(currentTourId).observe(this, reviews -> {
                if (reviews != null) {
                    reviewList = reviews;

                    userExistingReview = reviews.stream()
                            .filter(review -> review.userId == currentUserId)
                            .findFirst()
                            .orElse(null);

                    if (userExistingReview != null) {
                        btnSendFeedback.setEnabled(false);
                    } else {
                        btnSendFeedback.setEnabled(true);
                    }

                    // Update the adapter
                    if (reviewAdapter == null) {
                        reviewAdapter = new ReviewListAdapter(new java.util.ArrayList<>(), userList, this);
                        recyclerViewReviews.setAdapter(reviewAdapter);
                        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
                    }
                    reviewAdapter.setReviewList(reviews);
                }
            });
        }
    }

    private void sendFeedback() {
        boolean isEdit = editReviewId != 0;

        float rate = ratingBarInput.getRating();
        String content = editTextFeedbackContent.getText().toString().trim();

        if (currentUserId <= 0 || currentTourId <= 0 || currentUser == null) {
            Toast.makeText(this, "System error: User or Tour ID not loaded.", Toast.LENGTH_LONG).show();
            return;
        }

        if (rate == 0) {
            Toast.makeText(this, "Please select a rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback content.", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewEntity newReview = new ReviewEntity(
                editReviewId,
                rate,
                content,
                new Date(),
                currentTourId,
                currentUserId
        );

        if (isEdit) {
            reviewRepository.updateReview(newReview);
            Toast.makeText(this, "Feedback updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            reviewRepository.insertReview(newReview);
            Toast.makeText(this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();
        }




        if (isEdit){
            cancelEdit();
        }
        else{
            editTextFeedbackContent.setText("");
            //Default Rating
            ratingBarInput.setRating(0);
        }
    }

    private void cancelEdit() {
        editTextFeedbackContent.setText("");
        //Default Rating
        ratingBarInput.setRating(0);
        btnSendFeedback.setText("Send Feedback");
        editReviewId = 0;
        btnCancel.setVisibility(View.GONE);
    }

    public void editReview(){
        if (userExistingReview == null){
            Toast.makeText(this, "Review not found", Toast.LENGTH_SHORT).show();
            return;
        }

        editTextFeedbackContent.setText(userExistingReview.content);
        ratingBarInput.setRating(userExistingReview.rate);

        btnSendFeedback.setText("Save Edit");
        btnSendFeedback.setEnabled(true);

        findViewById(R.id.input_scroll_view).requestFocus();
        editReviewId = userExistingReview.id;
        btnCancel.setVisibility(View.VISIBLE);
    }

    public void deleteReview() {
        reviewRepository.deleteReview(userExistingReview);
    }
}