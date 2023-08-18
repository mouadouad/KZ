package com.mouadouad0.kz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

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
        banner();
        
        create = new Button(this);
        join = new Button(this);

        create.setBackgroundResource(R.drawable.c_lobby_button);
        join.setBackgroundResource(R.drawable.j_lobby_button);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setx(300), Shared.sety(200));
        addContentView(create, layoutParams2);

        create.setY(Shared.sety(200));
        create.setX(Shared.setx(400));


        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(300), Shared.sety(200));
        addContentView(join, layoutParams3);

        join.setY(Shared.sety(500));
        join.setX(Shared.setx(400));


        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null) {
            internetError();
        }


        create.setOnClickListener(view -> {

            if (activeNetwork == null) {
                internetError();
            } else {
                Intent i = new Intent(MultiMode.this, Create.class);
                startActivity(i);
            }

        });

        join.setOnClickListener(view -> {

            if (activeNetwork == null) {
                internetError();
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
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(100), Shared.sety(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.sety(50));
        back.setX(Shared.setx(50));
        back.setZ(30);

        back.setOnClickListener(view -> {

            Intent intent = new Intent(MultiMode.this, Start.class);
            startActivity(intent);
            finish();
            mInterstitialAd.show();
        });

    }

    public void internetError() {

        final RelativeLayout message_box;
        Button ok_button;

        final FrameLayout dim_layout;

        //MAKE THE SCREEN DIM
        Resources res = getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.dim, null);
        dim_layout = new FrameLayout(this);
        dim_layout.setForeground(shape);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (Start.width), (int) (Start.height));
        addContentView(dim_layout, layoutParams1);
        dim_layout.getForeground().setAlpha(200);
        dim_layout.setZ(20);

        //SETTING THE ERROR MESSAGE
        message_box = new RelativeLayout(this);
        ok_button = new Button(this);

        //BUTTON
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(200), Shared.sety(100));
        message_box.addView(ok_button, layoutParams3);
        ok_button.setBackgroundResource(R.drawable.okay_button);
        ok_button.setX(Shared.setx(250));
        ok_button.setY(Shared.sety(300 - 100 - 20));

        //BOX
        message_box.setBackgroundResource(R.drawable.internet_box);
        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setx(700), Shared.sety(300));
        addContentView(message_box, layoutParams4);
        message_box.setX(Shared.setx((Start.width - 700) / 2));
        message_box.setY(Shared.sety((Start.height - 300) / 2));
        message_box.setZ(30);

        //ONCLICK BUTTON
        ok_button.setOnClickListener(view -> {
            message_box.setVisibility(View.GONE);
            dim_layout.setVisibility(View.GONE);

        });

    }

    public void banner() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) Start.width, (int) Start.height - getStatusBarHeight());
        addContentView(layout, layoutParams1);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView, layoutParams);

        MobileAds.initialize(this, "ca-app-pub-3922358669029120~3985187056");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
