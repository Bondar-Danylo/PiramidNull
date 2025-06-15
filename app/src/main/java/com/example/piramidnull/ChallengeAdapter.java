package com.example.piramidnull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    public interface OnChallengeClickListener {
        void onChallengeClick(Challenge challenge);
    }

    private List<Challenge> challengeList;
    private OnChallengeClickListener listener;

    public ChallengeAdapter(List<Challenge> challengeList, OnChallengeClickListener listener) {
        this.challengeList = challengeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);

        holder.tvTitle.setText(challenge.getTitle());
        holder.tvName.setText(challenge.getName());
        holder.ivImage.setImageResource(challenge.getImageResId());
        holder.btnOpen.setText(challenge.getButtonText());

        holder.btnOpen.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChallengeClick(challenge);
            }
        });
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvName;
        ImageView ivImage;
        Button btnOpen;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvChallengeTitle);
            tvName = itemView.findViewById(R.id.tvChallengeName);
            ivImage = itemView.findViewById(R.id.ivChallengeImage);
            btnOpen = itemView.findViewById(R.id.btnOpenTool);
        }
    }
}
