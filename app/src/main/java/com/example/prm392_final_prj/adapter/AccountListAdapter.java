package com.example.prm392_final_prj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.AccountManagementActivity;
import com.example.prm392_final_prj.entity.UserEntity;

import java.util.*;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {
    private List<UserEntity> userList;
    private final Context context;

    public AccountListAdapter(List<UserEntity> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }
    public void setAccountList(List<UserEntity> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_list_item, parent, false);
        return new AccountListAdapter.AccountViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        UserEntity user = userList.get(position);
        holder.tvAccId.setText(String.valueOf(user.getId()));
        holder.accountNameText.setText(user.getFirstname() + " " + user.getLastname());
        holder.accountEmailText.setText(user.getEmail());
        holder.deleteButton.setEnabled(!(user.getRole().equalsIgnoreCase("ROLE_ADMIN") ||
                                        (user.getRole().equalsIgnoreCase("admin"))
                                        ));
        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof AccountManagementActivity) {
                ((AccountManagementActivity) context).deleteUser(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAccId, accountNameText, accountEmailText;
        private final ImageButton deleteButton;

        public AccountViewHolder(@NonNull View itemView, AccountListAdapter adapter) {
            super(itemView);
            tvAccId = itemView.findViewById(R.id.tvAccountId);
            accountNameText = itemView.findViewById(R.id.tvAccountName);
            accountEmailText = itemView.findViewById(R.id.tvAccountEmail);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
