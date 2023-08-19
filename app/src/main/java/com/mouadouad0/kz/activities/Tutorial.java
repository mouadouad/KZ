package com.mouadouad0.kz.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;


public class Tutorial extends AppCompatActivity {

    Button back, next;
    int page = 0;
    RelativeLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.backButton(this, this, Start.class);

        //BACKGROUND
        background = new RelativeLayout(this);
        RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        background.setBackgroundResource(R.drawable.tuto1);
        addContentView(background, backParams);

        //MAKING THE BUTTONS
        next = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(200), Shared.setY(150));
        next.setBackgroundResource(R.drawable.next_button);
        addContentView(next, layoutParams);
        next.setY(Shared.setY(1450));
        next.setX(Shared.setX(1080 - 200 - 100));

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

}
