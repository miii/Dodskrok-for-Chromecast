package com.jacobandersson.dodskrok;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.MediaRouteButton;

import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.util.ArrayList;

/**
 * Created by Jacob on 2015-12-21.
 */
public class GoogleCast {

    private static DataCastManager mCastManager = null;
    private static ArrayList<DataCastConsumer> consumers = new ArrayList<>();

    public static DataCastManager getInstance(Activity context) {
        if (mCastManager == null)
            createInstance(context);

        return mCastManager;
    }

    public static DataCastManager getInstance(DataCastConsumer context, boolean consumer) {
        if (consumer)
            consumers.add(context);

        return getInstance((Activity) context);
    }

    private static void createInstance(Activity context) {
        String applicationId = context.getResources().getString(R.string.cast_appid);

        BaseCastManager.checkGooglePlayServices(context);
        CastConfiguration options = new CastConfiguration.Builder(applicationId)
                .enableAutoReconnect()
                .enableDebug()
                .addNamespace(context.getResources().getString(R.string.cast_namespace))
                .enableWifiReconnection()
                .build();
        DataCastManager.initialize(context, options);

        mCastManager = DataCastManager.getInstance();
        CCBridge.setDataManager(mCastManager);
        mCastManager.reconnectSessionIfPossible();

        for(int i = 0; i < consumers.size(); i++)
            mCastManager.addDataCastConsumer(consumers.get(i));
    }

}
