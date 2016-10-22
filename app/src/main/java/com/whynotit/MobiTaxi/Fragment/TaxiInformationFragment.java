package com.whynotit.MobiTaxi.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.Models.Taxist;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harzallah on 03/06/2016.
 */
public class TaxiInformationFragment extends Fragment {

    private RelativeLayout loadingLayout;
    private LinearLayout rootLayout;
    private TextView taxistNom, taxistTelephone,taxiNumero,taxiMatricule,taxiNombrePlaces,marque,type,climatise;
    private CircleImageView taxistPhoto;
    private RatingBar note;
    private FloatingActionButton callTaxi;
    private MainActivity activity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information_taxi,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = MainActivity.getActivity();

        loadingLayout = (RelativeLayout) view.findViewById(R.id.loading_layout);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);

        taxistNom = (TextView) view.findViewById(R.id.taxist_nom);
        taxistTelephone = (TextView) view.findViewById(R.id.taxist_telephone);
        taxiNumero = (TextView) view.findViewById(R.id.taxi_numero);
        taxiMatricule = (TextView) view.findViewById(R.id.taxi_matricule);
        taxiNombrePlaces = (TextView) view.findViewById(R.id.taxi_nombre_places);
        taxistPhoto = (CircleImageView) view.findViewById(R.id.taxist_photo);
        note = (RatingBar) view.findViewById(R.id.taxi_note);
        callTaxi = (FloatingActionButton) view.findViewById(R.id.call_taxi);
        marque = (TextView) view.findViewById(R.id.taxi_marque);
        type = (TextView) view.findViewById(R.id.taxi_type);
        climatise = (TextView) view.findViewById(R.id.climatise);


    }


    public void updateTaxisInformation (final Taxist taxist)
    {
        Picasso.with(activity)
                .load(activity.getResources().getString(R.string.host)+"/iTaxi/ch_img/"+taxist.getId()+".png")
                .fit()
                .placeholder(R.drawable.ch_img)
                .error(R.drawable.ch_img)
                .into(taxistPhoto);

        taxistNom.setText(taxist.getNom());
        taxistTelephone.setText(taxist.getTelephone());
        try {
            note.setRating(Float.valueOf(taxist.getNote()));
        } catch (NullPointerException e) {
            note.setRating(4);
        }
        taxiNumero.setText(taxist.getTaxiNumero());
        taxiNombrePlaces.setText(taxist.getNombrePlaces());
        marque.setText(taxist.getMarque());
        type.setText(taxist.getType());
        climatise.setText(taxist.isClimatise() ? "Oui" : "Non");


        int numSerie = 0;
        int numVoiture = 0;
        try {
            numSerie = Integer.valueOf((taxist.getTaxiMatricule().split(":"))[0]);
            numVoiture = Integer.valueOf((taxist.getTaxiMatricule().split(":"))[1]);
        } catch (NumberFormatException e) {

        }

        taxiMatricule.setText(numSerie + " TN " +numVoiture);
        callTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.insertCallTaxi(taxist.getId());
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + taxist.getTelephone()));
                startActivity(intent);
            }
        });


        loadingLayout.setVisibility(View.GONE);
        rootLayout.setVisibility(View.VISIBLE);
        callTaxi.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        activity.toggleDrawerUse(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setTitle("Détails Taxi");

    }


}
