package com.unical.informatica.lorenzo.habits.support;

import android.util.Log;
import android.util.Xml;

import com.unical.informatica.lorenzo.habits.manager.HabitsManager;
import com.unical.informatica.lorenzo.habits.model.Habit;
import com.unical.informatica.lorenzo.habits.model.HabitAction;
import com.unical.informatica.lorenzo.habits.model.HabitHobby;
import com.unical.informatica.lorenzo.habits.model.HabitOther;
import com.unical.informatica.lorenzo.habits.model.HabitPosition;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class XMLPullParserHandler {

    private static final String ID = "id";
    private static final String DATE = "Date";
    private static final String TIME = "Time";
    private static final String TEXT = "Text";
    List<Habit> habits;
    private Habit habit;
    private String text;

    public XMLPullParserHandler() {
        habits = new ArrayList<Habit>();
    }

    public List<Habit> getEmployees() {
        return habits;
    }

    public String writeAll() throws IOException {
        String type = "";

        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);

        xmlSerializer.startDocument("UTF-8", true);
        for (Habit habit : HabitsManager.getInstance().getHabitsToSave()) {
            Log.i("Debug",habit.getText());
            if (habit instanceof HabitPosition)
                type = "HabitPosition";
            else if (habit instanceof HabitAction)
                type = "HabitAction";
            else if (habit instanceof HabitHobby)
                type = "HabitHobby";
            else if (habit instanceof HabitOther)
                type = "HabitOther";

            xmlSerializer.startTag("", type);

            xmlSerializer.startTag("", ID);
            xmlSerializer.text(habit.getID());
            xmlSerializer.endTag("", ID);

            xmlSerializer.startTag("", DATE);
            xmlSerializer.text(habit.getmDay());
            xmlSerializer.endTag("", DATE);

            xmlSerializer.startTag("", TIME);
            xmlSerializer.text(habit.getmTime());
            xmlSerializer.endTag("", TIME);

            xmlSerializer.startTag("", TEXT);
            xmlSerializer.text(habit.getText());
            xmlSerializer.endTag("", TEXT);

            xmlSerializer.endTag("", type);
        }
        xmlSerializer.endDocument();
        return writer.toString();
    }

    public List<Habit> read(InputStream fileInputStream) {

        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();


            parser.setInput(fileInputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("HabitPosition")) {
                            habit = new HabitPosition();
                        } else if (tagname.equalsIgnoreCase("HabitHobby")) {
                            habit = new HabitHobby();
                        } else if (tagname.equalsIgnoreCase("HabitAction")) {
                            habit = new HabitAction();
                        } else if (tagname.equalsIgnoreCase("HabitOther")) {
                            habit = new HabitOther();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.contains("Habit")) {
                            habits.add(habit);
                        } else if (tagname.equalsIgnoreCase("Date")) {
                            habit.setmDay(text);
                        } else if (tagname.equalsIgnoreCase("Time")) {
                            habit.setmTime(text);
                        } else if (tagname.equalsIgnoreCase("Text")) {
                            habit.setText(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return habits;
    }
}