package com.tugas.listtrip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tugas.listtrip.DetailWisataActivity;
import com.tugas.listtrip.R;
import com.tugas.listtrip.model.Destination;

import java.util.ArrayList;
import java.util.List;

public class AdapterListDestination extends RecyclerView.Adapter<AdapterListDestination.ViewHolder> implements Filterable {

    private List<Destination> listDestination;
    private List<Destination> listDestinationFiltered;
    private Context mContext;

    public AdapterListDestination(List<Destination> listDestination, Context mContext) {
        this.listDestination = listDestination;
        this.listDestinationFiltered = listDestination;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listdestination, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Destination destination = listDestination.get(position);

        Glide.with(mContext)
                .load(destination.getPhoto())
                .centerCrop()
                .into(holder.ivItem);

        holder.tvPlaceName.setText(destination.getName());
        holder.tvView.setText(destination.getView());
        holder.tvTicket.setText("IDR "+destination.getTicket());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DetailWisataActivity.class);
                intent.putExtra(DetailWisataActivity.DETAIL_DESTINATION, destination.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDestination.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    listDestinationFiltered = listDestination;
                }else {
                    List<Destination> filteredList = new ArrayList<>();
                    for(Destination row : listDestination){
                        if(row.getName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    listDestinationFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listDestinationFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listDestinationFiltered = (ArrayList<Destination>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivItem;
        TextView tvPlaceName, tvView, tvTicket;
        RatingBar rbItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItem = itemView.findViewById(R.id.ivItemDestination);
            tvPlaceName = itemView.findViewById(R.id.tvItemPlaceNameDestination);
            tvView = itemView.findViewById(R.id.tvViewItemDestination);
            tvTicket = itemView.findViewById(R.id.tvTicketItemDestination);
            rbItem = itemView.findViewById(R.id.rbItemDestination);

        }
    }

}
