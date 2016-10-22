package com.whynotit.MobiTaxi.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.Models.Distance;
import com.whynotit.MobiTaxi.Models.Localisation;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Harzallah on 13/04/2016.
 */
public class MapFragment extends Fragment  implements OnMapReadyCallback {

    private static int NUMBER_OF_TAXI_TO_FIND = 10;
    private static int MAX_DISTANCE_TO_CALL_TAXI = 25;
    private static MainActivity activity;
    private static AlertDialog dialog;
    private static CountDownTimer countDownTimer;
    private GoogleMap googleMap;
    final LatLng mainPoint = new LatLng(35.8340561 , 10.5984772);
    private List<Marker> mMarkers = new ArrayList<>();
    private Marker userMarker;
    private static FloatingActionButton findTaxi;
    private List<Distance> distances = new ArrayList<>();
    private static CoordinatorLayout rootLayout;
    private static View view;
    private SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;

        //return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!activity.isTimeBetweenGPSUpdatesNoraml()) {
            activity.setTimeBetweenGPSUpdatesToNormal();
        }

        activity.toggleDrawerUse(true);
        activity.getSupportActionBar().setTitle("Mobi Taxi");

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);


        findTaxi = (FloatingActionButton) view.findViewById(R.id.find_taxi);
        findTaxi.setEnabled(false);
        findTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findTaxi.setVisibility(View.INVISIBLE);
                findMostCloseTaxis ();
            }
        });
        rootLayout = (CoordinatorLayout) view.findViewById(R.id.root_layout);

    }

    private void findMostCloseTaxis() {
        showWaitingDialog();
        List<String> idTaxis = new ArrayList<>();
        for (int i = 0; i< distances.size() && i < NUMBER_OF_TAXI_TO_FIND; i++)
        {
            Distance distance = distances.get(i);
            if (distance.getDistanceToMe() < MAX_DISTANCE_TO_CALL_TAXI) {
                idTaxis.add(distances.get(i).getIdTaxi());
            }
        }

        if (idTaxis.size() == 0)
        {
            dismissWaitingDialog();
            Snackbar.make(rootLayout,"Aucun taxi proche de vous pour le moment. Réessayer dans quelques minutes",Snackbar.LENGTH_LONG)
                    .show();
        } else {
            //Toast.makeText(activity,"CONTACT DES TAXIS : "+idTaxis,Toast.LENGTH_LONG).show();
            Utils.insertTaxiRequest(idTaxis, rootLayout);
        }
    }

    public static void showFindTaxiButton ()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findTaxi.setVisibility(View.VISIBLE);
            }
        });

    }

    public static void showWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_waiting_for_taxi_response);

        dialog = builder.create();
        // display dialog
        dialog.show();
    }



    public static AlertDialog getDialog() {
        return dialog;
    }

    public static void dismissWaitingDialog()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                dialog.dismiss();
                findTaxi.setVisibility(View.VISIBLE);
            }
        });
    }

    public void stopCountDown ()
    {
        countDownTimer.cancel();
    }

    public static void startCountDown() {

        //showWaitingDialog();
        if (getDialog()!=null) {
            getDialog().findViewById(R.id.recherche_en_cours).setVisibility(View.GONE);
            getDialog().findViewById(R.id.attente_de_reponse).setVisibility(View.VISIBLE);
            ((TextView) getDialog().findViewById(R.id.message)).setText("Patientez pendant qu'un taxi accepte la course...");
        }


        countDownTimer = new CountDownTimer(22000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Utils.checkTaxistResponse(activity.getIdRequest());
            }
        }.start();

    }

    public static void aucuneReponseACetteCourse () {
        dismissWaitingDialog();
        Snackbar.make(rootLayout,"Aucun Taxi libre. reéssayez ou appelez un Taxi directement.",Snackbar.LENGTH_LONG)
                .show();
    }

    public static View getRootLayout ()
    {
        return rootLayout;
    }

    public static double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = mMarkers.indexOf(marker);
                if (position != -1)
                {
                    activity.setSelectedTaxi(activity.localisations.get(position).getIdTaxi());
                    activity.showTaxiInformationFragment();
                    Utils.loadTaxistInformationsFromDatabase(activity.getSelectedTaxi());
                }

                Log.e("Marker selected",position +": "+ marker.toString());
                return false;
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mainPoint, 13));


        setupMap();

        /*if (searchedCity != null) {
            LatLng g = getLocationFromAddress(searchedCity);


            if (g != null) {
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(g, 13));
            }

            map.addMarker(new MarkerOptions()
                    .title("Restaurant")
                    .snippet("HALAL")
                    .position(g));
            //.setIcon(icon);
        }*/
    }

    private void setupMap() {


        //Ajout des Markers la premiere fois
        userMarker = addUserMarker();
        addTaxiMarkers();


    }

    private void addTaxiMarkers() {

        for (Marker marker : mMarkers)
        {
            deleteMarker(marker);
        }

        mMarkers.clear();

        if (activity.localisations != null ) {

            for (Localisation localisation : activity.localisations) {
                mMarkers.add(addMarker(localisation));
            }
            UpdateDistancesList();

            refreshMapView ();

        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.showNoTaxiDialog();
                }
            });

        }
    }

    private void refreshMapView() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //Add user marker
        builder.include(userMarker.getPosition());

        for (Marker marker : mMarkers) {
            builder.include(marker.getPosition());
        }
        final LatLngBounds bounds = builder.build();

        int padding = 200; // offset from edges of the map in pixels
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(cu);
            }
        });

        findTaxi.setEnabled(true);

    }

    private void UpdateDistancesList() {
        distances.clear();

        for (Localisation localisation : activity.localisations)
        {
            distances.add(new Distance(localisation.getIdTaxi(),calculationByDistance(activity.getUserLatLong(),localisation.getLatLong())));
        }

        trierDistancesList();
        showDistancesList ();
    }

    private void trierDistancesList() {
        Collections.sort(distances, new Comparator<Distance>() {
            @Override
            public int compare(Distance lhs, Distance rhs) {
                return lhs.getDistanceToMe() > rhs.getDistanceToMe() ? 0 : -1;
            }
        });
    }

    private void showDistancesList() {
        for (Distance distance : distances)
        {
            Log.e("CCCCCCCCCc",distance.toString());
        }
    }


    private Marker addUserMarker() {
        Marker marker = googleMap
                .addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.client_marker))
                        .position(activity.getUserLatLong())
                        .title("Moi"));
        return marker;
    }

    public Marker addMarker (Localisation localisation)
    {

        Marker marker = googleMap
                .addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_marker))
                        .position(localisation.getLatLong())
                        .title("Taxi"));


        return marker;
    }

    public void deleteMarker (Marker marker)
    {
        if (marker != null)
        marker.remove();
    }

    public void updateUserPosition() {
        deleteMarker(userMarker);
        userMarker = addUserMarker();
        Toast.makeText(activity,"User location updated",Toast.LENGTH_LONG).show();
    }

    public void updateTaxisPosition()
    {
        updateUserPosition();
        addTaxiMarkers();
        refreshMapView();
        Toast.makeText(activity,"Taxis location updated",Toast.LENGTH_LONG).show();
    }




    @Override
    public void onStop() {
        Log.e("STOP","ON STOP");
        deleteMarker(userMarker);
        deleteTaxiMarkers();
        super.onStop();
    }

    private void deleteTaxiMarkers() {
        for (Marker marker : mMarkers)
        {
            deleteMarker(marker);
        }
    }

    public void removeAllMarkers() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deleteMarker(userMarker);
                deleteTaxiMarkers();
            }
        });

    }
}