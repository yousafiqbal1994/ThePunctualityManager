package tpm.employee.notifications;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import tpm.employee.R;
import tpm.employee.teachersmodule.CommentDetails;
import tpm.employee.teachersmodule.TeacherMainActivity;

/**
 * Created by Dell pc on 9/23/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String title;
    String message;
    String myID;
    String replyMSg;
    PowerManager.WakeLock wakeLock;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
         wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
//        String servicePing =remoteMessage.getData().get("messagee");
//        Log.e("servicePing","Service is pinged with message "+servicePing);
//
//        if(servicePing.equals("ping")){
//            if(!isMyServiceRunning(MyService.class)) {
//                Log.e("servicePing","Service Not running");
//            }else{
//                Log.e("servicePing","Service running");
//            }
//        }

        // Received to Students  variables
        title =remoteMessage.getData().get("title");
        message= remoteMessage.getData().get("msg");
        // Received to Teacher in Reply Back
        replyMSg = remoteMessage.getData().get("replymsg");
        myID = remoteMessage.getData().get("senderStudentID");
        //Receive notification on teacher Side
        if(myID != null && replyMSg !=null){ // A reply is Received
//            Log.e("checkings","Reply is received");
//            String parts [] =  replyMSg.split(",,,@@@Uc@Y@U...,,,");
//            String msg= parts[0]; // Actual reply message
//            String notID = parts[1]; // Notification ID to which this msg above is integrated as a comment/reply
            // Create a preference object  called "rp+[notID]"
//            String cID = "rp"+notID;
//            String nameOfStudent  = parts[2];
//            String idOfStudent = parts[3];
//            String timeofSending = parts[4];
//            writeIncommingComment(cID,msg,nameOfStudent,idOfStudent,timeofSending);
//            CommentDetails comment = new CommentDetails(cID,idOfStudent,msg, timeofSending, nameOfStudent);
//            addCommentLocally(comment,cID);
            String notificationID  = remoteMessage.getData().get("notificationID");
            String nameofStudent  = remoteMessage.getData().get("studentName");
            showNotificationToteacher(replyMSg,nameofStudent);

        }
        // Receive notification on Student Side
        if(title != null && message !=null){
//            String parts [] =  title.split(",,,@@@Uc@Y@U...,,,");
//            title =  parts[0]; // title
//            senderID =  parts[1]; // Sender ID
//            notificationID  = parts[2]; // This must be sent back to the teacher to store the replytext as comment in that notificaion ID preference
//            String nameofTeacher = parts[3];
            String sID = remoteMessage.getData().get("senderID");
            String nameofTeacher =  remoteMessage.getData().get("senderName");
            String notificationID =  remoteMessage.getData().get("notificationID");
            // Update the notification ID of the Saved preference during sending to this notificationID
//            updateNotificationID(title,message);
            showNotificationToStudent(title,sID,message,nameofTeacher,notificationID);
        }
    }

//    private void updateNotificationID(String titlee, String mssg) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FirebaseMessagingService.this);
//        Gson gson = new Gson();
////        context = con;
//        Map<String, ?> allEntries = prefs.getAll();
//        if(allEntries.size()>0){
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                String jsonn = prefs.getString(entry.getKey(),"");
//                String keyofCurrentObject = entry.getKey();
//                NotificationDetails obj = gson.fromJson(jsonn, NotificationDetails.class);
//                if(obj!=null){
//                    String initial = keyofCurrentObject.charAt(0)+""+keyofCurrentObject.charAt(1);
//                    if(initial.equals("nf")){
//                        if(obj.nTitle.equals(titlee) && obj.nMessage.equals(mssg)){
//
//                        }
//
////                        notificationsList.add(notifDetails);
//                    }
//                }
//            }
//        }
//    }


    public  boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) FirebaseMessagingService.this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void writeIncommingComment(String filename,String msg, String nameOfStudent, String IDofStudent, String time){
        try {
            FileOutputStream fOut = openFileOutput(filename,MODE_APPEND);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));
            bw.write(IDofStudent+",,,@@@Uc@Y@U...,,,"+msg+",,,@@@Uc@Y@U...,,,"+nameOfStudent+",,,@@@Uc@Y@U...,,,"+time);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addCommentLocally(CommentDetails comment,String cID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FirebaseMessagingService.this);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(comment);
        prefsEditor.putString(cID, json); // rp1,rp2,rp3,rp4....rpn
        prefsEditor.apply();
    }
    private void showNotificationToteacher(String ReplyMSg,String name) {
        wakeLock.acquire();
        Log.e("checkings","Teacher Notification");
        Intent showTaskIntent = new Intent(getApplicationContext(), TeacherMainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(2);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(name)
                .setContentText(ReplyMSg)
                .setSound(soundUri)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
        wakeLock.release();
    }

    private void showNotificationToStudent(String tit,String sId,  String msg,String nameofTeacher,String notificationID) {
        wakeLock.acquire();
        Intent i = new Intent(this,Notifications.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("senderID",sId);
        i.putExtra("title", tit);
        i.putExtra("msg", msg);
        i.putExtra("notificationID",notificationID);
        i.putExtra("nameofTeacher",nameofTeacher);
        Uri soundUri = RingtoneManager.getDefaultUri(2);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(tit)
                .setContentText(msg)
                .setSound(soundUri)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
        wakeLock.release();
    }

    private String getMyID() {
        SharedPreferences profile = FirebaseMessagingService.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");
    }

}
