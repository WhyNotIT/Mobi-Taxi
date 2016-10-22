package com.whynotit.MobiTaxi.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynotit.MobiTaxi.R;

/**
 * Created by Harzallah on 29/06/2016.
 */
public class TutorielActivity extends Activity {

    private ImageView imageTuto;
    private Button suivant;
    private TextView message;
    private int position=0;
    private String [] messages = {"Comment fonctionne Mobi Taxi?"
            , "Choisissez le plus proche Taxi de vous."
            , "Consultez son profil."
            , "Appelez le pour commander une course."
            , "Ou laissez l'application choisir le Taxi le plus proche de vous."
            , "Suivez l'arrivée de votre Taxi en direct sur la carte."};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriel);


        imageTuto = (ImageView) findViewById(R.id.tuto_image);
        suivant = (Button) findViewById(R.id.tuto_suivant);
        message = (TextView) findViewById(R.id.tuto_message);

        imageTuto.setImageResource(R.drawable.tuto0);
        message.setText(messages[position]);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (position) {
                    case 0 : {
                        imageTuto.setImageResource(R.drawable.tuto1);
                        message.setText(messages[++position]);
                        break;
                    }
                    case 1 : {
                        imageTuto.setImageResource(R.drawable.tuto2);
                        message.setText(messages[++position]);
                        break;
                    }
                    case 2 : {
                        imageTuto.setImageResource(R.drawable.tuto3);
                        message.setText(messages[++position]);
                        break;
                    }
                    case 3 : {
                        imageTuto.setImageResource(R.drawable.tuto4);
                        message.setText(messages[++position]);
                        break;
                    }
                    case 4 : {
                        imageTuto.setImageResource(R.drawable.tuto5);
                        message.setText(messages[++position]);
                        suivant.setText("Terminer");
                        break;
                    }
                    default: {
                        finish();
                    }

                }


            }
        });
    }
}

