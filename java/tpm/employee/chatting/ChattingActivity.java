package tpm.employee.chatting;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.R;
import tpm.employee.teachersmodule.TeacherUnReadMessageService;

/**
 * Created by YouCaf Iqbal on 3/5/2017.
 */

public class ChattingActivity extends AppCompatActivity {

    public String SenderName,ReceiverID,SenderID,ReceiverName;
    public ProgressDialog pDialog;
    private EditText messageBodys;
    public static String flag=null;
    public static int counter=0;
    JSONObject jsonn =null;
    String messageBody;
    private BroadcastReceiver receiver = null;
    MessageTabActivity chatsFragment;
    private MessageAdapter messageAdapter;
    ImageButton sendbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);
        registerBroadcastReceiver();
        chatsFragment = new MessageTabActivity();
        sendbtn= (ImageButton) findViewById(R.id.sendButton);
        Intent intent = getIntent();
        ReceiverName = intent.getStringExtra("RECEIVER_NAME");
        getSupportActionBar().setTitle(ReceiverName);
        SenderName = intent.getStringExtra("Sender_NAME");
        ReceiverID = intent.getStringExtra("ReceiverID");
        SenderID = intent.getStringExtra("SenderID");
        ListView messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        messageBodys = (EditText) findViewById(R.id.messageBodyField);
        updateUnreadMessageCounter();
        flag = ReceiverID;
        new chatHistoryAsyn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        ShowChatHistory();
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void registerBroadcastReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                if(success){
                    // Append msg to end of chat box // Receiving Message
                    if(getMyLevel().equals("5")){ // Student is logged in
                        if(UnReadMessageService.userHint.equals(ReceiverID)){
                            if(counter==0){
                                final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.chatsound);
                                mp.start();
                                String details = MessageAdapter.DIRECTION_INCOMING+",,,"+ReceiverName+",,,"+localTime();
                                messageAdapter.addMessage(UnReadMessageService.messageBody,details);
                                counter++;
                            }

                        }
                    }else{ // For now its teacher

                        if(TeacherUnReadMessageService.userHint.equals(ReceiverID)){
                            if(counter==0){
                                final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.chatsound);
                                mp.start();
                                String details = MessageAdapter.DIRECTION_INCOMING+",,,"+ReceiverName+",,,"+localTime();
                                messageAdapter.addMessage(TeacherUnReadMessageService.messageBody,details);
                                counter++;
                            }

                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(ChattingActivity.this).registerReceiver(receiver, new IntentFilter("chatbox"));
    }

    public void ShowChatHistory() {
        if (isFilePresent(SenderID+ReceiverID)) {
            String FilePath = ChattingActivity.this.getFilesDir()+"/mh"+SenderID+ReceiverID;
            File file = new File(FilePath);
            try {
                LineNumberReader lnr = new LineNumberReader(new FileReader(file));
                lnr.skip(Long.MAX_VALUE);
                Log.e("file","no of lines in file is "+lnr.getLineNumber());
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                for (int i = 0; i < lnr.getLineNumber(); i++) {
                    if ((line = br.readLine()) != null) {
                        String[] parts = line.split(getMessageSeparator());
                        String partID = parts[0];
                        String Message = parts[1];
                        String Name = parts[2];
                        String Time = parts[3];
                        if(partID.equals(getMyID())){
                            // Sent message
                            String details = MessageAdapter.DIRECTION_OUTGOING+",,,"+getMyName()+",,,"+Time;
                            messageAdapter.addMessage(Message,details);
                        }else{
                            // Received message
                            String details = MessageAdapter.DIRECTION_INCOMING+",,,"+ReceiverName+",,,"+Time;
                            messageAdapter.addMessage(Message, details);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private String getMessageSeparator(){
       return ",,,@@@Uc@Y@U...,,,";
    }
    private void updateUnreadMessageCounter() {
        Log.e("hoi",ReceiverID +" in MessagingActivity");
        SharedPreferences messagesCounterPreference = ChattingActivity.this.getSharedPreferences("MessagesCounter", MODE_PRIVATE);
        SharedPreferences.Editor messagesCounterEditor =  messagesCounterPreference.edit();
        messagesCounterEditor.putInt(ReceiverID,0); // set unread messages to 0
        messagesCounterEditor.apply();
    }

    public class sendMessageAsyn extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Calendar calendar = new GregorianCalendar();
            TimeZone timeZone = calendar.getTimeZone();
            String timee = timeZone.getID();
            // Send request to server
            String serverLink = getResources().getString(R.string.serverLink);
            OkHttpClient client =  new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("receiver_ID",ReceiverID)
                    .add("sender_ID",SenderID)
                    .add("zoneeee",timee)
                    .add("body",messageBody);
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(serverLink+getResources().getString(R.string.sendmyMSG))
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                jsonn = new JSONObject(res);
                // Do something with the response.
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(jsonn!=null){
                try {
                    String checking = jsonn.getString("messageResponse");
                    if(checking.equals("Sent")){
                        addAsMostRecent();
                        updateLastMsgSent(messageBody);
                        if(checkInList(ReceiverID).equals("0")){ // add user to list
                            addUserToList(ReceiverID);
                            chatsFragment.addPerson(new ChatDetails(SenderName,SenderID,ReceiverID,ReceiverName),SenderID+ReceiverID,getApplicationContext());
                        }
//                        writeSentMessage(messageBody);
                    }else{
                        Toast.makeText(ChattingActivity.this,"Message Sending Failed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    @Override
    protected void onPause() {
        flag = null;
        LocalBroadcastManager.getInstance(ChattingActivity.this).unregisterReceiver(receiver);
        super.onPause();
    }
    @Override
    protected void onResume() {
        flag = ReceiverID;
        LocalBroadcastManager.getInstance(ChattingActivity.this).registerReceiver(receiver, new IntentFilter("chatbox"));
        super.onResume();
    }

    public void writeSentMessage(String messageBody){
        try {
            FileOutputStream fOut = openFileOutput("mh"+SenderID+ReceiverID,MODE_APPEND);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));
            bw.write(SenderID+getMessageSeparator()+messageBody+getMessageSeparator()+SenderName+getMessageSeparator()+localTime());
            bw.newLine();
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateLastMsgSent(String messageText){
        SharedPreferences lastMessagesPreference = ChattingActivity.this.getSharedPreferences("LastMessage", MODE_APPEND);
        SharedPreferences.Editor messagesCounterEditor =  lastMessagesPreference.edit();
        messagesCounterEditor.putString(ReceiverID,messageText);
        messagesCounterEditor.apply();
    }

    private void addAsMostRecent() {
        SharedPreferences.Editor profilerEditor;
        SharedPreferences userProfile;
        userProfile = ChattingActivity.this.getSharedPreferences("MostRecentChatter", MODE_APPEND);
        profilerEditor = userProfile.edit();
        profilerEditor.putString("chatterID","mp"+SenderID+ReceiverID);
        profilerEditor.apply();
    }

    private String checkInList(String receiverID) {
        String m="0";
        SharedPreferences chattersList = ChattingActivity.this.getSharedPreferences("ChattersList", MODE_APPEND);
        Map<String, ?> allEntries = chattersList.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if(receiverID.equals(entry.getKey())){
                m =  chattersList.getString(entry.getKey(),"0");
            }
        }
        return m; //user is not in list, m = 0  returned
    }
    private void addUserToList(String receiverID){
        SharedPreferences chattersList = ChattingActivity.this.getSharedPreferences("ChattersList", MODE_APPEND);
        SharedPreferences.Editor profilerEditor = chattersList.edit();
        profilerEditor.putString(receiverID, receiverID);
        profilerEditor.apply();
    }

    private void sendMessage() {

        messageBody = messageBodys.getText().toString().trim();
        if (messageBody.isEmpty() || messageBody.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) ChattingActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            String details = MessageAdapter.DIRECTION_OUTGOING+",,,"+SenderName+",,,"+localTime();
            messageAdapter.addMessage(messageBody, details); // Update the UI...Set a sending spinner
            messageBodys.setText("");
            new sendMessageAsyn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    public class chatHistoryAsyn extends AsyncTask<Void,Void,Void> {
        JSONObject json =null;
        @Override
        protected Void doInBackground(Void... voids) {
            String serverLink = getResources().getString(R.string.serverLink);
            OkHttpClient client =  new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            FormBody.Builder formBuilder = new FormBody.Builder() .add("ReceiverID",ReceiverID)
                    .add("SenderID",SenderID);
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(serverLink+getResources().getString(R.string.getHistory))
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                json = new JSONObject(res);
                // Do something with the response.
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChattingActivity.this);
            pDialog.setMessage("Loading chat...");
//            Toast.makeText(ChattingActivity.this,"Loading Chat",Toast.LENGTH_SHORT).show();
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(json!=null){
                showHistory(json);
            }
            pDialog.dismiss();
        }

    }
    private void showHistory(JSONObject json) {

        try {
            JSONObject jObject = null;
            for (int i = 1; i < json.length() + 1; i++) {
                jObject = json.getJSONObject(i+"");
                String messageID = jObject.getString("messageID");
                String senderID = jObject.getString("senderID");
                String receiverID = jObject.getString("receiverID");
                String messageTime = jObject.getString("messgeTime");
                String messageText = jObject.getString("messageText");
                String unreadFlag = jObject.getString("unreadFlag");
                if(senderID.equals(getMyID())){
                    // Sent message
                    String details = MessageAdapter.DIRECTION_OUTGOING+",,,"+getMyName()+",,,"+messageTime;
                    messageAdapter.addMessage(messageText,details);
                }else{
                    // Received message
                    String details = MessageAdapter.DIRECTION_INCOMING+",,,"+ReceiverName+",,,"+messageTime;
                    messageAdapter.addMessage(messageText, details);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("exception a",e.toString());
        }
    }

    private String getMyID() {
        SharedPreferences profile = ChattingActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");

    }
    private String getMyName() {
        SharedPreferences profile = ChattingActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("name","0");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //counter=0;
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private String localTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, K:mm a");
        return sdf.format(now);
    }
    public boolean isFilePresent(String fname){
        fname = "mh"+fname;
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private String getMyLevel() {
        SharedPreferences profile = ChattingActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("adminLevel","0");
    }
}
