package com.whynotit.MobiTaxi.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.whynotit.MobiTaxi.Activity.SplashActivity;
import com.whynotit.MobiTaxi.Models.User;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

/**
 * Created by Harzallah on 22/10/2015.
 */
public class DialogInscription extends Dialog {

    public static final String TAG = "CR";

    public SplashActivity activity;
    public Button inscription;

    private EditText name,lastname,telephone,password;
    private DatePicker birthday;
    private RadioButton monsieur,madame;
    public NestedScrollView rootLayout;
    private DialogInscription dialogInscription;
    public ProgressBar progressBar;

    public DialogInscription(SplashActivity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_inscription);
        setCancelable(true);
        dialogInscription = this;

        rootLayout = (NestedScrollView) findViewById(R.id.root_layout);
        name = (EditText) findViewById(R.id.name);
        lastname = (EditText) findViewById(R.id.last_name);
        telephone = (EditText) findViewById(R.id.telephone);
        password = (EditText) findViewById(R.id.password);
        monsieur = (RadioButton) findViewById(R.id.monsieur);
        monsieur.setChecked(true);
        madame = (RadioButton) findViewById(R.id.madame);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        inscription = (Button) findViewById(R.id.inscription);
        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidString(name.getText().toString()))
                {
                    showSnackBar("Prénom est tros court!");
                    return;
                }

                if (!isValidString(lastname.getText().toString()))
                {
                    showSnackBar("Nom est tros court!");
                    return;
                }

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

                User user = new User();
                user.setName(name.getText().toString());
                user.setLastName(lastname.getText().toString());
                user.setTelephone(telephone.getText().toString());
                user.setPassword(password.getText().toString());
                if (madame.isChecked())
                    user.setGender("Madame");
                else
                    user.setGender("Monsieur");

                if (Utils.isNetworkAvailable()) {
                    inscription.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    Utils.insertUser(activity,dialogInscription, user);
                }

            }
        });
    }

    public void showSnackBar(String message) {
        Snackbar.make(rootLayout,message,Snackbar.LENGTH_LONG).show();
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

    private boolean isValidPhone(String phone) {
        try {
            int number = Integer.valueOf(phone);
            return phone.length() == 8 ? true : false;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }
}