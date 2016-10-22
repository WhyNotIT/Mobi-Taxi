package com.whynotit.MobiTaxi.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.whynotit.MobiTaxi.Models.Avatar;
import com.whynotit.MobiTaxi.Models.Localisation;
import com.whynotit.MobiTaxi.Models.Taxist;
import com.whynotit.MobiTaxi.Models.User;
import com.whynotit.MobiTaxi.Dialog.DialogPublicite;
import com.whynotit.MobiTaxi.Drawer.DrawerItemClickListener;
import com.whynotit.MobiTaxi.Drawer.DrawerItemCustomAdapter;
import com.whynotit.MobiTaxi.Drawer.ObjectDrawerItem;
import com.whynotit.MobiTaxi.Fragment.IndicationFragment;
import com.whynotit.MobiTaxi.Fragment.MapFragment;
import com.whynotit.MobiTaxi.Fragment.TaxiInformationFragment;
import com.whynotit.MobiTaxi.GCM.QuickstartPreferences;
import com.whynotit.MobiTaxi.GCM.RegistrationIntentService;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static MainActivity activity;
    public MapFragment mapFragment;
    private Toolbar toolbar;
    private LinearLayout rootLayout;
    private User applicationUser;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationDrawerItemTitles;
    private RelativeLayout mDrawer;
    private ListView mDrawerListView;

    private FragmentTransaction ft;
    public LatLng userLocation;
    private JSONArray localisationsArray;
    public List<Localisation> localisations;
    private Localisation comingTaxi;
    public IndicationFragment indicationFragment;
    private String selectedTaxi;
    private TaxiInformationFragment taxiInformationFragment;
    private View.OnClickListener mDrawerListner;
    private android.support.v7.app.AlertDialog dialog;
    private boolean backFromIndicationFragment = false;
    private boolean resumed;
    private AudioManager mAudioManager;
    private float mStreamVolume;
    private SoundPool mSoundPool;
    private int mSoundID;
    private String idRequest;

    public Localisation getComingTaxi() {
        return comingTaxi;
    }

    public void setComingTaxi(final Localisation comingTaxi) {

        if (comingTaxi != null ) {
            this.comingTaxi = comingTaxi;
        } else {
            showToast(-1,"Le Taxi est introuvable! Appelez le.");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (indicationFragment != null && activity.comingTaxi != null) {
                    if (indicationFragment.isVisible()) {
                        indicationFragment.updateMapViewMarkers();
                    } else if (indicationFragment.isHidden() && activity.getUserLatLong() != null && activity.getComingTaxi().getLatLong() != null){
                        indicationFragment.calculerTempsAttente();
                    }
                }


            }
        });


    }

    //GCM
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9200;


    //Localisation
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    private static final long NORMAL_TIME_BW_UPDATES = 1000 * 45; // 60 secondes
    private static final long FAST_TIME_BW_UPDATES = 1000 * 10; // 10 secondes
    // The time between updates in milliseconds
    private static long TIME_BW_UPDATES = 0;

    private LocationManager locationManager = null;

    public void setTimeBetweenGPSUpdatesToFast() {
        Log.e("GPSSS","FAST GPS");
        TIME_BW_UPDATES = FAST_TIME_BW_UPDATES;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupGpsDetector();
            }
        });

    }

    public void setTimeBetweenGPSUpdatesToNormal() {
        Log.e("GPSSS","NORMAL GPS");
        TIME_BW_UPDATES = NORMAL_TIME_BW_UPDATES;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupGpsDetector();
            }
        });

    }

    public boolean isTimeBetweenGPSUpdatesNoraml() {
        return TIME_BW_UPDATES == NORMAL_TIME_BW_UPDATES ? true : false;
    }



    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(final Location location) {

            Utils.updateUserPosition(location);
            //Reload locations of all Taxis or just the coming taxi
            if (indicationFragment != null && indicationFragment.isVisible() && comingTaxi.getIdTaxi() != null) {
                Utils.loadTaxiLocationFromDB(comingTaxi.getIdTaxi());
            } else {
                Utils.reloadLocationsFromDB(activity, rootLayout);
            }

            //Toast.makeText(activity, location.toString(), Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    //notifyUserPositionChanged();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private ImageView tshirt;
    private List<Drawable> tshirtList = new ArrayList<>();
    private String idCourse;
    private String numeroTaxi;
    private String taxiPhone;
    private static List<Avatar> avatarsList, indicationsList = new ArrayList<>();
    private Avatar avatarSelectionner;

    public static List<Avatar> getIndicationsList() {
        return indicationsList;
    }

    public static void setIndicationsList(List<Avatar> indicationsList) {
        MainActivity.indicationsList = indicationsList;
    }

    public static void setAvatarsList(List<Avatar> avatarsList) {
        MainActivity.avatarsList = avatarsList;
    }

    public void notifyUserPositionChanged() {
        if (mapFragment.isAdded())
            mapFragment.updateUserPosition();
        else if (indicationFragment.isAdded()) {
            indicationFragment.updateUserPosition();
        }
    }

    public static MainActivity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setTimeBetweenGPSUpdatesToNormal();


        applicationUser = Utils.loadUserFromSharedPreferences(activity);

        // Save user to GCM service
        saveUserToGCM();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupDrawer();


        // Begin the transaction
        ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        mapFragment = new MapFragment();
        ft.replace(R.id.fragment_view, mapFragment);
        // Append this transaction to the backstack
        //ft.addToBackStack("main");
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();


        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        userLocation = new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("long", 0));

        String locs = getIntent().getStringExtra("localisations");

        Log.e("EXTRA",locs+"");
        //Setup locations on new Thread
        localisations = new ArrayList<>();
        setLocalisations(locs);


        checkFirstLunch();
        //Utils.loadTaxiLocationFromDB("1");
        //Utils.loadAvatarsFromDB(activity,rootLayout);

    }

    private void checkFirstLunch() {
        if (Utils.isFirstLunch()) {
            startActivity(new Intent(activity,TutorielActivity.class));
        }
    }

    private void setupGpsDetector() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (locationManager != null && locationListenerGPS != null) {
            locationManager.removeUpdates(locationListenerGPS);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);

    }

    public User getApplicationUser() {
        return applicationUser;
    }

    private void saveUserToGCM() {

        //GCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                /*if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }*/
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


    }

    private void setupDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerListView = (ListView) findViewById(R.id.drawer_list_view);
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener(activity,mDrawer, mDrawerListView, mDrawerLayout));

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[4];

        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_acceuil, "Acceuil");
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_rules, "Conditions générales");
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_help, "Tutoriel");
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_info, "A propos");
        /*drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_mes_rappels, "Mes rappels");
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_ajouter_rappel, "Ajouter un rappel");
        drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_choisir_filiere, "Choisir une filiére");
        drawerItem[5] = new ObjectDrawerItem(R.drawable.clubs, "ISSAT SO clubs");
        drawerItem[6] = new ObjectDrawerItem(R.drawable.ic_maj, "Mettre à jour l'emploi du temps");
        drawerItem[7] = new ObjectDrawerItem(R.drawable.ic_noter, "Noter ISSAT SO");*/

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
        mDrawerListView.setAdapter(adapter);

        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
        //       GravityCompat.START);

        // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.openDrawer,
                R.string.closeDrawer) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();

        //// Save the default listener after setting everything else up to make the back button in toolbar work work work ;)
        mDrawerListner = mDrawerToggle.getToolbarNavigationClickListener();
    }

    // Tells the toolbar+drawer to switch to the up button or switch back to the normal drawer
    public void toggleDrawerUse(boolean useDrawer) {
        // Enable/Disable the icon being used by the drawer
        mDrawerToggle.setDrawerIndicatorEnabled(useDrawer);

        // Switch between the listeners as necessary
        if (useDrawer)
            mDrawerToggle.setToolbarNavigationClickListener(mDrawerListner);
        else
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public LatLng getUserLatLong() {
        return userLocation;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;



        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));


        /**  FRAGMENT INDICATION   **/
        //showFragmentIndication();


        //AUDIO
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mStreamVolume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // make a new SoundPool, allowing up to 10 streams
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        // set a SoundPool OnLoadCompletedListener that calls setupGestureDetector()
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundPool.play(mSoundID, mStreamVolume, mStreamVolume, 1, 0, 1f);
            }
        });



    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            buildAlertMessageNoGps();
        return isLocationEnabled();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Veuillez activer le service de localisation ( GPS ) dans vos paramètres afin de permettre votre géolocalisation.")
                .setCancelable(false)
                .setPositiveButton("ACTIVER", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        Snackbar.make(rootLayout, "Activez votre GPS pour utiliser cette application", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Activer", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        buildAlertMessageNoGps();
                                    }
                                });
                    }
                });
        final AlertDialog alert = builder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                alert.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
                alert.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(activity.getResources().getColor(R.color.colorSecondaryText));
                alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(activity.getResources().getColor(R.color.colorSecondaryText));
            }
        });


        alert.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

        if (isTimeBetweenGPSUpdatesNoraml()) {
            removeGpsUpdates();
        }

        resumed = false;
        // Release all SoundPool resources
        mSoundPool.unload(mSoundID);
        mSoundPool.release();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void removeGpsUpdates () {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.e("GPSSS","REMOVE");
            TIME_BW_UPDATES = 0;
            locationManager.removeUpdates(locationListenerGPS);

    }

    public void setLocalisations(String locs) {

        try {
            if (locs != null) {
                localisationsArray = new JSONArray(locs);

                if (localisationsArray.length() > 0) {

                    Gson gson = new Gson();
                    localisations.clear();

                    for (int i = 0; i < localisationsArray.length(); i++) {
                        localisations.add(gson.fromJson(localisationsArray.get(i).toString(), Localisation.class));
                    }


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyTaxisPositionChanged();
                        }
                    });

                } else if (mapFragment.isAdded()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNoTaxiDialog();
                        }
                    });
                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNoTaxiDialog();
                    }
                });
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }
}

    public void showNoTaxiDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_no_taxi);
        builder.setPositiveButton("QUITTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });


        dialog = builder.create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(activity.getResources().getColor(R.color.colorSecondaryText));
            }
        });

        // display dialog
        dialog.show();

    }

    private void notifyTaxisPositionChanged() {
        if (mapFragment.isAdded())
        mapFragment.updateTaxisPosition();
    }

    /**     ***** GCM ******** **/
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("XXXXXXXXXXX", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public ImageView getTshirt() {
        return tshirt;
    }

    public void setTshirt(ImageView tshirt) {
        this.tshirt = tshirt;
    }

    public void showSnackBar(String message) {
        Snackbar.make(rootLayout,message,Snackbar.LENGTH_LONG).show();
    }

    public List<Avatar> getAvatarsList() {
        return avatarsList;
    }

    public List<Drawable> getTshirtList() {
        return tshirtList;
    }

    public void showFragmentIndication() {
        mapFragment.stopCountDown();
        mapFragment.removeAllMarkers();
        // Begin the transaction
        ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
        // Replace the contents of the container with the new fragment
        indicationFragment = new IndicationFragment();
        ft.replace(R.id.fragment_view, indicationFragment);
        // Append this transaction to the backstack
        ft.addToBackStack("indication");
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }


    public void setIdCourse(String idCourse) {
        this.idCourse = idCourse;
    }

    public String getIdCourse() {
        return idCourse;
    }

    public void setNumeroTaxi(String numeroTaxi) {
        this.numeroTaxi = numeroTaxi;
    }

    public String getNumeroTaxi() {
        return numeroTaxi;
    }

    public void setTaxiPhone(String taxiPhone) {
        this.taxiPhone = taxiPhone;
    }


    public void setAvatarSelectionner(Avatar avatarSelectionner) {
        this.avatarSelectionner = avatarSelectionner;
    }

    public Avatar getAvatarSelectionner() {
        return avatarSelectionner;
    }

    public JSONArray getIndicationsListToJson() {
        JSONArray indications = new JSONArray();

        for (Avatar avatar : indicationsList)
        {
            indications.put(avatar.toString());
        }

        return indications;
    }

    public String getTaxiPhone() {
        return taxiPhone;
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }

    public void setSelectedTaxi(String selectedTaxi) {
        this.selectedTaxi = selectedTaxi;
    }

    public void showTaxiInformationFragment() {
        // Begin the transaction
        ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
        // Replace the contents of the container with the new fragment
        taxiInformationFragment = new TaxiInformationFragment();
        ft.replace(R.id.fragment_view, taxiInformationFragment);
        // Append this transaction to the backstack
        ft.addToBackStack("taxiInformation");
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

    }

    public String getSelectedTaxi() {
        return selectedTaxi;
    }

    public void updateTaxistInformationsFragment(final Taxist taxist) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                taxiInformationFragment.updateTaxisInformation(taxist);
            }
        });

    }

    public void showFinirCourseDialog ()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_finir_course);

        dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void dismissFinirCourseDialog ()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    public void showFinirCourseWithRateDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_finir_with_rate_course);

        dialog = builder.create();

        dialog.show();

        final TextView message = (TextView) dialog.findViewById(R.id.message);
        final RatingBar note = (RatingBar) dialog.findViewById(R.id.course_note);
        final Button confirmer = (Button) dialog.findViewById(R.id.confirm_button);
        final LinearLayout thanks = (LinearLayout) dialog.findViewById(R.id.thanks);
        // display dialog

        confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setVisibility(View.GONE);
                thanks.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
                confirmer.setVisibility(View.GONE);

                new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        dialog.dismiss();
                        activity.finish();
                    }
                }.start();

                //loadingIndicatorView.setVisibility(View.GONE);
                Utils.insertStatus(1,note.getRating());
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (backFromIndicationFragment)
        {
            Toast.makeText(activity,"Course ANNULEE!",Toast.LENGTH_LONG).show();
            Utils.insertStatus(0,-1);
            backFromIndicationFragment = false;
        }
    }

    public void setBackFromIndicationFragment(boolean backFromIndicationFragment) {
        this.backFromIndicationFragment = backFromIndicationFragment;
    }

    public boolean isMainActivityResumed() {
        return resumed;
    }

    public void showToast(final int status, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // get your custom_toast.xml ayout
                LayoutInflater inflater = getLayoutInflater();

                View layout = inflater.inflate(R.layout.custom_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_layout_id));

                ImageView logo = (ImageView) layout.findViewById(R.id.toast_image);

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                long[] pattern = {0, 500, 250, 500};
                v.vibrate(pattern,-1);

                if (status == 1) {
                    mSoundID = mSoundPool.load(activity, R.raw.positive, 1);
                    logo.setImageResource(R.drawable.found);
                } else if (status == 99){
                    mSoundID = mSoundPool.load(activity, R.raw.positive, 1);
                    logo.setImageResource(R.drawable.ic_phone);
                }
                else {
                    mSoundID = mSoundPool.load(activity, R.raw.negative, 1);
                    logo.setImageResource(R.drawable.not_found);
                }
                // set a message
                TextView text = (TextView) layout.findViewById(R.id.message);
                text.setText(message);

                // Toast...
                Toast toast = new Toast(activity);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

    }

    public void showDialogPub(String urlPub) {

        DialogPublicite dialogPublicite = new DialogPublicite(activity,urlPub);
        dialogPublicite.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.showToast(99,"Pensez à appeler votre Taxi pour confirmer votre course.");
            }
        });
        dialogPublicite.show();
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public String getIdRequest() {
        return idRequest;
    }
}
