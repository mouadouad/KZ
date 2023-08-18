package com.mouadouad0.kz;

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

import java.util.Random;

public class Solo extends AppCompatActivity {

    int line;
    String s;
    int a, b, c, d;
    Button confirm, replay;
    Boolean error = false;
    TextView at1;
    MainGame mainGameClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainGameClass = new MainGame(this);
        setContentView(mainGameClass);

        randomGenerator();
        setConfirmButtons();
        Shared.background(this, this);
        Shared.backButton(this, this, Start.class);
        setTextView();

        confirm.setOnClickListener(view -> {
            if (!error) {
                checkAnswer();
            }
        });

        replay.setOnClickListener(view -> {
            if (!error) {
                line = 0;
                randomGenerator();
                at1.setText("");
                mainGameClass.getEditText().setText("");
            }
        });

    }

    public void randomGenerator() {

        Random rand;
        rand = new Random();

        a = rand.nextInt(10);
        b = rand.nextInt(10);
        c = rand.nextInt(10);
        d = rand.nextInt(10);

        while (b == a) {
            b = rand.nextInt(10);
        }

        while (c == a || c == b) {
            c = rand.nextInt(10);
        }

        while (d == a || d == b || d == c) {
            d = rand.nextInt(10);
        }

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);

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

    public void setTextView() {

        float a, b;
        a = 195;  //(1080-200*3-30*3)/2;
        b = 200 * 3 + 30 * 2;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(b), Shared.setY(550));
        at1 = new TextView(this);
        // at1.setBackgroundResource(R.drawable.border);
        at1.setTextSize(Shared.seth(20));
        at1.setTextColor(Color.WHITE);
        at1.setTypeface(Start.fredoka);
        at1.setMovementMethod(new ScrollingMovementMethod());
        ((Activity) this).addContentView(at1, layoutParams);
        at1.setY(Shared.setY(50));
        at1.setX(Shared.setX(a));


    }

    public void checkAnswer() {

        String r1 = mainGameClass.getEditText().getText().toString();

        if (r1.isEmpty()) {
            setError();
        } else if (r1.length() != 4) {
            setError();
        } else {
            int rep = Integer.parseInt(r1);
            int a1, b1, c1, d1, juste = 0, m_p = 0;

            a1 = rep / 1000;
            b1 = (rep % 1000) / 100;
            c1 = (rep / 10) - 100 * a1 - 10 * b1;
            d1 = rep - 10 * (100 * a1 + 10 * b1 + c1);

            if (a1 == b1 || a1 == c1 || a1 == d1 || b1 == c1 || b1 == d1 || c1 == d1) {
                setError();
            } else if (a1 == a && b1 == b && c1 == c && d1 == d) {
                line++;
                s = "Correct";
                String st1 = " " + line + "--" + r1 + "       " + s + "\n";

                SpannableString ss = new SpannableString(st1);
                ForegroundColorSpan red = new ForegroundColorSpan(Color.GREEN);
                ss.setSpan(red, 14 + String.valueOf(line).length(), 14 + String.valueOf(line).length() + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#F5A623"));//104AA8
                ss.setSpan(black, 0, 3 + String.valueOf(line).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                at1.append(ss);
                mainGameClass.getEditText().setText("");

            } else {
                if (a1 == a) {
                    juste++;
                } else if (a1 == b || a1 == c || a1 == d) {
                    m_p++;
                }

                if (b1 == b) {
                    juste++;
                } else if (b1 == a || b1 == c || b1 == d) {
                    m_p++;
                }

                if (c1 == c) {
                    juste++;
                } else if (c1 == b || c1 == a || c1 == d) {
                    m_p++;
                }

                if (d1 == d) {
                    juste++;
                } else if (d1 == b || d1 == c || d1 == a) {
                    m_p++;
                }

                if (juste != 0 && m_p != 0) {
                    String j = String.valueOf(juste);
                    String m = String.valueOf(m_p);
                    s = j + "J" + m + "M";
                } else if (juste == 0 && m_p == 0) {
                    s = "----";
                } else if (juste == 0) {
                    String m = String.valueOf(m_p);
                    s = m + "M";
                } else {
                    String j = String.valueOf(juste);
                    s = j + "J";
                }

                line++;

                String st1 = " " + line + "--" + r1 + "       " + s + "\n";

                SpannableString ss = new SpannableString(st1);
                ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#F5A623")); //104AA8
                ss.setSpan(red, 14 + String.valueOf(line).length(), 14 + String.valueOf(line).length() + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(black, 0, 3 + String.valueOf(line).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                at1.append(ss);
                mainGameClass.getEditText().setText("");

            }

        }

        //SCROLL TO THE LAST OF THE TEXTVIEW
        final int scrollAmount = at1.getLayout().getLineTop(at1.getLineCount()) - at1.getHeight();
        at1.scrollTo(0, Math.max(scrollAmount, 0));

    }

    public void setError() {

        Error myError = Shared.setError(this, this, R.drawable.error_box);
        error = true;
        myError.okButton.setOnClickListener(view -> {
            myError.messageBox.setVisibility(View.GONE);
            myError.dimLayout.setVisibility(View.GONE);
            error = false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainGameClass.getEditText().setText("");
    }
}
