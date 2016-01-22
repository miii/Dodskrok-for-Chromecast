package com.jacobandersson.dodskrok.cast;

import android.app.Activity;

import com.google.android.gms.cast.CastDevice;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.jacobandersson.dodskrok.playing.PlayingActivity;
import com.jacobandersson.dodskrok.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CCBridge {

    private static Activity context = null;
    private static DataCastManager dataCastManager = null;
    private static CastListener castListener = null;

    private final static String NAMESPACE_ADD_MISSION = "add_mission";
    private final static String NAMESPACE_ADD_PLAYER = "add_player";
    private final static String NAMESPACE_REMOVE_MISSION = "remove_mission";
    private final static String NAMESPACE_REMOVE_PERSON = "remove_player";
    private final static String NAMESPACE_ACTION = "action";
    private final static String NAMESPACE_TIME_MIN = "time_min";
    private final static String NAMESPACE_TIME_MAX = "time_max";

    ////////////////////////////////

    public static void setContext(PlayingActivity context) {
        CCBridge.context = context;
    }

    public static void setDataManager(DataCastManager dataCastManager) {
        CCBridge.dataCastManager = dataCastManager;
    }

    public static void setCastListener(CastListener cl) {
        castListener = cl;
    }

    ////////////////////////////////

    public static void addPlayer(String name) {
        _sendDataMessageIfConnected(name, NAMESPACE_ADD_PLAYER);
    }

    public static void addMission(String mission) {
        _sendDataMessageIfConnected(mission, NAMESPACE_ADD_MISSION);
    }

    public static void removePlayer(Integer index) {
        _sendDataMessageIfConnected(index.toString(), NAMESPACE_REMOVE_PERSON);
    }

    public static void removeMission(Integer index) {
        _sendDataMessageIfConnected(index.toString(), NAMESPACE_REMOVE_MISSION);
    }

    public static void pause() {
        _sendDataMessageIfConnected("pause", NAMESPACE_ACTION);
    }

    public static void play() {
        _sendDataMessageIfConnected("play", NAMESPACE_ACTION);
    }

    public static void updateTime(String min, String max) {
        _sendDataMessageIfConnected(min, NAMESPACE_TIME_MIN);
        _sendDataMessageIfConnected(max, NAMESPACE_TIME_MAX);
    }

    private static void sendLists(ArrayList<String> missions, ArrayList<String> players) {
        _sendDataMessageIfConnected("initialize", NAMESPACE_ACTION);

        for(int i = 0; i < missions.size(); i++)
            _sendDataMessageIfConnected(missions.get(i), NAMESPACE_ADD_MISSION);

        for(int i = 0; i < players.size(); i++)
            _sendDataMessageIfConnected(players.get(i), NAMESPACE_ADD_PLAYER);
    }

    /////////////////////////

    public static void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        if (message.equals("\"initialize\""))
            if (castListener != null) castListener.onInitialized();
        else if (message.equals("\"not_admin\""))
            if (castListener != null) castListener.onPermissionDenied();
    }

    private static void _sendDataMessageIfConnected(String message, String key) {
        if (!dataCastManager.isConnected())
            return;

        HashMap<String, String> data = new HashMap<>(1);
        data.put(key, message);
        String json = new JSONObject(data).toString();

        try {
            dataCastManager.sendDataMessage(json, context.getResources().getString(R.string.cast_namespace));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface CastListener {
        void onPermissionDenied();
        void onInitialized();
    }
}
