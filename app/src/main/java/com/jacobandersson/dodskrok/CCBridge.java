package com.jacobandersson.dodskrok;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.android.gms.cast.CastDevice;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jacob on 2015-12-20.
 */
public class CCBridge {

    private static PlayingActivity context = null;
    private static DataCastManager dataCastManager = null;

    private final static String NAMESPACE_ADD_MISSION = "add_mission";
    private final static String NAMESPACE_ADD_PERSON = "add_player";
    private final static String NAMESPACE_REMOVE_MISSION = "remove_mission";
    private final static String NAMESPACE_REMOVE_PERSON = "remove_player";
    private final static String NAMESPACE_ACTION = "action";
    private final static String NAMESPACE_TIME_MIN = "time_min";
    private final static String NAMESPACE_TIME_MAX = "time_max";

    public static boolean add(String text, PlayingArrayAdapter.Types type) {
        if (type == PlayingArrayAdapter.Types.PERSON)
            return addPerson(text);
        if (type == PlayingArrayAdapter.Types.MISSION)
            return addMission(text);
        return false;
    }

    public static boolean remove(int index, PlayingArrayAdapter.Types type) {
        if (type == PlayingArrayAdapter.Types.PERSON)
            return removePerson(index);
        if (type == PlayingArrayAdapter.Types.MISSION)
            return removeMission(index);
        return false;
    }

    /////////////////////////

    public static void setContext(PlayingActivity context) {
        CCBridge.context = context;
    }

    public static void setDataManager(DataCastManager dataCastManager) {
        CCBridge.dataCastManager = dataCastManager;
    }

    /////////////////////////

    private static boolean addPerson(String name) {
        sendDataMessageIfConnected(name, NAMESPACE_ADD_PERSON);

        context.personsArrayAdapter.getList().add(name);
        context.personsArrayAdapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(context.personListView, context);

        checkFloatingActionButton();

        return true;
    }

    private static boolean addMission(String mission) {
        sendDataMessageIfConnected(mission, NAMESPACE_ADD_MISSION);

        context.missionsArrayAdapter.getList().add(mission);
        context.missionsArrayAdapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(context.missionsListView, context);

        checkFloatingActionButton();

        return true;
    }

    private static boolean removePerson(Integer index) {
        sendDataMessageIfConnected(index.toString(), NAMESPACE_REMOVE_PERSON);

        context.personsArrayAdapter.getList().remove(index.intValue());
        context.personsArrayAdapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(context.personListView, context);

        checkFloatingActionButton();

        return true;
    }

    private static boolean removeMission(Integer index) {
        sendDataMessageIfConnected(index.toString(), NAMESPACE_REMOVE_MISSION);

        context.missionsArrayAdapter.getList().remove(index.intValue());
        context.missionsArrayAdapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(context.missionsListView, context);

        checkFloatingActionButton();

        return true;
    }

    public static void pause() {
        sendDataMessageIfConnected("pause", NAMESPACE_ACTION);
    }

    public static void play() {
        sendDataMessageIfConnected("play", NAMESPACE_ACTION);
    }

    public static void updateTime(String min, String max) {
        sendDataMessageIfConnected(min, NAMESPACE_TIME_MIN);
        sendDataMessageIfConnected(max, NAMESPACE_TIME_MAX);
    }

    public static void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        if (message.equals("\"initialize\""))
            castSendLists();
        else if (message.equals("\"not_admin\""))
            context.showNotAdminDialog();
    }

    ///////////////////////////

    public static void checkFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab_play);

        if (context.isStartReady() && context.castDeviceConnected) {
            int bg = ContextCompat.getColor(context, R.color.colorPrimaryDark);
            int color = ContextCompat.getColor(context, android.R.color.white);

            fab.setBackgroundTintList(ColorStateList.valueOf(bg));
            fab.setColorFilter(color);
        } else {
            int bg = ContextCompat.getColor(context, android.R.color.white);
            int color = ContextCompat.getColor(context, R.color.background);

            fab.setBackgroundTintList(ColorStateList.valueOf(bg));
            fab.setColorFilter(color);
        }
    }

    private static void sendDataMessageIfConnected(String message, String key) {
        if (!dataCastManager.isConnected())
            return;

        HashMap<String, String> data = new HashMap<String, String>(1);
        data.put(key, message);
        String json = new JSONObject(data).toString();

        try {
            dataCastManager.sendDataMessage(json, context.getResources().getString(R.string.cast_namespace));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void castSendLists() {
        ArrayList<String> missions = context.missionsArrayAdapter.getList();
        ArrayList<String> persons = context.personsArrayAdapter.getList();

        context.castDeviceConnected = true;

        sendDataMessageIfConnected("initialize", NAMESPACE_ACTION);
        updateTime(String.valueOf(context.settingsTimeMin), String.valueOf(context.settingsTimeMax));

        for(int i = 0; i < missions.size(); i++)
            sendDataMessageIfConnected(missions.get(i), NAMESPACE_ADD_MISSION);

        for(int i = 0; i < persons.size(); i++)
            sendDataMessageIfConnected(persons.get(i), NAMESPACE_ADD_PERSON);
    }
}
