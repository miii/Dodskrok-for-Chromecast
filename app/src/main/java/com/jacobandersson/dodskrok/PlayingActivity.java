package com.jacobandersson.dodskrok;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;

import io.fabric.sdk.android.Fabric;

public class PlayingActivity extends AppCompatActivity implements DataCastConsumer {

    private final String LOG_DEBUG = "DKCC/Debug";

    private PlayingActivity maInstance;

    public ListView missionsListView;
    public ListView personListView;

    public PlayingArrayAdapter personsArrayAdapter;
    public PlayingArrayAdapter missionsArrayAdapter;
    public float settingsTimeMin = 1;
    public float settingsTimeMax = 3;

    public FloatingActionButton fab_play;

    public DataCastManager mCastManager;

    public boolean castDeviceConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        logUser();
        Answers.getInstance().logContentView(new ContentViewEvent());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        maInstance = this;
        GoogleCast.getInstance(this, true);
        CCBridge.setContext(this);
        //checkForUpdates();

        getSupportActionBar().setTitle(R.string.actionbar_title);

        fab_play = (FloatingActionButton) findViewById(R.id.fab_play);
        final FloatingActionButton fab_pause = (FloatingActionButton) findViewById(R.id.fab_pause);

        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar snackbar = Snackbar.make(
                        maInstance.findViewById(R.id.coordLayout),
                        R.string.fab_play_error,
                        Snackbar.LENGTH_LONG
                );
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(maInstance, R.color.colorAccent));

                if (isStartReady()) {
                    if (castDeviceConnected) {
                        CCBridge.play();
                        getSupportActionBar().setTitle(R.string.actionbar_title);
                        view.setVisibility(View.GONE);
                        fab_pause.setVisibility(View.VISIBLE);
                    } else {
                        snackbar.setText(R.string.fab_not_connected);
                        snackbar.show();
                    }
                } else {
                    snackbar.show();
                }

            }
        });
        fab_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CCBridge.pause();
                getSupportActionBar().setTitle(R.string.paused);
                view.setVisibility(View.GONE);
                fab_play.setVisibility(View.VISIBLE);
            }
        });

        missionsListView = (ListView) findViewById(R.id.missions_listview);
        missionsListView.setAdapter(new PlayingArrayAdapter(this, PlayingArrayAdapter.Types.MISSION));
        Utility.setListViewHeightBasedOnChildren(missionsListView, this);

        personListView = (ListView) findViewById(R.id.persons_listview);
        personListView.setAdapter(new PlayingArrayAdapter(this, PlayingArrayAdapter.Types.PERSON));
        Utility.setListViewHeightBasedOnChildren(personListView, this);

        Button addPersonButton = (Button) findViewById(R.id.addPersonButton);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlayingInputDialog(maInstance, PlayingArrayAdapter.Types.PERSON);
            }
        });

        Button addMissionButton = (Button) findViewById(R.id.addMissionButton);
        addMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlayingInputDialog(maInstance, PlayingArrayAdapter.Types.MISSION);
            }
        });

        TextView appName = (TextView) findViewById(R.id.app_name);
        String version = BuildConfig.VERSION_NAME;
        String appNameLabel = getResources().getString(R.string.dkcc_app_name) + " " + version;
        appName.setText(appNameLabel);
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        //Crashlytics.setUserIdentifier("12345");
        //Crashlytics.setUserEmail("user@fabric.io");
        //Crashlytics.setUserName("Test User");
    }

    /*private void checkForUpdates() {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Update url
        final String url = getResources().getString(R.string.dkcc_update_site);
        final String currentVersion = BuildConfig.VERSION_NAME;
        String latestUrl = url + getResources().getString(R.string.dkcc_update_file);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, latestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_DEBUG, "Installed version: " + currentVersion);
                        Log.d(LOG_DEBUG, "Latest version: " + response);
                        // If new version is available
                        if (!response.equals(currentVersion)) {
                            // Update
                            showUpdateDialog(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_DEBUG, "Update server down");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }*/

    public boolean isStartReady() {
        return personsArrayAdapter.getCount() > 1 && missionsArrayAdapter.getCount() > 0;
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
            showTimeDialog();

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

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_time));

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_set_time, null);
        final EditText minEditText = (EditText) dialogView.findViewById(R.id.dialog_time_min);
        final EditText maxEditText = (EditText) dialogView.findViewById(R.id.dialog_time_max);

        minEditText.setText(String.valueOf(settingsTimeMin));
        maxEditText.setText(String.valueOf(settingsTimeMax));

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.dialog_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsTimeMin = Float.parseFloat(minEditText.getText().toString());
                settingsTimeMax = Float.parseFloat(maxEditText.getText().toString());

                CCBridge.updateTime(minEditText.getText().toString(), maxEditText.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }

    public void showNotAdminDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.dialog_not_admin))
            .setMessage(getResources().getString(R.string.dialog_not_admin_content))
            .setNeutralButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        builder.show();
    }

    /*public void showUpdateDialog(final String version) {
        String dialogMessage = String.format(
                getResources().getString(R.string.dialog_update_available_content),
                BuildConfig.VERSION_NAME,
                version
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_update_available))
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String apkUrl = getResources().getString(R.string.dkcc_update_site) + "download/";

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
                        startActivity(browserIntent);
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }*/

    @Override
    public void onApplicationConnected(ApplicationMetadata appMetadata, String applicationStatus, String sessionId, boolean wasLaunched) {
    }

    @Override
    public void onApplicationDisconnected(int errorCode) {
    }

    @Override
    public void onApplicationStopFailed(int errorCode) {

    }

    @Override
    public void onApplicationConnectionFailed(int errorCode) {

    }

    @Override
    public void onApplicationStatusChanged(String appStatus) {

    }

    @Override
    public void onVolumeChanged(double value, boolean isMute) {

    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        Log.d(LOG_DEBUG, "message: " + message);
        CCBridge.onMessageReceived(castDevice, namespace, message);
    }

    @Override
    public void onMessageSendFailed(Status status) {
    }

    @Override
    public void onRemoved(CastDevice castDevice, String namespace) {
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onDisconnected() {
        castDeviceConnected = false;
        CCBridge.checkFloatingActionButton();
    }

    @Override
    public void onDisconnectionReason(@BaseCastManager.DISCONNECT_REASON int reason) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onCastDeviceDetected(MediaRouter.RouteInfo info) {
    }

    @Override
    public void onCastAvailabilityChanged(boolean castPresent) {
    }

    @Override
    public void onConnectivityRecovered() {
    }

    @Override
    public void onUiVisibilityChanged(boolean visible) {

    }

    @Override
    public void onReconnectionStatusChanged(int status) {

    }

    @Override
    public void onDeviceSelected(CastDevice device) {
    }

    @Override
    public void onFailed(int resourceId, int statusCode) {
    }
}
