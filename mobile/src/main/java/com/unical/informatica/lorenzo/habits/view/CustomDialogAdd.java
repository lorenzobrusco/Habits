package com.unical.informatica.lorenzo.habits.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.model.HabitAction;
import com.unical.informatica.lorenzo.habits.model.HabitHobby;
import com.unical.informatica.lorenzo.habits.model.HabitOther;
import com.unical.informatica.lorenzo.habits.model.HabitPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorenzo on 20/08/2016.
 */
public class CustomDialogAdd extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private Button save;
    private Button cancel;
    private Spinner mday;
    private Spinner mtime;
    private TextView mtext;
    private Habit habit;
    private String type;
    private boolean modify;

    public CustomDialogAdd(Context context, String type) {
        super(context);
        this.mContext = context;
        this.type = type;
        this.modify = false;
    }

    public CustomDialogAdd(Context context, String type, Habit habit) {
        super(context);
        this.mContext = context;
        this.type = type;
        this.habit = habit;
        this.modify = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.coloroDialog);
        ImageView image = (ImageView) findViewById(R.id.imageViewAdd);
        TextView titleDialog = (TextView) findViewById(R.id.AddNewHabits);
        TextView typeText = (TextView) findViewById(R.id.contextRowHabitsTitleAdd);
        TextView title = (TextView) findViewById(R.id.rowHabitsTitleAdd);
        TextView date = (TextView) findViewById(R.id.rowHabitsDateAdd);
        TextView time = (TextView) findViewById(R.id.rowHabitsTimeAdd);
        TextView text = (TextView) findViewById(R.id.contexTextIntoHabitRowAdd);
        save = (Button) findViewById(R.id.buttonSave);
        cancel = (Button) findViewById(R.id.buttonDelete);
        mday = (Spinner) findViewById(R.id.rowHabitsDateContextAdd);
        mtime = (Spinner) findViewById(R.id.rowHabitsTimeContextAdd);
        this.initDay();
        this.initTime();

        mtext = (TextView) findViewById(R.id.contexIntoHabitRowAdd);
        typeText.setText(type);
        if (modify)
            titleDialog.setText("Modify " + type);
        if (type.equals("Position")) {
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_map, mContext.getTheme()));
            relativeLayout.setBackgroundColor(getContext().getResources().getColor(R.color.position));
            title.setTextColor(getContext().getResources().getColor(R.color.position));
            date.setTextColor(getContext().getResources().getColor(R.color.position));
            time.setTextColor(getContext().getResources().getColor(R.color.position));
            text.setTextColor(getContext().getResources().getColor(R.color.position));
            save.setTextColor(getContext().getResources().getColor(R.color.position));
            cancel.setTextColor(getContext().getResources().getColor(R.color.position));
            if (!this.modify)
                this.habit = new HabitPosition();
        } else if (type.equals("Action")) {
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_action_phone, mContext.getTheme()));
            relativeLayout.setBackgroundColor(getContext().getResources().getColor(R.color.action));
            title.setTextColor(getContext().getResources().getColor(R.color.action));
            date.setTextColor(getContext().getResources().getColor(R.color.action));
            time.setTextColor(getContext().getResources().getColor(R.color.action));
            text.setTextColor(getContext().getResources().getColor(R.color.action));
            save.setTextColor(getContext().getResources().getColor(R.color.action));
            cancel.setTextColor(getContext().getResources().getColor(R.color.action));
            if (!this.modify)
                this.habit = new HabitAction();
        } else if (type.equals("Hobby")) {
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_hobby, mContext.getTheme()));
            relativeLayout.setBackgroundColor(getContext().getResources().getColor(R.color.hobby));
            title.setTextColor(getContext().getResources().getColor(R.color.hobby));
            date.setTextColor(getContext().getResources().getColor(R.color.hobby));
            time.setTextColor(getContext().getResources().getColor(R.color.hobby));
            text.setTextColor(getContext().getResources().getColor(R.color.hobby));
            save.setTextColor(getContext().getResources().getColor(R.color.hobby));
            cancel.setTextColor(getContext().getResources().getColor(R.color.hobby));
            if (!this.modify)
                this.habit = new HabitHobby();
        } else if (type.equals("Other")) {
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_other, mContext.getTheme()));
            relativeLayout.setBackgroundColor(getContext().getResources().getColor(R.color.other));
            save.setTextColor(getContext().getResources().getColor(R.color.other));
            title.setTextColor(getContext().getResources().getColor(R.color.other));
            date.setTextColor(getContext().getResources().getColor(R.color.other));
            time.setTextColor(getContext().getResources().getColor(R.color.other));
            text.setTextColor(getContext().getResources().getColor(R.color.other));
            cancel.setTextColor(getContext().getResources().getColor(R.color.other));
            if (!this.modify)
                this.habit = new HabitOther();
        }
        if (modify) {
            this.mday.setPrompt(this.habit.getmDay());
            this.mtime.setPrompt(this.habit.getmTime());
            this.mtext.setText(this.habit.getText());
        }

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSave: {
                this.habit.setText(((TextView) findViewById(R.id.contexIntoHabitRowAdd)).getText().toString());
                habit.setmDay(mday.getSelectedItem().toString());
                habit.setmTime(mtime.getSelectedItem().toString());
                habit.createID();
                if (!modify)
                    HabitsManager.getInstance().addHabits(habit);
                HabitsManager.getInstance().getAdapter().notifyDataSetChanged();
                dismiss();
                break;
            }
            case R.id.buttonDelete: {
                dismiss();
                break;
            }
        }
    }

    private void initDay() {
        // Spinner Drop down elements
        List<String> days = new ArrayList<String>();
        days.add("Everyday");
        days.add("Sunday");
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thrusday");
        days.add("Friday");
        days.add("Saturday");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.row_spinner_layout, days);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mday.setAdapter(dataAdapter);

        mday.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private void initTime() {
        // Spinner Drop down elements
        List<String> times = new ArrayList<String>();
        times.add("Early morning");
        times.add("Morning");
        times.add("Midday");
        times.add("Afternoon");
        times.add("Late afternoon");
        times.add("night");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.row_spinner_layout, times);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mtime.setAdapter(dataAdapter);

        mtime.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }

}
