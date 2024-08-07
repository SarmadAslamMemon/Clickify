package com.example.clickify.AdminDashBoard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickify.R;
import com.example.clickify.User;
import com.example.clickify.UtilityClass;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(User user, int position);
    }

    public UserListAdapter(Context context, List<User> userList, OnItemClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userId;
        public TextView userEmail;
        public Button actionButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.user_id);
            userEmail = itemView.findViewById(R.id.user_email);
            actionButton = itemView.findViewById(R.id.action_button);
        }

        public void bind(final User user, final OnItemClickListener listener, int position) {

            userId.setText(String.valueOf(user.getId()));
            userEmail.setText(user.getUserEmail());
            if (Objects.equals(user.getIsRegistered(), "1")) {
                actionButton.setText("Delete");
            } else {
                actionButton.setText("Approve");
            }

            // Check if the internet is available
            if (UtilityClass.isInternetAvailable(actionButton.getContext())) {
                actionButton.setOnClickListener((View v) -> {
                    listener.onItemClick(user, position);
                });
            } else {
                showSnackBar(actionButton); // Pass the actionButton view to showSnackBar
            }
        }

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, listener, holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    public static void showSnackBar(View v)
    {
        Snackbar.make(v,"Please Check Your Internet Connection!", Snackbar.LENGTH_LONG).show();
    }
}
