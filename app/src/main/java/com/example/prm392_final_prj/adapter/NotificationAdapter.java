package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.NotificationEntity;
import com.example.prm392_final_prj.repository.NotificationRepository;
import com.example.prm392_final_prj.utils.TimeConverter;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationEntity> notiList = new ArrayList<>();
    private final Context context;
    private final NotificationRepository repository;

    public NotificationAdapter(Context context, NotificationRepository repository) {
        this.context = context;
//        this.notiList = notiList;
        this.repository = repository;
    }

    public void setNotiList(List<NotificationEntity> list) {
        this.notiList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noti_list_item, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationEntity noti = notiList.get(position);

        holder.tvNotiTitle.setText(noti.getTitle());
        holder.tvNotiMessage.setText(noti.getMessage());
        holder.tvTimeStamp.setText(TimeConverter.getTimeAgo(noti.getCreatedAt()));

        holder.rbIsRead.setChecked(noti.isRead());

        // Change item background.
        if (noti.isRead()) {
            holder.llItem.setBackgroundColor(Color.parseColor("#EEEEEE")); // xám nhạt
        } else {
            holder.llItem.setBackgroundColor(Color.parseColor("#FFFFFF")); // trắng
        }

        holder.rbIsRead.setOnClickListener(v -> {
            if (!noti.isRead()) {
                repository.markNotificationAsRead(noti.getId());
                noti.setRead(true);
                holder.rbIsRead.setChecked(true);
                holder.llItem.setBackgroundColor(Color.parseColor("#EEEEEE"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView tvNotiTitle, tvNotiMessage, tvTimeStamp;
        RadioButton rbIsRead;
        LinearLayout llItem;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.llItem);
            tvNotiTitle = itemView.findViewById(R.id.tvNotiTitle);
            tvNotiMessage = itemView.findViewById(R.id.tvNotiMessage);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            rbIsRead = itemView.findViewById(R.id.rbIsRead);
        }
    }
}
