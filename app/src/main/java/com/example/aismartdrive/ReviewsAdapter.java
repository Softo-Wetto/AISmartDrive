package com.example.aismartdrive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aismartdrive.DB.user.User;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<User> reviewsList;

    public ReviewsAdapter(List<User> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        User review = reviewsList.get(position);

        holder.vehicleNameTextView.setText("Vehicle Name: " + review.getVehicleName());
        holder.ratingTextView.setText("Rating: " + review.getRating());
        holder.commentTextView.setText("Comment: " + review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView vehicleNameTextView, ratingTextView, commentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            vehicleNameTextView = itemView.findViewById(R.id.vehicleNameTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}

