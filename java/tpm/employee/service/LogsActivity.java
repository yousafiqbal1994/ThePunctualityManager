package tpm.employee.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import tpm.employee.R;

/**
 * Created by Infinal on 4/27/2017.
 */

public class LogsActivity extends AppCompatActivity {

    public ArrayList<LogsClass> logs= new ArrayList<LogsClass>();
    public ArrayAdapter<LogsClass> adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        adapter = new MyListAdapter();
        String serverLink = getResources().getString(R.string.serverLink);
        String urlpostss=serverLink+getResources().getString(R.string.getLogs);
        progressBar.setVisibility(View.VISIBLE);
        new JsonTask(getApplicationContext(),null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlpostss+getMyID());
    }

    public void populateListView() {
        ListView logsList = (ListView) findViewById(R.id.logsList);
        logsList.setAdapter(adapter);
    }


    private class MyListAdapter extends ArrayAdapter<LogsClass> {
        MyListAdapter() {
            super(LogsActivity.this, R.layout.logs_single_item, logs);
        }
        @NonNull
        @Override
        public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {

            View itemView = null;
            if (convertView == null) {
                itemView = getLayoutInflater().inflate(R.layout.logs_single_item, parent, false);
            } else {
                itemView = convertView;
            }
            final LogsClass log = logs.get(position);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.pdName = (TextView) itemView.findViewById(R.id.pdName);
            viewHolder.checkIn = (TextView) itemView.findViewById(R.id.checkInTime);
            viewHolder.checkOut = (TextView) itemView.findViewById(R.id.checkOutTime);
            viewHolder.pdName.setText(log.getPdName());
            String parts[] = log.getCheckInTime().split(" ");
            viewHolder.checkIn.setText(parts[1]);
            if(log.getCheckOutTime().equals("null")){
                viewHolder.checkOut.setText("CheckedIN");
            }else{
                String partss[] = log.getCheckOutTime().split(" ");
                viewHolder.checkOut.setText(partss[1]);
            }
            return itemView;
        }
    }

    public class ViewHolder {
        TextView pdName,checkIn,checkOut;
    }

    private String getMyID() {
        SharedPreferences profile = LogsActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");
    }
    private class JsonTask extends AsyncTask<String, Integer, String> {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Context ctx;
        String j=null;
        JsonTask(Context ctx, String j) {
            this.ctx = ctx;
            this.j=j;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Log.e("xxxx",url.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream streams = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(streams));
                StringBuilder buffers = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffers.append(line);
                }
                String finalJsons = buffers.toString();
                JSONArray logsArray = new JSONArray(finalJsons);
                for (int i = 0; i < logsArray.length(); i++) {
                    try {
                        JSONObject json = logsArray.getJSONObject(i);
                        String pdName = json.getString("name");
                        String checkIn = json.getString("checkin");
                        String  checkOut = json.getString("checkout");
                        LogsClass log = new LogsClass(pdName,checkIn,checkOut);
                        logs.add(log);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return buffers.toString();

            } catch (MalformedURLException  | JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("No", " Internet");

            } finally{
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resultFromServer) {
            super.onPostExecute(resultFromServer);
            Log.e("serverrrr","From Server \n "+resultFromServer);
            populateListView();
            progressBar.setVisibility(View.GONE);
        }

    }
}
