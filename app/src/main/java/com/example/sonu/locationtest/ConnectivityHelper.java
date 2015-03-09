package com.example.sonu.locationtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by sonu on 29/1/15.
 */
public class ConnectivityHelper {

    private Activity _context;
    private int LOC_INTENT = 121;

    public ConnectivityHelper(Activity context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    /**
     * Show an alert message
     *
     * @param title     title to be displayed
     * @param msg       message to to be shown
     * @param enableGPS show GPS warning. true to show.
     */
    public void showAlert(String title, String msg, boolean enableGPS) {
        AlertDialog.Builder alert = new AlertDialog.Builder(_context);
        alert.setTitle(title);
        alert.setMessage(msg);
        if (enableGPS) {
            alert.setPositiveButton("Enable GPS",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            _context.startActivityForResult(viewIntent, LOC_INTENT);

                        }
                    });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    _context.finish();
                    dialog.dismiss();
                }
            });
        } else {
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _context.finish();
                    dialog.dismiss();
                }
            });
        }
        alert.setIcon(R.drawable.ic_launcher);
        alert.show();
    }

}