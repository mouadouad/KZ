package com.mouadouad0.kz;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class Error {
    public RelativeLayout messageBox;
    public Button okButton;
    public FrameLayout dimLayout;

    public Error(RelativeLayout messageBox, Button okButton, FrameLayout dimLayout) {
        this.messageBox = messageBox;
        this.okButton = okButton;
        this.dimLayout = dimLayout;
    }
}
