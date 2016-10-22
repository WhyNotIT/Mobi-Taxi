package com.whynotit.MobiTaxi.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.whynotit.MobiTaxi.Activity.SplashActivity;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;


/**
 * Created by Harzallah on 07/05/2016.
 */
public class DialogConnexion extends Dialog {

    public static final String TAG = "CR";

    public SplashActivity activity;
    public Button connexion;

    private EditText telephone,password;
    public LinearLayout rootLayout;
    private DialogConnexion dialogConnexion;
    public ProgressBar progressBar;

    public DialogConnexion(SplashActivity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_connexion);
        setCancelable(true);
        dialogConnexion = this;

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        telephone = (EditText) findViewById(R.id.telephone);
        password = (EditText) findViewById(R.id.password);


        progressBar = (ProgressBar) findViewById(R.id.progress);

        connexion = (Button) findViewById(R.id.connexion);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isValidPhone(telephone.getText().toString()))
                {
                    showSnackBar("Téléphone invalide!");
                    return;
                }

                if (!isValidPassword(password.getText().toString()))
                {
                    showSnackBar("Mot de passe est trop court! (au moins 5 charactères)");
                    return;
                }



                if (Utils.isNetworkAvailable()) {
                    showProgressBar();
                    Utils.connectUser(dialogConnexion,telephone.getText().toString(),password.getText().toString());
                }

            }
        });
    }

    public void showProgressBar ()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connexion.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideProgressBar ()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connexion.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private boolean isValidMatricule(String matricule) {
        if (!matricule.contains(":"))
        {
            return false;
        } else {
            String[] tab = matricule.split(":");
            if (tab.length > 2)
            {
                return false;
            }
            try {
                int nb = Integer.valueOf(tab[0]);
                nb = Integer.valueOf(tab[1]);
                return true;
            } catch (NumberFormatException e)
            {
                return false;
            }
        }
    }

    private boolean isValidInt(String numero) {
        try {
            int number = Integer.valueOf(numero);
            return numero.length() > 0 ? true : false;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    private boolean isValidPhone(String phone) {
        try {
            int number = Integer.valueOf(phone);
            return phone.length() == 8 ? true : false;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    public void showSnackBar(String message) {
        Snackbar.make(rootLayout,message, Snackbar.LENGTH_LONG).show();
    }

    public boolean isValidString(String s) {
        return (s.length() > 2);
    }

    public boolean isValidEmail(String email) {
        return (email.length() > 6 && email.contains("@") && email.contains("."));
    }

    public boolean isValidPassword(String password) {
        return (password.length() > 4);
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }
}