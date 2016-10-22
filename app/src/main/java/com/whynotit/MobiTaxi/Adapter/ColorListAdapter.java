package com.whynotit.MobiTaxi.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harzallah on 21/04/2016.
 */
public class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.ViewHolder> {
    private final MainActivity activity;
    private List<String> colorsList;

    public ColorListAdapter(MainActivity activity) {
        this.activity = activity;
        colorsList = new ArrayList<>();
        colorsList.add("#ffffff"); //black
        colorsList.add("#000000"); //white
        colorsList.add("#9e9e9e"); //grey
        colorsList.add("#f44336"); //red
        colorsList.add("#304ffe"); //blue
        colorsList.add("#ffeb3b"); //yellow
        colorsList.add("#00c853"); //green
        colorsList.add("#ec407a"); //pink
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView colorView;


        public ViewHolder(View view) {
            super(view);

            colorView = (CircleImageView) view.findViewById(R.id.color_view);
        }
    }


    @Override
    public ColorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_layout, parent, false);
        return new ColorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorListAdapter.ViewHolder holder, final int position) {
        final int color = Color.parseColor(colorsList.get(position));
        holder.colorView.setColorFilter(color);
        holder.colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getTshirt() != null) {
                    activity.getTshirt().setColorFilter(color);
                    activity.getAvatarSelectionner().setCouleur(colorsList.get(position));
                }
                else
                    activity.showSnackBar("Commencez par choisir un avatar!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorsList.size();
    }
}
