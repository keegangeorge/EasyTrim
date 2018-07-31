package com.kgeor.easytrim;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * DashboardAdapter class responsible for linking the DiscreteScrollView with the Data
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    // FIELDS //
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
                .load(data.get(position).getImage()) // fetches the correct image for the card
                .into(holder.image); // binds image to holder
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Inner class for initializing the ViewHolder for the DiscreteScrollView
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // FIELDS //
        private ImageView image;

        // CONSTRUCTOR //
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
            itemView.findViewById(R.id.container).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // finds the correct position and scrolls smoothly to it when tapped:
            parentRecycler.smoothScrollToPosition(getAdapterPosition());
        }
    } // inner class end
} // Dashboard Adapter class end
