package tpm.employee.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Context.ALARM_SERVICE;


public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            Intent intentt = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, 234324243, intentt, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            int interval = 3000; // 3 second
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
//            Toast.makeText(context, "Alarm will be called after 5 every seconds",Toast.LENGTH_SHORT).show();
            Log.e("checkstrength","Device Rebooted now check service");
        }
    }
}
