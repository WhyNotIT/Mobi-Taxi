package com.whynotit.MobiTaxi.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.Adapter.AvatarListAdapter;
import com.whynotit.MobiTaxi.Adapter.ColorListAdapter;
import com.whynotit.MobiTaxi.Adapter.IndicationListAdapter;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

/**
 * Created by Harzallah on 21/04/2016.
 */
public class IndicationFragment extends Fragment implements OnMapReadyCallback {

    private MainActivity activity;
    private RecyclerView colorList;
    private RecyclerView avatarList;
    private static RecyclerView indcationList;
    private ColorListAdapter mColorAdapter;
    private AvatarListAdapter mAvatarAdapter;
    private IndicationListAdapter mIndicationAdapter;
    private TextView tempsAttente;
    private FloatingActionButton sendIndications;
    private static CoordinatorLayout rootLayout;
    private ImageButton callTaxiButton;
    private GoogleMap googleMap;
    final LatLng mainPoint = new LatLng(35.8340561 , 10.5984772);
    private Marker userMarker;
    private Marker comingTaxiMarker;
    private LinearLayout conteneurTempsAttente;
    private Button taxiTrouve;
    private Button taxiAbscent;



    public static View getRootLayout() {
        return rootLayout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_indication, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = MainActivity.getActivity();

        rootLayout = (CoordinatorLayout) view.findViewById(R.id.root_layout);
        conteneurTempsAttente = (LinearLayout) view.findViewById(R.id.conteneur_temps_attente);
        callTaxiButton = (ImageButton) view.findViewById(R.id.call_taxi_button);
        taxiAbscent = (Button) view.findViewById(R.id.taxi_abscent);

        taxiTrouve = (Button) view.findViewById(R.id.taxi_trouve);
        taxiTrouve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showFinirCourseWithRateDialog();
            }
        });

        taxiAbscent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showFinirCourseDialog();
                Utils.insertStatus(-1,-1);
            }
        });
        taxiTrouve.setEnabled(false);

        callTaxiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + activity.getTaxiPhone()));
                startActivity(intent);
            }
        });

        tempsAttente = (TextView) view.findViewById(R.id.temps_attente);

        calculerTempsAttente ();

        com.google.android.gms.maps.SupportMapFragment mapFragment = (com.google.android.gms.maps.SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_indication);
        mapFragment.getMapAsync(this);

        /*
        indcationList = (RecyclerView) view.findViewById(R.id.indication_list);
        mIndicationAdapter = new IndicationListAdapter(activity);
        LinearLayoutManager indicationLayoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        indcationList.setLayoutManager(indicationLayoutManager);
        indcationList.setAdapter(mIndicationAdapter);


        mAvatarAdapter = new AvatarListAdapter(activity,mIndicationAdapter);
        LinearLayoutManager avatarLayoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        avatarList = (RecyclerView) view.findViewById(R.id.avatar_list);
        avatarList.setLayoutManager(avatarLayoutManager);
        avatarList.setAdapter(mAvatarAdapter);

        mColorAdapter = new ColorListAdapter(activity);
        LinearLayoutManager colorLayoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        colorList = (RecyclerView) view.findViewById(R.id.color_list);
        colorList.setLayoutManager(colorLayoutManager);
        colorList.setAdapter(mColorAdapter);

        Toast.makeText(activity,"Le taxi numero " + activity.getIdTaxi() +" est en route!",Toast.LENGTH_LONG).show();

        sendIndications = (FloatingActionButton) view.findViewById(R.id.send_indications);
        sendIndications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sendIndicationsToTaxi();
            }
        });

        */

        /** PUUUUUUUUUUUUUUUUB **/
        /**if (Utils.isNetworkAvailable()) {
            Utils.showPub(activity);
        } else { **/
            activity.showToast(99,"Pensez à appeler votre Taxi pour confirmer votre course.");


    }



    @Override
    public void onResume() {
        super.onResume();

        if (activity.isTimeBetweenGPSUpdatesNoraml()) {
            activity.setTimeBetweenGPSUpdatesToFast();
        }

        activity.setBackFromIndicationFragment(true);

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void calculerTempsAttente() {
        double distanceToMe = MapFragment.calculationByDistance(activity.getUserLatLong(),activity.getComingTaxi().getLatLong());
        double temps = distanceToMe * 1.5;
        if (temps >= 1) {

            hideIndicationButtons ();

            String msg = "Numéro Taxi : "
                    + "<b><font color=#F44336> "
                    + activity.getNumeroTaxi()
                    + "</font></b>"
                    + "<br> Temps d'attente : "
                    + "<b><font color=#F44336> "
                    + String.valueOf(Math.round(temps)+1)
                    + " minutes.</font></b>";


            tempsAttente.setText(Html.fromHtml(msg));
            taxiTrouve.setEnabled(false);
            taxiAbscent.setEnabled(false);

        } else {
            //conteneurTempsAttente.setBackgroundResource(R.color.white);

            showIndicationsButtons();

            String msg = "<b><font color=#F44336>"
                    + "Numéro Taxi : "
                    + activity.getNumeroTaxi()
                    + "<br>Temps d'attente : "
                    + "1 minute."
                    + "</font></b>";

            tempsAttente.setText(Html.fromHtml(msg));
            taxiTrouve.setEnabled(true);
            taxiAbscent.setEnabled(true);


            if (!activity.isMainActivityResumed()) {
                Utils.sendNotificationTaxiIsHere(activity, "Votre Taxi arrive dans quelques secondes.");
            }


        }

    }

    private void hideIndicationButtons() {
        taxiAbscent.setVisibility(View.GONE);
        taxiTrouve.setVisibility(View.GONE);
    }

    private void showIndicationsButtons () {
        taxiAbscent.setVisibility(View.VISIBLE);
        taxiTrouve.setVisibility(View.VISIBLE);
    }

    public static void scrollToLastAvatarAdded (int position)
    {
        indcationList.smoothScrollToPosition(position-1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mainPoint, 13));


        setupMap();

    }

    private Marker addUserMarker() {
        deleteMarker(userMarker);

        Marker marker = googleMap
                .addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.client_marker))
                        .position(activity.getUserLatLong())
                        .title("Moi"));
        return marker;
    }

    private void setupMap() {
        //Ajout des Markers la premiere fois
        userMarker = addUserMarker();
        comingTaxiMarker = addComingTaxiMarker();

        refreshMapView();
    }

    private Marker addComingTaxiMarker() {

        deleteMarker(comingTaxiMarker);

        Marker marker = googleMap
                .addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_marker))
                        .position(activity.getComingTaxi().getLatLong())
                        .title("Taxi numéro " + activity.getNumeroTaxi()));

        return marker;

    }

    private void refreshMapView() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //Add user marker
        builder.include(userMarker.getPosition());
        builder.include(comingTaxiMarker.getPosition());


        final LatLngBounds bounds = builder.build();

        int padding = 200; // offset from edges of the map in pixels
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(cu);
            }
        });

    }

    public void deleteMarker (Marker marker)
    {
        if (marker != null)
            marker.remove();
    }

    public void updateMapViewMarkers() {
        comingTaxiMarker = addComingTaxiMarker();
        userMarker = addUserMarker();
        refreshMapView();

        calculerTempsAttente();
    }

    public void updateUserPosition() {
        userMarker = addUserMarker();
        refreshMapView();

        calculerTempsAttente();
    }


}

