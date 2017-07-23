package tpm.employee.teachersmodule;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.R;

import static android.content.Context.MODE_APPEND;

/**
 * Created by YouCaf Iqbal on 3/18/2017.
 */
public class SendTab extends Fragment {
    FancyButton sendAll;
    View v;
    TextView status;
    String titleOnly;
    String res;
    ProgressBar pb;
    String titleMyIdNotificationID;
    String notID;
    String mesage,titlee;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.sendtablayout,container,false);
        if (container == null) {
            return null;
        }

        pb = (ProgressBar) v.findViewById(R.id.pb);
        pb.setVisibility(View.GONE);
        status = (TextView) v.findViewById(R.id.status);
        sendAll = (FancyButton)v.findViewById(R.id.all);
        sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialog();
            }
        });
        return  v;
    }
//
//    private void showDialog() {
//
//        LayoutInflater li = LayoutInflater.from(getActivity());
//        View promptsView;
//        promptsView = li.inflate(R.layout.sendnotificationdialog, null);
//        final EditText title = (EditText) promptsView
//                .findViewById(R.id.titleText);
//        final EditText message = (EditText) promptsView
//                .findViewById(R.id.messageText);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));
//        alertDialogBuilder.setView(promptsView);
//        alertDialogBuilder
//                .setPositiveButton("Send",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                updateNotificationID();
//                                titleOnly = title.getText().toString();
//                                notID = "nf"+getNotificationID();
////                                titleMyIdNotificationID = title.getText().toString()+",,,@@@Uc@Y@U...,,,"+getMyID()+",,,@@@Uc@Y@U...,,,"+getNotificationID()+",,,@@@Uc@Y@U...,,,"+getMyName();
//                                titlee = title.getText().toString();
//                                mesage=message.getText().toString();
//                                new sendAsyn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                                // Add this notification to shared preference
////                                Log.e("notifcatioCounter",getNotificationID());
////                                NotificationDetails notification = new NotificationDetails(notID,titleOnly,mesage,getMyID(),"all",localTime());
////                                addNotificationLocally(notification,getNotificationID());
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//    }

    private void addNotificationLocally(NotificationDetails notification,String notificatioID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notification);
        prefsEditor.putString("nf"+notificatioID, json);
        prefsEditor.apply();
    }

    private String getNotificationID() {
        SharedPreferences profile = getActivity().getSharedPreferences("NotificationCounter", MODE_APPEND);
        int counter = profile.getInt("counter",0);
       return String.valueOf(counter);
    }

    private void updateNotificationID() {
        SharedPreferences messagesCounterPreference = getActivity().getSharedPreferences("NotificationCounter", MODE_APPEND);
        SharedPreferences.Editor messagesCounterEditor =  messagesCounterPreference.edit();
        int currentUserCounterValue = messagesCounterPreference.getInt("counter",0);
        if(currentUserCounterValue == 0){
            // first
            messagesCounterEditor.putInt("counter",1);
        }else{
            // already unread messages
            messagesCounterEditor.putInt("counter",currentUserCounterValue+1);
        }
        messagesCounterEditor.apply();
    }



    private String getMyID() {
        SharedPreferences profile = getActivity().getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");

    }




    public class sendAsyn extends AsyncTask<Void,Void,Void> {
        JSONObject json =null;
        @Override
        protected Void doInBackground(Void... voids) {
            String serverLink = getResources().getString(R.string.serverLink);
            OkHttpClient client =  new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("id",getMyID())
                    .add("msg",mesage)
                    .add("name",getMyName())
                    .add("title",titlee);
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(serverLink+getResources().getString(R.string.sendnotification))
                    .post(formBody)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            status.setText("Sending...");
            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            status.setText("");
            pb.setVisibility(View.GONE);
            NotificationDetails notification = new NotificationDetails(res,titleOnly,mesage,getMyID(),"all",localTime());
            Log.e("xxxxx",res);
            addNotificationLocally(notification,notID);

        }

    }

    private String getMyName() {
        SharedPreferences profile = getActivity().getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("name","0");

    }

    private String localTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, K:mm a");
        return sdf.format(now);
    }
}
