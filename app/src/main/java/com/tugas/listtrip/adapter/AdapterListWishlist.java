package com.tugas.listtrip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tugas.listtrip.DetailWisataActivity;
import com.tugas.listtrip.R;
import com.tugas.listtrip.model.Wishlist;

import java.util.List;

public class AdapterListWishlist extends RecyclerView.Adapter<AdapterListWishlist.HolderView> {

    private List<Wishlist> listWishlist;
    private Context mContext;

    public AdapterListWishlist(List<Wishlist> listWishlist, Context mContext) {
        this.listWishlist = listWishlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.listdestination, parent, false);

        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {

        final Wishlist wishlist = listWishlist.get(position);

        holder.tvPlaceName.setText(wishlist.getName());
        holder.tvView.setText(wishlist.getView());
        holder.tvTicket.setText("IDR "+wishlist.getTicket());
        holder.rbItem.setRating(Float.parseFloat(wishlist.getRating()));

        Glide.with(mContext)
                .load(wishlist.getPhoto())
                .centerCrop()
                .into(holder.ivItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DetailWisataActivity.class);
                intent.putExtra(DetailWisataActivity.DETAIL_DESTINATION, wishlist.getId_destination());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listWishlist.size();
    }

    class HolderView extends RecyclerView.ViewHolder{

        ImageView ivItem;
        TextView tvPlaceName, tvView, tvTicket;
        RatingBar rbItem;

        HolderView(@NonNull View itemView) {
            super(itemView);

            ivItem = itemView.findViewById(R.id.ivItemDestination);
            tvPlaceName = itemView.findViewById(R.id.tvItemPlaceNameDestination);
            tvView = itemView.findViewById(R.id.tvViewItemDestination);
            tvTicket = itemView.findViewById(R.id.tvTicketItemDestination);
            rbItem = itemView.findViewById(R.id.rbItemDestination);
        }
    }

}
