package com.example.bankpick.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    Context context;
    String[] titles;
    String[] descriptions;
    int[] images;

    public OnboardingAdapter(Context context, String[] titles, String[] descriptions, int[] images) {
        this.context = context;
        this.titles = titles;
        this.descriptions = descriptions;
        this.images = images;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.tvTitle.setText(titles[position]);
        holder.tvDescription.setText(descriptions[position]);
        holder.ivIllustration.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class SlideViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivIllustration;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivIllustration = itemView.findViewById(R.id.ivIllustration);
        }
    }
}
