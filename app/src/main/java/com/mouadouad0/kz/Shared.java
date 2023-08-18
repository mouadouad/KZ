package com.mouadouad0.kz;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class Shared {

    public static int setx(float x) {
        return (int) ((x * Start.width) / 1080);
    }

    public static int sety(float x) {
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

    public static Error setError(Context context, AppCompatActivity appCompatActivity) {
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
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(200), Shared.sety(100));
        messageBox.addView(okButton, layoutParams3);
        okButton.setBackgroundResource(R.drawable.okay_button);
        okButton.setX(Shared.setx(250));
        okButton.setY(Shared.sety(300 - 100 - 20));

        //BOX
        messageBox.setBackgroundResource(R.drawable.error_box);
        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setx(700), Shared.sety(300));
        appCompatActivity.addContentView(messageBox, layoutParams4);
        messageBox.setX(Shared.setx(190));
        messageBox.setY(Shared.sety(735));
        messageBox.setZ(30);

        return new Error(messageBox, okButton, dimLayout);
    }
}


