package com.mouadouad0.kz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


public class Tutorial extends AppCompatActivity {

    Button back, next;
    int page = 0;
    RelativeLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backButton();

        //BACKGROUND
        background = new RelativeLayout(this);
        RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        background.setBackgroundResource(R.drawable.tuto1);
        addContentView(background, backParams);

        //MAKING THE BUTTONS
        next = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(200), Shared.sety(150));
        next.setBackgroundResource(R.drawable.next_button);
        addContentView(next, layoutParams);
        next.setY(Shared.sety(1450));
        next.setX(Shared.setx(1080 - 200 - 100));

        next.setOnClickListener(view -> {

            switch (page) {
                case 0:
                    background.setBackgroundResource(R.drawable.tuto2);
                    break;
                case 1:
                    background.setBackgroundResource(R.drawable.tuto3);
                    next.setVisibility(View.GONE);
                    break;
            }

            page++;


        });

    }

    private void backButton() {

        back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(100), Shared.sety(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.sety(50));
        back.setX(Shared.setx(50));
        back.setZ(30);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(Tutorial.this, Start.class);
            startActivity(intent);
        });

    }

}
