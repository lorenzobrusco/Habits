package com.unical.informatica.lorenzo.habits.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;

/**
 * Created by Lorenzo on 06/09/2016.
 */
public class CustomDialogPrivacy extends Dialog implements View.OnClickListener {

    private Button close;

    public CustomDialogPrivacy(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_privacy_layout);
        close = (Button) findViewById(R.id.closePrivacy);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closePrivacy: {
                dismiss();
                break;
            }
        }
    }
}
