package com.mouadouad0.kz;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.mouadouad0.kz.activities.Start;

public class Shared {

    public static int setX(float x) {
        return (int) ((x * Start.width) / 1080);
    }

    public static int setY(float x) {
        return (int) ((x * Start.height) / 1770);
    }

    public static int seth(float h) {
        float width = Start.width;
        float height = Start.height;
        float hypotenuse = (int) Math.sqrt(width * width + height * height);

        return (int) ((h * hypotenuse) / 2074);
    }

    public static void background(Context context, AppCompatActivity appCompatActivity) {
        //BACKGROUND
        RelativeLayout background = new RelativeLayout(context);
        RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color = Color.parseColor("#2F2958");
        background.setBackgroundColor(back_color);
        appCompatActivity.addContentView(background, backParams);
    }

    public static Error setError(Context context, AppCompatActivity appCompatActivity, Integer resource) {
        final RelativeLayout messageBox;
        final Button okButton;
        final FrameLayout dimLayout;

        //MAKE THE SCREEN DIM
        Resources res = appCompatActivity.getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.dim, null);
        dimLayout = new FrameLayout(context);
        dimLayout.setForeground(shape);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (Start.width), (int) (Start.height));
        appCompatActivity.addContentView(dimLayout, layoutParams1);
        dimLayout.getForeground().setAlpha(200);
        dimLayout.setZ(20);

        //SETTING THE ERROR MESSAGE
        messageBox = new RelativeLayout(context);
        okButton = new Button(context);

        //BUTTON
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(200), Shared.setY(100));
        messageBox.addView(okButton, layoutParams3);
        okButton.setBackgroundResource(R.drawable.okay_button);
        okButton.setX(Shared.setX(250));
        okButton.setY(Shared.setY(300 - 100 - 20));

        //BOX
        messageBox.setBackgroundResource(resource);
        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setX(700), Shared.setY(300));
        appCompatActivity.addContentView(messageBox, layoutParams4);
        messageBox.setX(Shared.setX(190));
        messageBox.setY(Shared.setY(735));
        messageBox.setZ(30);

        return new Error(messageBox, okButton, dimLayout);
    }

    public static void internetError(Context context, AppCompatActivity appCompatActivity) {
        Error myError = Shared.setError(context, appCompatActivity, R.drawable.internet_box);

        myError.okButton.setOnClickListener(view -> {
            myError.messageBox.setVisibility(View.GONE);
            myError.dimLayout.setVisibility(View.GONE);

        });
    }

    public static void backButton(Context context, AppCompatActivity appCompatActivity, Class<? extends AppCompatActivity> activity) {
        Button back=new Button(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100),Shared.setY(100));
        back.setBackgroundResource(R.drawable.back_button);
        appCompatActivity.addContentView(back,layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));
        back.setZ(30);

        back.setOnClickListener(view -> {
            Intent intent=new Intent(context, activity);
            appCompatActivity.startActivity(intent);
        });
    }

    public static void banner(Context context, AppCompatActivity appCompatActivity) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) Start.width, (int) Start.height - getStatusBarHeight(appCompatActivity));
        appCompatActivity.addContentView(layout, layoutParams1);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView, layoutParams);

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        //MobileAds.initialize(context, "ca-app-pub-3922358669029120~3985187056");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private static int getStatusBarHeight(AppCompatActivity appCompatActivity) {
        int result = 0;
        int resourceId = appCompatActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appCompatActivity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}


