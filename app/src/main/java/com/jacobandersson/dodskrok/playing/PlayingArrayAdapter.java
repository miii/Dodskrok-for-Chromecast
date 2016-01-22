package com.jacobandersson.dodskrok.playing;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacobandersson.dodskrok.R;
import com.jacobandersson.dodskrok.cast.CCBridge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jacob on 2015-12-20.
 */
public class PlayingArrayAdapter extends BaseAdapter {

    public enum Types {
        PLAYER,
        MISSION
    }
    private Types type;

    private HashMap<String, Integer> locale;

    private PlayingActivity activity;
    private static LayoutInflater inflater = null;

    private ArrayList<String> list;
    private OnClickListener clickListener;

    public PlayingArrayAdapter(PlayingActivity activity, Types type, OnClickListener oCL) {
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = new ArrayList<>();
        this.clickListener = oCL;

        setLocale(type);
    }

    public ArrayList<String> getList() {
        return list;
    }

    private void setLocale(Types type) {
        locale = new HashMap<String, Integer>();
        this.type = type;

        switch (type) {
            case PLAYER:
                locale.put("dialog_remove_title", R.string.dialog_remove_person_title);
                locale.put("dialog_remove_content", R.string.dialog_remove_person);
                break;
            case MISSION:
                locale.put("dialog_remove_title", R.string.dialog_remove_mission_title);
                locale.put("dialog_remove_content", R.string.dialog_remove_mission);
                break;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View elementView = inflater.inflate(R.layout.listview_element, viewGroup, false);

        final int index = i;

        if (type == Types.MISSION) {
            ImageView icon = (ImageView) elementView.findViewById(R.id.listview_element_icon);
            icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_work_black_24dp));
        }

        TextView nameTextView = (TextView) elementView.findViewById(R.id.listview_element_string);
        nameTextView.setText(list.get(i));

        ImageView removeImageView = (ImageView) elementView.findViewById(R.id.listview_element_remove);
        removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(locale.get("dialog_remove_title"))
                        .setMessage(locale.get("dialog_remove_content"))
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clickListener.onPositiveClick(index);
                            }
                        })
                        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
            }
        });

        return elementView;
    }

    public interface OnClickListener {
        void onPositiveClick(int index);
    }
}
