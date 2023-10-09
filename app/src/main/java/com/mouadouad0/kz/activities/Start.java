package com.mouadouad0.kz.activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;


public class Start extends AppCompatActivity {

    public static float width, height;
    Button solo, multi, tutorial;
    ImageView icon;
    //InterstitialAd mInterstitialAd;
    public static Typeface fredoka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        Shared.background(this, this);
        makeButtons();
        makeIcon();
        animate();

        //FONT
        fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");
        
        //ADD INTERSTITIAL
        //mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3922358669029120/9714465383");
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //ONCLICK BUTTONS
        solo.setOnClickListener(view -> {
            Intent intent = new Intent(Start.this, Solo.class);
            startActivity(intent);
        });

        multi.setOnClickListener(view -> {
            Intent intent = new Intent(Start.this, MultiMode.class);
            startActivity(intent);
            //mInterstitialAd.show();

        });
        tutorial.setOnClickListener(view -> {
            Intent intent = new Intent(Start.this, Tutorial.class);
            startActivity(intent);

        });


    }

    private void animate() {
        //ANIMATION FOR SPLASH SCREEN
        Animation from_bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        Animation from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);

        solo.setAnimation(from_bottom);
        multi.setAnimation(from_bottom);
        icon.setAnimation(from_top);
        tutorial.setAnimation(from_top);
    }

    private void makeIcon() {
        //MAKING THE ICON
        icon = new ImageView(this);
        icon.setBackgroundResource(R.drawable.icon);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(400), Shared.setY(300));
        addContentView(icon, layoutParams2);
        icon.setY(Shared.setY(300));
        icon.setX(Shared.setX(340));
    }

    private void makeButtons() {
        //MAKING THE BUTTONS
        multi = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        multi.setBackgroundResource(R.drawable.multi_button);
        addContentView(multi, layoutParams);
        multi.setY(Shared.setY(1000));
        multi.setX(Shared.setX(390));

        solo = new Button(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        solo.setBackgroundResource(R.drawable.normal_button);
        addContentView(solo, layoutParams1);
        solo.setY(Shared.setY(800));
        solo.setX(Shared.setX(390));

        tutorial = new Button(this);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(250), Shared.setY(150));
        tutorial.setBackgroundResource(R.drawable.tuto_button);
        addContentView(tutorial, layoutParams3);
        tutorial.setY(Shared.setY(100));
        tutorial.setX(Shared.setX(1080 - 300));
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }

}
