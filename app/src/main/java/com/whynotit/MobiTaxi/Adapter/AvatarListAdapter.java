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
import com.whynotit.MobiTaxi.Fragment.IndicationFragment;
import com.whynotit.MobiTaxi.R;

import java.util.List;

/**
 * Created by Harzallah on 22/04/2016.
 */
public class AvatarListAdapter extends RecyclerView.Adapter<AvatarListAdapter.ViewHolder> {
    private final MainActivity activity;
    private final IndicationListAdapter mIndicationAdapter;
    private List<Avatar> avatarsList;

    public AvatarListAdapter(MainActivity activity, IndicationListAdapter mIndicationAdapter) {
        this.activity = activity;

        this.mIndicationAdapter = mIndicationAdapter;
        avatarsList = activity.getAvatarsList();
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
    public AvatarListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.avatar_layout_small, parent, false);
        return new AvatarListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AvatarListAdapter.ViewHolder holder, final int position) {

        Picasso.with(activity)
                .load(activity.getResources().getString(R.string.host)+avatarsList.get(position).getUrl())
                .noPlaceholder()
                .into(holder.avatar);

        Picasso.with(activity)
                .load(activity.getResources().getString(R.string.host)+avatarsList.get(position).getUrl().replace(".png","_tshirt.png"))
                .noPlaceholder()
                .into(holder.tshirt);



        holder.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setAvatarSelectionner(avatarsList.get(position));
                activity.getIndicationsList().add(avatarsList.get(position));
                mIndicationAdapter.notifyDataSetChanged();
                IndicationFragment.scrollToLastAvatarAdded(activity.getIndicationsList().size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarsList.size();
    }
}
