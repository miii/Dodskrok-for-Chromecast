package com.jacobandersson.dodskrok.playing;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jacobandersson.dodskrok.BuildConfig;
import com.jacobandersson.dodskrok.R;
import com.jacobandersson.dodskrok.Utility;
import com.jacobandersson.dodskrok.cast.CCBridge;

public class PlayingActivityModel {

    private PlayingActivity activity;

    private float settingsTimeMin = 1;
    private float settingsTimeMax = 3;

    private FloatingActionButton fab_play;

    public ListView missionsListView;
    public ListView playersListView;

    public PlayingArrayAdapter playersArrayAdapter;
    public PlayingArrayAdapter missionsArrayAdapter;

    public boolean castDeviceConnected = false;

    public PlayingActivityModel(PlayingActivity activity) {
        this.activity = activity;
        missionsArrayAdapter = new PlayingArrayAdapter(activity, PlayingArrayAdapter.Types.MISSION, new PlayingArrayAdapter.OnClickListener() {
            @Override
            public void onPositiveClick(int index) {
                missionsArrayAdapter.getList().remove(index);
                updateAdapter(PlayingArrayAdapter.Types.MISSION);
                CCBridge.removeMission(index);
            }
        });
        playersArrayAdapter = new PlayingArrayAdapter(activity, PlayingArrayAdapter.Types.PLAYER, new PlayingArrayAdapter.OnClickListener() {
            @Override
            public void onPositiveClick(int index) {
                playersArrayAdapter.getList().remove(index);
                updateAdapter(PlayingArrayAdapter.Types.PLAYER);
                CCBridge.removePlayer(index);
            }
        });
    }

    public void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.dialog_time));

        LayoutInflater inflater = activity.getLayoutInflater();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.dialog_not_admin))
                .setMessage(activity.getResources().getString(R.string.dialog_not_admin_content))
                .setNeutralButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

    public void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        //Crashlytics.setUserIdentifier("12345");
        //Crashlytics.setUserEmail("user@fabric.io");
        //Crashlytics.setUserName("Test User");
    }

    public void updateFloatingActionButton() {
        Log.e("DKAPP-FAB", "Updating FAB Button");
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab_play);

        if (listsConditionsIsOK())
            Log.e("DKAPP-FAB", "Cond OK");
        if (castDeviceConnected)
            Log.e("DKAPP-FAB", "Connected");

        if (listsConditionsIsOK() && castDeviceConnected) {
            Log.e("DKAPP-FAB", "Ready");
            int bg = ContextCompat.getColor(activity, R.color.colorPrimaryDark);
            int color = ContextCompat.getColor(activity, android.R.color.white);

            fab.setBackgroundTintList(ColorStateList.valueOf(bg));
            fab.setColorFilter(color);
        } else {
            Log.e("DKAPP-FAB", "Not ready");
            int bg = ContextCompat.getColor(activity, android.R.color.white);
            int color = ContextCompat.getColor(activity, R.color.background);

            fab.setBackgroundTintList(ColorStateList.valueOf(bg));
            fab.setColorFilter(color);
        }
    }

    public boolean listsConditionsIsOK() {
        return playersArrayAdapter.getCount() >= 2 && missionsArrayAdapter.getCount() >= 1;
    }

    public void addMissionToAdapter(String name) {
        missionsArrayAdapter.getList().add(name);
        updateAdapter(PlayingArrayAdapter.Types.MISSION);
    }

    public void addPlayerToAdapter(String name) {
        playersArrayAdapter.getList().add(name);
        updateAdapter(PlayingArrayAdapter.Types.PLAYER);
    }

    public void setFooterText() {
        TextView appName = (TextView) activity.findViewById(R.id.app_name);
        String version = BuildConfig.VERSION_NAME;
        String appNameLabel = activity.getResources().getString(R.string.dkcc_app_name) + " " + version;
        appName.setText(appNameLabel);
    }

    public void setupFabButtons() {
        fab_play = (FloatingActionButton) activity.findViewById(R.id.fab_play);
        final FloatingActionButton fab_pause = (FloatingActionButton) activity.findViewById(R.id.fab_pause);

        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar snackbar = Snackbar.make(
                        activity.findViewById(R.id.coordLayout),
                        R.string.fab_play_error,
                        Snackbar.LENGTH_LONG
                );
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));

                if (listsConditionsIsOK()) {
                    if (castDeviceConnected) {
                        CCBridge.play();
                        activity.getSupportActionBar().setTitle(R.string.actionbar_title);
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
                activity.getSupportActionBar().setTitle(R.string.paused);
                view.setVisibility(View.GONE);
                fab_play.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setupListViews() {
        missionsListView = (ListView) activity.findViewById(R.id.missions_listview);
        missionsListView.setAdapter(missionsArrayAdapter);
        Utility.setListViewHeightBasedOnChildren(missionsListView, activity);

        playersListView = (ListView) activity.findViewById(R.id.persons_listview);
        playersListView.setAdapter(playersArrayAdapter);
        Utility.setListViewHeightBasedOnChildren(playersListView, activity);
    }

    public void setupGameButtons() {
        Button addPersonButton = (Button) activity.findViewById(R.id.addPersonButton);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayingInputDialog pid = new PlayingInputDialog(activity, PlayingArrayAdapter.Types.PLAYER);
                pid.setOnPositiveClickListener(new PlayingInputDialog.onClickListener() {
                    @Override
                    public void onClick(String name) {
                        addPlayerToAdapter(name);
                        CCBridge.addPlayer(name);
                        updateFloatingActionButton();
                    }
                });
            }
        });

        Button addMissionButton = (Button) activity.findViewById(R.id.addMissionButton);
        addMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayingInputDialog pid = new PlayingInputDialog(activity, PlayingArrayAdapter.Types.MISSION);
                pid.setOnPositiveClickListener(new PlayingInputDialog.onClickListener() {
                    @Override
                    public void onClick(String name) {
                        addMissionToAdapter(name);
                        CCBridge.addPlayer(name);
                        updateFloatingActionButton();
                    }
                });
            }
        });
    }

    private void updateAdapter(PlayingArrayAdapter.Types type) {

        PlayingArrayAdapter adapter = null;
        ListView listview = null;

        switch(type) {
            case MISSION:
                adapter = missionsArrayAdapter;
                listview = missionsListView;
                break;
            case PLAYER:
                adapter = playersArrayAdapter;
                listview = playersListView;
                break;
        }

        adapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(listview, activity);
    }

}
