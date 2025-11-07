package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_final_prj.R;

import com.example.prm392_final_prj.activities.FeedbackActivity;
import com.example.prm392_final_prj.activities.NavigationBaseActivity;
import com.example.prm392_final_prj.dto.request.ReviewDisplay;
import com.example.prm392_final_prj.entity.ReviewEntity;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.repository.ReviewRepository;
import com.example.prm392_final_prj.utils.ReviewDisplayConverter;
import com.example.prm392_final_prj.utils.TimeConverter;

import java.util.List;

public class ReviewListAdapter
        extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private List<ReviewDisplay> reviewList;
    private final Context context;
    private final ReviewDisplayConverter converter;

    public ReviewListAdapter(List<ReviewEntity> reviewList, List<UserEntity> userList, Context context) {
        converter = new ReviewDisplayConverter(userList);
        this.reviewList = converter.convert(reviewList);
        this.context = context;
    }

    public void setReviewList(List<ReviewEntity> reviewList){
        this.reviewList = converter.convert(reviewList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewDisplay review = reviewList.get(position);
        holder.userId = review.getUserId();

        holder.reviewContent.setText(review.getContent());

        holder.reviewUserName.setText(review.getUsername());
        holder.reviewTimeAgo.setText(review.getTimeAgo());

        holder.setOptionsVisibility();

        bindStarRating(holder.reviewRatingStars, review.getRate(), context);
    }

    private void bindStarRating(LinearLayout starLayout, float rate, Context context) {
        starLayout.removeAllViews();

        int filledStars = Math.round(rate);
        int starColor = ContextCompat.getColor(context, R.color.yellow);

        for (int i = 1; i <= 5; i++) {
            TextView star = new TextView(context);
            star.setText("★");
            star.setTextSize(16);

            if (i <= filledStars) {
                star.setTextColor(starColor);
                star.setText("★");
            } else {
                star.setTextColor(starColor);
                star.setText("☆");
            }
            starLayout.addView(star);
        }
    }


    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private int userId;
        private final ImageView reviewUserAvatar;
        private final TextView reviewUserName;
        private final TextView reviewTimeAgo;
        private final LinearLayout reviewRatingStars;
        private final TextView reviewContent;
        private final ReviewListAdapter adapter;
        private final ImageView btnReviewOptions;

        public ReviewViewHolder(@NonNull View itemView, ReviewListAdapter adapter){
            super(itemView);
            this.adapter = adapter;
            reviewUserAvatar = itemView.findViewById(R.id.review_user_avatar);
            reviewUserName = itemView.findViewById(R.id.review_user_name);
            reviewTimeAgo = itemView.findViewById(R.id.review_time_ago);
            reviewRatingStars = itemView.findViewById(R.id.review_rating_stars);
            reviewContent = itemView.findViewById(R.id.review_content);
            btnReviewOptions = itemView.findViewById(R.id.btn_review_options);
        }

        public void setOptionsVisibility(){
            if (adapter.context instanceof FeedbackActivity
                    && ((FeedbackActivity) adapter.context).getCurrentUserId() == userId) {
                btnReviewOptions.setOnClickListener(this::showPopupMenu);
            }
            else {
                btnReviewOptions.setVisibility(View.GONE);
            }
        }

        private void showPopupMenu(View view) {
            PopupMenu popup = new PopupMenu(adapter.context, view);
            popup.getMenuInflater().inflate(R.menu.review_options_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.action_edit_review) {
                    ((FeedbackActivity) adapter.context).editReview();
                    return true;
                } else if (itemId == R.id.action_delete_review) {
                    ((FeedbackActivity) adapter.context).deleteReview();
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }
}