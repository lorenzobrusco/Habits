package com.unical.informatica.lorenzo.habits.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.unical.informatica.lorenzo.habits.MainActivity;
import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.adapter.HabitAdapter;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.view.CustomDialogAdd;


/**
 * Created by Lorenzo on 03/05/2016.
 * This fragment contein start layout
 */

public class StartFragment extends Fragment {

    private final String xmlFile = "habits.xml";
    private static FloatingActionButton actionButton;
    private static FloatingActionMenu actionButtonMenu;
    private HabitAdapter habitAdapter;
    private ObservableListView listView;
    private TextView textView;
    private String day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.habits_layout, container, false);
        this.listView = (ObservableListView) view.findViewById(R.id.list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Toast.makeText(getContext(), "show it on wearable", Toast.LENGTH_LONG).show();
                Habit habit = HabitsManager.getInstance().getHabit(pos);
                HabitsManager.getInstance().buildNotify(getContext(), habit);
                HabitsManager.getInstance().vibrate(getContext(), 300);
                HabitsManager.getInstance().sendRoutines(habit.getType() + "--" + habit.getText());

                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (actionButtonMenu.isOpen())
                    actionButtonMenu.close(true);
                CustomDialogAdd customDialogAdd = new CustomDialogAdd(getContext(), HabitsManager.getInstance().getHabits(day).get(i).getType(), HabitsManager.getInstance().getHabits(day).get(i));
                customDialogAdd.show();
            }
        });
        this.textView = (TextView) view.findViewById(R.id.HabitsIsEmpty);
        this.initListView();
        if (actionButton == null)
            this.initFAB();
        return view;
    }

    private void initFAB() {
        final ImageView fabIconNew = new ImageView(this.getContext());
        fabIconNew.setImageResource(R.drawable.ic_action_add);
        actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this.getActivity())
                .setContentView(fabIconNew)
                .setBackgroundDrawable(R.drawable.button_action_orange)
                .build();

        final int subActionButtonSize = getResources().getDimensionPixelSize(R.dimen.radius_small);
        FrameLayout.LayoutParams dimensionSubActionButtons = new FrameLayout.LayoutParams(subActionButtonSize, subActionButtonSize);
        SubActionButton.Builder subActionButtons = new SubActionButton.Builder(this.getActivity());
        subActionButtons.setLayoutParams(dimensionSubActionButtons);

        ImageView position = new ImageView(this.getContext());
        ImageView action = new ImageView(this.getContext());
        ImageView hobby = new ImageView(this.getContext());
        ImageView other = new ImageView(this.getContext());

        position.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location, this.getContext().getTheme()));
        action.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_action, this.getContext().getTheme()));
        hobby.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_hobby, this.getContext().getTheme()));
        other.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit, this.getContext().getTheme()));

        actionButtonMenu = new FloatingActionMenu.Builder(this.getActivity())
                .addSubActionView(subActionButtons.setContentView(position).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_red, this.getContext().getTheme())).build())
                .addSubActionView(subActionButtons.setContentView(action).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_yellow, this.getContext().getTheme())).build())
                .addSubActionView(subActionButtons.setContentView(hobby).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_green, this.getContext().getTheme())).build())
                .addSubActionView(subActionButtons.setContentView(other).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue, this.getContext().getTheme())).build())
                .attachTo(actionButton)
                .build();

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonMenu.close(true);
                CustomDialogAdd customDialogAdd = new CustomDialogAdd(getContext(), "Position");
                customDialogAdd.show();

            }

        });

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonMenu.close(true);
                CustomDialogAdd customDialogAdd = new CustomDialogAdd(getContext(), "Action");
                customDialogAdd.show();

            }

        });

        hobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonMenu.close(true);
                CustomDialogAdd customDialogAdd = new CustomDialogAdd(getContext(), "Hobby");
                customDialogAdd.show();

            }

        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonMenu.close(true);
                CustomDialogAdd customDialogAdd = new CustomDialogAdd(getContext(), "Other");
                customDialogAdd.show();

            }

        });

        actionButtonMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {

            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();

            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();

            }

        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    public Fragment setDay(String day) {
        this.day = day;
        return this;
    }

    private void initListView() {
        this.habitAdapter = new HabitAdapter(getContext(), HabitsManager.getInstance().getHabits(day));
        this.listView.setAdapter(this.habitAdapter);
        if (!HabitsManager.getInstance().getHabits(day).isEmpty())
            this.textView.setVisibility(View.INVISIBLE);
    }

}
