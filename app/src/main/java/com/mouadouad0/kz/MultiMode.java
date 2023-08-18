package com.mouadouad0.kz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MultiMode extends AppCompatActivity {

    Button create, join;
    public static String name;
    Button back;
    NetworkInfo activeNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.background(this, this);
        backButton();
        Shared.banner(this, this);
        
        create = new Button(this);
        join = new Button(this);

        create.setBackgroundResource(R.drawable.c_lobby_button);
        join.setBackgroundResource(R.drawable.j_lobby_button);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(200));
        addContentView(create, layoutParams2);

        create.setY(Shared.setY(200));
        create.setX(Shared.setX(400));


        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(200));
        addContentView(join, layoutParams3);

        join.setY(Shared.setY(500));
        join.setX(Shared.setX(400));


        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null) {
            Shared.internetError(this, this);
        }


        create.setOnClickListener(view -> {

            if (activeNetwork == null) {
                Shared.internetError(this, this);
            } else {
                Intent i = new Intent(MultiMode.this, Create.class);
                startActivity(i);
            }

        });

        join.setOnClickListener(view -> {

            if (activeNetwork == null) {
                Shared.internetError(this, this);
            } else {
                Intent i = new Intent(MultiMode.this, Join.class);
                startActivity(i);
            }


        });


    }

    public void backButton() {

        final InterstitialAd mInterstitialAd;
        //ADD INTERSTITIAL
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3922358669029120/9714465383");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));
        back.setZ(30);

        back.setOnClickListener(view -> {

            Intent intent = new Intent(MultiMode.this, Start.class);
            startActivity(intent);
            finish();
            mInterstitialAd.show();
        });

    }

    @Override
    public void onBackPressed() {
        InterstitialAd mInterstitialAd;
        //ADD INTERSTITIAL
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3922358669029120/9714465383");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        Intent intent = new Intent(MultiMode.this, Start.class);
        startActivity(intent);
        finish();
        mInterstitialAd.show();

    }
}
