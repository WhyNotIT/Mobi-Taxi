package com.whynotit.MobiTaxi.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Harzallah on 07/05/2016.
 */
public class DialogPublicite extends Dialog {

    public static final String TAG = "CR";

    public MainActivity activity;

    public LinearLayout rootLayout;
    private DialogPublicite dialogPublicite;
    private ImageView imagePub;
    private String urlPub;

    public DialogPublicite(MainActivity activity, String urlPub) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.urlPub = urlPub;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_publicite);
        setCancelable(true);
        dialogPublicite = this;

        imagePub = (ImageView) findViewById(R.id.image_pub);
        CircleImageView close = (CircleImageView) findViewById(R.id.close);

        Picasso.with(activity)
                .load(urlPub)
                .fit()
                .noPlaceholder()
                .into(imagePub);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPublicite.dismiss();
            }
        });

    }

       public LinearLayout getRootLayout() {
        return rootLayout;
    }

}