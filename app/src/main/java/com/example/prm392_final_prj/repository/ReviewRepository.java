package com.example.prm392_final_prj.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.prm392_final_prj.dao.ReviewDao;
import com.example.prm392_final_prj.dao.AppDatabase; // <-- ĐÃ THAY ĐỔI
import com.example.prm392_final_prj.entity.ReviewEntity;
import java.util.List;

public class ReviewRepository {

    private ReviewDao mReviewDao;

    public ReviewRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mReviewDao = db.reviewDao();
    }

    public void insertReview(ReviewEntity review) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mReviewDao.insert(review);
        });
    }

    public LiveData<List<ReviewEntity>> getReviewsForTour(int tourId) {
        return mReviewDao.getReviewsForTour(tourId);
    }
}