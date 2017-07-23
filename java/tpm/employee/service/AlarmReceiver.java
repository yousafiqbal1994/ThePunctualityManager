package tpm.employee.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_APPEND;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("servxxx","Called after 3 secs");
        if(!isServiceRunning(context)) {
            context.startService(new Intent(context, MyService.class));
        }else{
            Log.e("servxxx","Already running");
        }
    }


    private boolean apkStatus(Context c) {
        SharedPreferences profile = c.getSharedPreferences("ApkFlag", MODE_APPEND);
        return profile.getBoolean("apk",false);
    }
    private boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String getMyAdminLevel(Context c) {
        SharedPreferences profile = c.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("adminLevel","0");
    }
    private void showToast(final String text,final Context context) {
        Handler h = new Handler(context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
            }
        });
    }
}

