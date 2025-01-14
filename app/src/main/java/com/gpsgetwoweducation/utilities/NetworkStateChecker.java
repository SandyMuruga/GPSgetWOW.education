package com.gpsgetwoweducation.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    private DataBaseHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        db = new DataBaseHelper(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName(
                                cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME))
                        );
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    public boolean getStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void saveName(final int id, final String name) {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            if (!obj.getBoolean("error")) {
//                                //updating the status in sqlite
//                                db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);
//
//                                //sending the broadcast to refresh the list
//                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("name", name);
//                return params;
//            }
//        };
//
//        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
