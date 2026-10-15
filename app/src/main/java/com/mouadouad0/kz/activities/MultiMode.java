package com.mouadouad0.kz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;

public class MultiMode extends AppCompatActivity {

    Button create, join;
    public static String name;
    Button back;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.setUpScreen(this);
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
        if (!Shared.isOnline(this)) {
            Shared.internetError(this, this);
        }


        create.setOnClickListener(view -> {

            if (!Shared.isOnline(this)) {
                Shared.internetError(this, this);
            } else {
                Shared.signIn(() -> startActivity(new Intent(MultiMode.this, Create.class)),
                        () -> Shared.internetError(this, this));
            }

        });

        join.setOnClickListener(view -> {

            if (!Shared.isOnline(this)) {
                Shared.internetError(this, this);
            } else {
                Shared.signIn(() -> startActivity(new Intent(MultiMode.this, Join.class)),
                        () -> Shared.internetError(this, this));
            }


        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                leave();
            }
        });

    }

    public void backButton() {

        //ADD INTERSTITIAL
        InterstitialAd.load(this, "ca-app-pub-3922358669029120/9714465383", new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;
                    }
                });

        back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));
        back.setZ(30);

        back.setOnClickListener(view -> leave());

    }

    private void leave() {
        if (interstitialAd == null) {
            Shared.navigateBack(this, Start.class);
            return;
        }

        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                Shared.navigateBack(MultiMode.this, Start.class);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Shared.navigateBack(MultiMode.this, Start.class);
            }
        });
        interstitialAd.show(this);
        interstitialAd = null;
    }
}
