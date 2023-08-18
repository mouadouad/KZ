package com.mouadouad0.kz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainGame extends View {

    Context context;
    final List<Button> buttons = new ArrayList<>();
    private TextView et;

    public MainGame(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setKeyboard();
        setEditText();
        setButtonsClick();
    }

    public TextView getEditText() {
        return et;
    }

    private void setButtons(Button button) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(200), Shared.sety(120));
        ((Activity) this.context).addContentView(button, layoutParams);

    }

    private void setKeyboard() {
        final List<Integer> backgrounds = Arrays.asList(R.drawable.button_1, R.drawable.button_2,
                R.drawable.button_3, R.drawable.button_4, R.drawable.button_5, R.drawable.button_6,
                R.drawable.button_7, R.drawable.button_8, R.drawable.button_9, R.drawable.button_0,
                R.drawable.del_button);

        for (int i = 0; i < 11; i++) {
            Button button = new Button(this.context);
            setButtons(button);
            button.setY(Shared.sety(800 + (int) (i / 3) * 150));
            button.setX(Shared.setx(195 + (i % 3) * 250));
            button.setBackgroundResource(backgrounds.get(i));
            buttons.add(button);
        }
        buttons.get(9).setX(Shared.setx(310));
        buttons.get(10).setX(Shared.setx(310 + 200 + 30));

    }

    private void setEditText() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(660), Shared.sety(100));
        et = new TextView(this.context);
        et.setBackgroundResource(R.drawable.border);
        et.setTextColor(Color.WHITE);
        et.setTypeface(Start.fredoka);
        et.setTextSize(Shared.seth(20));
        et.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        ((Activity) this.context).addContentView(et, layoutParams);
        et.setY(Shared.sety(650));
        et.setX(Shared.setx(195));
    }

    private void setButtonsClick() {

        for (int i = 1; i < 11; i++) {
            final String s = String.valueOf(i % 10);
            buttons.get(i - 1).setOnClickListener(view -> et.append(s));
        }

        buttons.get(10).setOnClickListener(view -> {
            String r1 = et.getText().toString();
            EditText et1 = new EditText(context);

            if (r1.isEmpty()) {
                et.setText("");
            } else {
                et1.setText(r1);
                et1.setText(et1.getText().delete(et1.getText().length() - 1, et1.getText().length()));
                et.setText(et1.getText());
            }
        });

    }

}
