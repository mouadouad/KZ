package com.mouadouad0.kz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mouadouad0.kz.activities.Start;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainGameView extends View {

    Context context;
    final List<Button> buttons = new ArrayList<>();
    private TextView editText, gameText;

    public MainGameView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setKeyboard();
        setEditTextParams();
        setButtonsClick();
        setGameTextParams();
    }

    public TextView getEditText() {
        return editText;
    }

    public TextView getGameText() {
        return gameText;
    }

    private void setButtons(Button button) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(200), Shared.setY(120));
        ((Activity) this.context).addContentView(button, layoutParams);
    }

    private void setKeyboard() {
        final List<Integer> buttonBackgrounds = Arrays.asList(R.drawable.button_1, R.drawable.button_2,
                R.drawable.button_3, R.drawable.button_4, R.drawable.button_5, R.drawable.button_6,
                R.drawable.button_7, R.drawable.button_8, R.drawable.button_9, R.drawable.button_0,
                R.drawable.del_button);

        for (int i = 0; i < 11; i++) {
            Button button = new Button(this.context);
            setButtons(button);
            final int a = i / 3;
            button.setY(Shared.setY(800 + a * 150));
            button.setX(Shared.setX(195 + (i % 3) * 250));
            button.setBackgroundResource(buttonBackgrounds.get(i));
            buttons.add(button);
        }
        buttons.get(9).setX(Shared.setX(310));
        buttons.get(10).setX(Shared.setX(310 + 200 + 30));
    }

    private void setEditTextParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(660), Shared.setY(100));
        editText = new TextView(this.context);
        editText.setBackgroundResource(R.drawable.border);
        editText.setTextColor(Color.WHITE);
        editText.setTypeface(Start.fredoka);
        editText.setTextSize(Shared.seth(20));
        editText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        ((Activity) this.context).addContentView(editText, layoutParams);
        editText.setY(Shared.setY(650));
        editText.setX(Shared.setX(195));
    }

    private void setButtonsClick() {
        for (int i = 1; i < 11; i++) {
            final String buttonValue = String.valueOf(i % 10);
            buttons.get(i - 1).setOnClickListener(view -> editText.append(buttonValue));
        }
        buttons.get(10).setOnClickListener(view -> { //DELETE
            String input = editText.getText().toString();

            if (input.isEmpty()) {
                editText.setText("");
            } else {
                final String stringAfterDelete = input.substring(0, input.length() - 1);
                editText.setText(stringAfterDelete);
            }
        });
    }

    public void setGameTextParams() {
        final float a, b;
        a = 195;  //(1080-200*3-30*3)/2;
        b = 200 * 3 + 30 * 3;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(b), Shared.setY(550));
        gameText = new TextView(context);
        // at1.setBackgroundResource(R.drawable.border);
        gameText.setTextSize(Shared.seth(18));
        gameText.setTextColor(Color.WHITE);
        gameText.setTypeface(Start.fredoka);
        gameText.setMovementMethod(new ScrollingMovementMethod());
        ((Activity) context).addContentView(gameText, layoutParams);
        gameText.setY(Shared.setY(50));
        gameText.setX(Shared.setX(a));
    }

    public Error setError() {
        return Shared.setError(this.context, ((AppCompatActivity) this.context), R.drawable.error_box);
    }

}
