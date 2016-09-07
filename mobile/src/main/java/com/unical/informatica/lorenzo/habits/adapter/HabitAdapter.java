package com.unical.informatica.lorenzo.habits.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unical.informatica.lorenzo.habits.R;
import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.model.HabitAction;
import com.unical.informatica.lorenzo.habits.model.HabitHobby;
import com.unical.informatica.lorenzo.habits.model.HabitOther;
import com.unical.informatica.lorenzo.habits.model.HabitPosition;

import java.util.List;


/**
 * Created by Lorenzo on 16/08/2016.
 */
public class HabitAdapter extends BaseAdapter {

    private Context mContext;
    private List<Habit> mHabits;

    public HabitAdapter(final Context context, final List<Habit> habits) {
        this.mContext = context;
        this.mHabits = habits;
    }

    public void setHabits(final List<Habit> habits) {
        this.mHabits = habits;
    }

    @Override
    public int getCount() {
        return this.mHabits.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mHabits.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.habit_row_layout, null);
        }
        final Habit habit = (Habit) getItem(i);
        final int index = i;
        final ImageView image = (ImageView) view.findViewById(R.id.imageView);
        final ImageView delete = (ImageView) view.findViewById(R.id.deleteButton);
        final TextView type = (TextView) view.findViewById(R.id.contextRowHabitsTitle);
        final TextView date = (TextView) view.findViewById(R.id.rowHabitsDateContext);
        final TextView time = (TextView) view.findViewById(R.id.rowHabitsTimeContext);
        final TextView text = (TextView) view.findViewById(R.id.contexIntoHabitRow);
        if(habit instanceof HabitPosition)
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_map,mContext.getTheme()));
        else if(habit instanceof HabitAction)
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_action_phone,mContext.getTheme()));
        else if(habit instanceof HabitHobby)
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_hobby,mContext.getTheme()));
        else if(habit instanceof HabitOther)
            image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_other,mContext.getTheme()));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Delete Routine")
                        .setMessage("Are you sure you want to delete this habit?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HabitsManager.getInstance().deleteHabit(index);
                                HabitsManager.getInstance().getAdapter().notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        type.setText(habit.getType());
        date.setText(habit.getmDay());
        time.setText(habit.getmTime());
        text.setText(habit.getText());
        return view;

    }
}
