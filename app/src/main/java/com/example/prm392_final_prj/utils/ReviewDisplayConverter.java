package com.example.prm392_final_prj.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.prm392_final_prj.activities.NavigationBaseActivity;
import com.example.prm392_final_prj.activities.UserProfileActivity;
import com.example.prm392_final_prj.dto.request.ReviewDisplay;
import com.example.prm392_final_prj.entity.ReviewEntity;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ReviewDisplayConverter {
    private List<UserEntity> listUser;

    public ReviewDisplayConverter(List<UserEntity> listUser) {
        this.listUser = listUser;
    }

    private String getUserDisplayName(int userId) {
        if (listUser == null) {
            return "Unknown User";
        }

        for (UserEntity user : listUser) {
            if (user.getId() == userId) {
                return user.getFirstname() + " " + user.getLastname();
            }
        }
        return "Unknown User";
    }

    public ReviewDisplay convert(ReviewEntity review) {
        return new ReviewDisplay(
                review.getUserId(),
                review.rate,
                review.content,
                TimeConverter.getTimeAgo(review.timeStamp),
                getUserDisplayName(review.userId)
        );
    }

    public List<ReviewDisplay> convert(List<ReviewEntity> reviews) {
        if (reviews == null) {
            return null;
        }

        List<ReviewDisplay> reviewDisplays = new ArrayList<>();
        for (ReviewEntity review : reviews) {
            reviewDisplays.add(convert(review));
        }
        return reviewDisplays;
    }
}
