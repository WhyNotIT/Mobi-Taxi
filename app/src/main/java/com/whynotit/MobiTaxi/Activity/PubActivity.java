package com.whynotit.MobiTaxi.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.whynotit.MobiTaxi.R;
import com.whynotit.MobiTaxi.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harzallah on 18/06/2016.
 */
public class PubActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);

        final PubActivity activity = this;

        ImageView imagePub = (ImageView) findViewById(R.id.image_pub);
        CircleImageView close = (CircleImageView) findViewById(R.id.close);

        String idPub = getIntent().getStringExtra("idPub");
        String urlPub = getIntent().getStringExtra("urlPub");

        Picasso.with(this)
                .load(urlPub)
                .fit()
                .noPlaceholder()
                .into(imagePub);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });


        Utils.updatePubNombreVuGCM(this,idPub);



    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
