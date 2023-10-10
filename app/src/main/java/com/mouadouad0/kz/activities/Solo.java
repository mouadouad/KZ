package com.mouadouad0.kz.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mouadouad0.kz.Digits;
import com.mouadouad0.kz.Error;
import com.mouadouad0.kz.MainGame;
import com.mouadouad0.kz.MainGameView;
import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;

import java.util.Random;

public class Solo extends AppCompatActivity {

    Button confirm, replay;
    MainGameView mainGameView;
    MainGame mainGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainGameView = new MainGameView(this);
        setContentView(mainGameView);
        mainGame = new MainGame(mainGameView);

        setConfirmButtons();
        Shared.background(this, this);
        Shared.backButton(this, this, Start.class);

        confirm.setOnClickListener(view -> {
            if (mainGame.isNotError()) {
                mainGame.checkEntry();
            }
        });

        replay.setOnClickListener(view -> {
            if (mainGame.isNotError()) {
                mainGame.reset();
            }
        });
    }

    public void setConfirmButtons() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(400), Shared.setY(150));

        confirm = new Button(this);
        replay = new Button(this);

        ((Activity) this).addContentView(confirm, layoutParams);
        ((Activity) this).addContentView(replay, layoutParams);

        replay.setBackgroundResource(R.drawable.again_button);
        confirm.setBackgroundResource(R.drawable.check_button);

        float a;
        a = 1080 - 400 - 100;

        confirm.setY(Shared.setY(1400));
        confirm.setX(Shared.setX(a));

        replay.setY(Shared.setY(1400));
        replay.setX(Shared.setX(100));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainGameView.getEditText().setText("");
    }
}
