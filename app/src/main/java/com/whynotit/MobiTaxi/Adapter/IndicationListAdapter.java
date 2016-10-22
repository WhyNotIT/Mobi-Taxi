package com.whynotit.MobiTaxi.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.Models.Avatar;
import com.whynotit.MobiTaxi.R;

import java.util.List;

/**
 * Created by Harzallah on 22/04/2016.
 */
public class IndicationListAdapter extends RecyclerView.Adapter<IndicationListAdapter.ViewHolder> {
    private final MainActivity activity;
    private List<Avatar> indicationsList;

    public IndicationListAdapter (MainActivity activity) {
        this.activity = activity;
        indicationsList = activity.getIndicationsList();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatar;
        public ImageView tshirt;
        public RelativeLayout avatarView;


        public ViewHolder(View view) {
            super(view);

            avatar = (ImageView) view.findViewById(R.id.avatar);
            tshirt = (ImageView) view.findViewById(R.id.tshirt);
            avatarView = (RelativeLayout) view.findViewById(R.id.avatar_view);
        }
    }


    @Override
    public IndicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.avatar_layout, parent, false);
        return new IndicationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IndicationListAdapter.ViewHolder holder, final int position) {

        Picasso.with(activity)
                .load(activity.getResources().getString(R.string.host)+ indicationsList.get(position).getUrl())
                .noPlaceholder()
                .into(holder.avatar);

        Picasso.with(activity)
                .load(activity.getResources().getString(R.string.host)+ indicationsList.get(position).getUrl().replace(".png","_tshirt.png"))
                .noPlaceholder()
                .into(holder.tshirt);

        activity.setTshirt(holder.tshirt);

        if (indicationsList.get(position).getCouleur() != null)
        {
            holder.tshirt.setColorFilter(indicationsList.get(position).getCouleurInt());
        }

        holder.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.setTshirt(holder.tshirt);
                indicationsList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return indicationsList.size();
    }
}
