package com.jacobandersson.dodskrok.playing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.cast.CastDevice;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.jacobandersson.dodskrok.R;
import com.jacobandersson.dodskrok.cast.CCBridge;
import com.jacobandersson.dodskrok.cast.CCConsumer;
import com.jacobandersson.dodskrok.cast.GoogleCast;

import io.fabric.sdk.android.Fabric;

public class PlayingActivity extends AppCompatActivity {

    private PlayingActivityModel model;
    private DataCastManager mCastManager;

    public static final String LOG_TAG = "DKAPP/Playing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new PlayingActivityModel(this);

        Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        model.logUser();
        Answers.getInstance().logContentView(new ContentViewEvent());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.actionbar_title);

        GoogleCast.getInstance(this);
        GoogleCast.addConsumer(new CCConsumer(this, new CCConsumer.CCConsumerListener() {
            @Override
            public void onConnected() {
                CCBridge.initialize(model.missionsArrayAdapter.getList(), model.playersArrayAdapter.getList());
            }

            @Override
            public void onDisconnected() {
                model.castDeviceConnected = false;
                model.updateFloatingActionButton();
            }

            @Override
            public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
                CCBridge.onMessageReceived(castDevice, namespace, message);
            }
        }));
        CCBridge.setContext(this);

        model.setupFabButtons();
        model.setupListViews();
        model.setupGameButtons();
        model.setFooterText();

        CCBridge.setCastListener(new CCBridge.CastListener() {
            @Override
            public void onPermissionDenied() {
                model.showNotAdminDialog();
            }

            @Override
            public void onInitialized() {
                Log.d(PlayingActivity.LOG_TAG, "onInitialized");
                model.castDeviceConnected = true;
                model.updateFloatingActionButton();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_time)
            model.showTimeDialog();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCastManager = DataCastManager.getInstance();
        mCastManager.incrementUiCounter();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mCastManager.decrementUiCounter();
    }
}
