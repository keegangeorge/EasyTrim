package com.kgeor.easytrim;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private List<DataItem> data;
    private RecyclerView parentRecycler;

    public DashboardAdapter(List<DataItem> data) {
        this.data = data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.home_page_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(data.get(position).getImage())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
//            image.setOnClickListener(this);
            itemView.findViewById(R.id.container).setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            parentRecycler.smoothScrollToPosition(getAdapterPosition());
            if (getLayoutPosition() == 1) {
                System.out.println("#1 is Clicked!");
            }
        }
    }


}
