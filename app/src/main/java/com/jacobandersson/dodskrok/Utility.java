package com.jacobandersson.dodskrok;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Jacob on 2015-12-20.
 */
public class Utility {

    public static void setListViewHeightBasedOnChildren(ListView listview, Context context) {
        ListAdapter listAdapter = listview.getAdapter();
        if (listAdapter == null)
            return;

        int totalHeight = listAdapter.getCount() * 57;
        totalHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, totalHeight, context.getResources().getDisplayMetrics());

        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight;
        listview.setLayoutParams(params);
        listview.requestLayout();
    }

    public static String getNamespace(String subNamespace, Context context) {
        return context.getResources().getString(R.string.cast_namespace) + "." + subNamespace;
    }

}
