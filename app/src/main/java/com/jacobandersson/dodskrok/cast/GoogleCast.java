package com.jacobandersson.dodskrok.cast;

import android.app.Activity;
import android.util.Log;

import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;
import com.jacobandersson.dodskrok.R;

import java.util.ArrayList;

public class GoogleCast {

    private static DataCastManager mCastManager = null;

    private static final String LOG_TAG = "DKAPP/GCast";

    public static DataCastManager getInstance(Activity context) {
        if (mCastManager == null)
            createInstance(context);

        return mCastManager;
    }

    public static void addConsumer(DataCastConsumer consumer) {
        Log.d(GoogleCast.LOG_TAG, "Consumer added");
        mCastManager.addDataCastConsumer(consumer);
    }

    private static void createInstance(Activity context) {
        String applicationId = context.getResources().getString(R.string.cast_appid);
        Log.d(GoogleCast.LOG_TAG, "Instance created");

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
    }

}
