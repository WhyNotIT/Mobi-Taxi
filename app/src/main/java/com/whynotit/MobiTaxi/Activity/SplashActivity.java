package com.whynotit.MobiTaxi.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.squareup.okhttp.OkHttpClient;
import com.whynotit.MobiTaxi.Models.User;
import com.whynotit.MobiTaxi.Dialog.DialogConnexion;
import com.whynotit.MobiTaxi.Dialog.DialogInscription;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Harzallah on 14/04/2016.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_LEVEL = "level";
    public CallbackManager callbackManager;
    private OkHttpClient httpClient;
    private LinearLayout connectLayout;
    private CoordinatorLayout rootLayout;
    private LinearLayout loadingLayout;
    private SharedPreferences sharedPreferences;
    private JSONArray userPosts;
    private LoginButton loginButton;
    private TextView splashMessage;

    public static final String TOKEN = "NbTyy968vVON-d3oprebTobeBS89Mfqs";
    private Snackbar snackbar;
    private static SplashActivity activity;
    private LocationManager locationManager = null;
    public Location mLocation = new Location(LocationManager.GPS_PROVIDER);

    public boolean isPositionFound = false, isTaxisFound = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(final Location location) {
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
                    mLocation = location;
                    locationManager.removeUpdates(locationListenerGPS);
                    isPositionFound = true;
                    if (isTaxisFound)
                    {
                        startMainActivity();
                    }
                    //showLoadingAnimation("Recherche de taxi en cours...");
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
    private String localisations;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_splash);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        activity = this;
        callbackManager = CallbackManager.Factory.create();
        httpClient = Utils.getHttpClient(this);
        connectLayout = (LinearLayout) findViewById(R.id.connect_layout);
        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions((Arrays.asList("public_profile", "user_friends", "email")));
        splashMessage = (TextView) findViewById(R.id.splash_message);


        //setupPubNotifications();
        //startMainActivity();


    }

    private void setupPubNotifications() {

        Intent myIntent = new Intent(SplashActivity.this , PubActivity.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(getApplication(), 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60*1000 , pendingIntent);  //set repeating every 24 hours
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            buildAlertMessageNoGps();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        locationManager.removeUpdates(locationListenerGPS);
    }

    public void initUI() {

        if (snackbar != null)
            snackbar.dismiss();
            connectLayout.setVisibility(View.GONE);

        if (!Utils.isNetworkAvailable()) {
            connectLayout.setVisibility(View.GONE);
            hideLoadingAnimation();
            snackbar.make(rootLayout, "Verifier votre connexion!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("REESSAYER", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initUI();
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .show();

        } else if (Utils.isConnected(this)) {
            if (checkLocation()) {
                Utils.loadLocationsFromDB(activity,rootLayout);
                detectGpsLocation();
            }
        } else {

            if (AccessToken.getCurrentAccessToken() != null)
                getFacebookProfileData(AccessToken.getCurrentAccessToken());
            else {
                connectLayout.setVisibility(View.VISIBLE);
                hideLoadingAnimation();
                Button inscription = (Button) findViewById(R.id.inscription);

                Button connexion = (Button) findViewById(R.id.connexion);

                connexion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogConnexion dialogConnexion = new DialogConnexion(activity);
                        dialogConnexion.show();
                    }
                });


                inscription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInscription dialogInscription = new DialogInscription(activity);
                        dialogInscription.show();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //getFacebookProfileData(AccessToken.getCurrentAccessToken());
    }

    public void getFacebookProfileData(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        User applicationUser;
                        try {
                            connectLayout.setVisibility(View.GONE);
                            showLoadingAnimation("Configuration de votre compte...");
                            try {
                                applicationUser = new User(object.getString("id"), object.getString("email"), object.getString("first_name"), object.getString("last_name"), object.getString("gender"), "01/01/1900");
                            } catch (JSONException e) {
                                applicationUser = new User(object.getString("id"), "empty", object.getString("first_name"), object.getString("last_name"), object.getString("gender"), "01/01/1900");

                            }
                            applicationUser.setPassword("facebook");
                            applicationUser.setGender("empty");
                            Log.e("RRRRRRRRRRRRRRr",applicationUser.toString());
                            // TODO: 16/04/2016
                            Utils.insertUser(activity,null,applicationUser);

                            //loadUserPostsFromDB(object.getString("id"));
                        } catch (JSONException e) {
                            connectLayout.setVisibility(View.VISIBLE);
                            hideLoadingAnimation();
                            Snackbar.make(rootLayout, "You must accept all permissions!", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Accept", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getMorePermissions();
                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            connectLayout.setVisibility(View.VISIBLE);
                            hideLoadingAnimation();
                            Snackbar.make(rootLayout, "Unable to connect. try again!", Snackbar.LENGTH_LONG).show();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getMorePermissions() {
        LoginManager.getInstance().logInWithReadPermissions(
                activity,
                Arrays.asList("public_profile", "user_friends", "email"));

    }

    private void hideLoadingAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void showLoadingAnimation(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                splashMessage.setText(message);
                loadingLayout.setVisibility(View.VISIBLE);
            }
        });

    }


    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("localisations",localisations);
        intent.putExtra("long", mLocation.getLongitude());
        intent.putExtra("lat", mLocation.getLatitude());
        removeGpsUpdates();
        startActivity(intent);
        finish();
    }

    public void detectGpsLocation() {
         if (countDownTimer != null) {
             countDownTimer.cancel();
         }

        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Snackbar.make(rootLayout,"Localisation lente? Sortez si vous êtes dans un immeuble ou deplacez vous.",Snackbar.LENGTH_INDEFINITE)
                        .show();

            }
        }.start();

        showLoadingAnimation("Localisation en cours...");
        Location location = new Location(locationManager.GPS_PROVIDER);
        //location.setLatitude(35.8550561);
        //location.setLongitude(10.6004772);
        //startMainActivity(location);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
        //Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
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
                        Snackbar.make(rootLayout,"Activez votre GPS pour utiliser cette application",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Activer", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        buildAlertMessageNoGps();
                                    }
                                });
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void setLocalisations(String localisations) {
        this.localisations = localisations;
    }

    public static SplashActivity getActivity() {
        return activity;
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
        locationManager.removeUpdates(locationListenerGPS);

    }

}

