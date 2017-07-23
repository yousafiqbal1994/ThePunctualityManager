package tpm.employee.teachersmodule;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 3/22/2017.
 */

public class DetailedNotificationActivtiy extends AppCompatActivity {

    public ArrayList<CommentDetails> commentsList= new ArrayList<CommentDetails>();
    public ArrayAdapter<CommentDetails> adapter;
    SharedPreferences prefs;
    public Context context;
    ListView list;
    View v;
    TextView titleText,msgText;
    String nID; // Replace first 2 words of nID nf with rp you will get the replies preference of this sent notification

    String cID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statuscomments_layout);
        list = (ListView) findViewById(R.id.idlistview);

        titleText = (TextView) findViewById(R.id.titleText);
        msgText = (TextView) findViewById(R.id.msgText);
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("title"));
        titleText.setText(intent.getStringExtra("title"));
        msgText.setText(intent.getStringExtra("message"));
        nID = intent.getStringExtra("id");
        Log.e("niID",nID);
        new sendAsyn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // This is the notification ID that you will send to server to get the replies for and add those replies as
        // commentsList.add(commentDetails);

//        cID = "rp"+nID; // Now it is rp[nf#],  in this preference the comment for this notification is stored
//        Log.e("ssss",cID);
        //refreshListView();
        // Send query to Server get All the replies for that notification

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
            Log.e("niID",nID);
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("id",nID);
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(serverLink+getResources().getString(R.string.getReplies))
                    .post(formBody)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String res = response.body().string();
                Log.e("asdsad",res);
                json = new JSONObject(res);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            status.setText("");
//            pb.setVisibility(View.GONE);
            if(json!=null){
                Log.e("asdsad","Json  not null");
                addObjects(json);
            }else{
                Log.e("asdsad","Json  null");
            }
        }

    }

    private void addObjects(JSONObject json) {

        try {
            JSONObject jObject = null;
            for (int i = 1; i < json.length() + 1; i++) {
                jObject = json.getJSONObject(i+"");
                String msg = jObject.getString("reply");
                String IDofStudent = jObject.getString("idFrom");
                String Time = jObject.getString("Timme");
                String sendername = jObject.getString("sendername");
                Log.e("asdsad",msg+IDofStudent+Time+sendername);
                CommentDetails commentDetails = new CommentDetails("1",IDofStudent,msg,Time,sendername); // Name of student missing
                commentsList.add(commentDetails);
            }
            refreshListView();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("exception a",e.toString());
        }
    }
    private void refreshListView(){
//        commentsList.clear();
//        addAllthePeople();
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


//    public void addAllthePeople() {
//        // Read line form file split it and add as object in CommentDetails
//        if (isFilePresent(cID)) {
//
//            String FilePath = DetailedNotificationActivtiy.this.getFilesDir()+"/"+cID;
//            File file = new File(FilePath);
//            try {
//                LineNumberReader lnr = new LineNumberReader(new FileReader(file));
//                lnr.skip(Long.MAX_VALUE);
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                String line;
//                for (int i = 0; i < lnr.getLineNumber(); i++) {
//                    if ((line = br.readLine()) != null) {
//                        String[] parts = line.split(",,,@@@Uc@Y@U...,,,");
//                        String IDofStudent = parts[0];
//                        String msg = parts[1];
//                        String nameOfStudent = parts[2];
//                        String Time = parts[3];
////                        CommentDetails commentDetails = new CommentDetails(cID,IDofStudent,msg,Time,nameOfStudent);
////                        commentsList.add(commentDetails);
//                    }
//                }
//
//            } catch (Exception e) {
//
//            }
//        }
//
//    }


    public class MyListAdapter extends ArrayAdapter<CommentDetails> {

        public MyListAdapter() {
            super(DetailedNotificationActivtiy.this, R.layout.comment_item_view, commentsList);
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {

            View itemView = null;
            if (convertView == null) {
                itemView = getLayoutInflater().inflate(R.layout.comment_item_view, parent, false);
            } else {
                itemView = convertView;
            }
            ViewHolder viewHolder = new ViewHolder();
            // Find the comment to work with.
            final CommentDetails currentPerson = commentsList.get(position);
            viewHolder.Msg = (TextView) itemView.findViewById(R.id.Message);
            viewHolder.time = (TextView) itemView.findViewById(R.id.textView);
            viewHolder.time.setText(currentPerson.getcSendingTime());
            viewHolder.Msg.setText(currentPerson.getcText());
            viewHolder.title = (TextView) itemView.findViewById(R.id.title);
            viewHolder.title.setText(currentPerson.getcSenderName());
            return itemView;

        }
    }

    public class ViewHolder {
        TextView title;
        TextView Msg,time;
    }

    public boolean isFilePresent(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
}
