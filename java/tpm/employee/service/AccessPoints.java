package tpm.employee.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

import tpm.employee.R;

public class AccessPoints extends AppCompatActivity {

    private  double biglevel= 999;
    String SSid;
    SessionManager manager;
    private ListView listview;
    private Button next;
    static List<ScanResult> list ;
    ArrayList<String> AParray;
    ArrayAdapter<String> APAdapter;
    public ArrayList<ScanResult> scanresult;
    public ArrayList<String> ssidlist;
    String Office="";
    private SparseBooleanArray sp;
    SharedPreferences sps;
    int inout ;
    String locationt ,checkin,checkou ;
    public static final String MyPREFERENCES = "ProfileDetails" ;
    String names ;
    String des ;
    String jls ;
    String sts ;
    String ets ;
    String id , pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_points);
        Bundle b = getIntent().getExtras();
         names = b.getString("name");
         des = b.getString("desi");
         jls = b.getString("est");
         sts = b.getString("st");
         ets = b.getString("et");
        pic = b.getString("pic");
       id = b.getString("id");


        sps = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        scanresult = new ArrayList<ScanResult>();
        ssidlist = new ArrayList<String>();
        manager=new SessionManager(this);
        listview = (ListView) findViewById(R.id.idlistview);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setItemChecked(2, true);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



            }
        });
        next= (Button) findViewById(R.id.next);


        AParray=new ArrayList<String>();

        //////////////////////////////////////////
        WifiConnectivity con;
        con = new WifiConnectivity(AccessPoints.this);
        try {
            scanresult = con.scanWIFI(AccessPoints.this);
            Log.e("Length", scanresult.size() + "");
            for (ScanResult sc : scanresult ) {
                ssidlist.add(String.valueOf(sc.SSID));
                // BSSID_LIST.put(String.valueOf(sc.SSID),String.valueOf(sc.BSSID));
            }
        } catch (Exception e) {
            //  Log.e("Ly bi", "A e o masla" + e);
            Toast.makeText(AccessPoints.this,"Problem:"+e.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.e("Ly bi", "Ab Okay Hai");
        APAdapter = new ArrayAdapter<String>(AccessPoints.this, android.R.layout.simple_list_item_multiple_choice, ssidlist);
        listview.setAdapter(APAdapter);
        String serverLink = getResources().getString(R.string.serverLink);
        String urlpostss=serverLink+getResources().getString(R.string.restart);
        new JsonTasktwo(getApplicationContext(),null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlpostss+id);

    }

    public String pickMac_List(SparseBooleanArray sps) {
        //int i = 0;
        String MAC="";
        Log.e("#####Value of sp",sps.size()+"");
        for (int k=0;k<sp.size();k++) {
            for (ScanResult sc : scanresult) {
                if (ssidlist.get(sps.keyAt(k)).equals(String.valueOf(sc.SSID))) {
                        Log.e("not",sc.SSID);
                        MAC += sc.SSID + "/" + sc.BSSID + "-";

                }
            }

        }
        return MAC;
    }


    class JsonTasktwo extends AsyncTask<String, Integer, String> {

        HttpURLConnection connection = null;
        BufferedReader reader = null;


        Context ctx;
        View v;
        int i=0;
        String j=null;
        JsonTasktwo(Context ctx, String j) {
            this.ctx = ctx;
            this.j=j;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ////////////////
            String context1 = params[0];
            try {



                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream streams = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(streams));
                StringBuffer buffers = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffers.append(line);
                }
                String finalJsons = buffers.toString();
                Log.e("testing", finalJsons);



                JSONArray ppArray = new JSONArray(finalJsons);

                for (int i = 1; i <= ppArray.length(); i++) {
                    JSONObject parentObject = ppArray.getJSONObject(i - 1);
                    locationt = parentObject.getString("location");
                    checkin = parentObject.getString("checkin");
                    checkou = parentObject.getString("checkout");


                }
                // getting Integer
                return buffers.toString();
                //return  buffer.toString();

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("No", " Internet");

            } catch (JSONException e) {

                e.printStackTrace();

            }  finally{
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
            ////////////////////
            ////////////////////
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  pd.dismiss();

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp = listview.getCheckedItemPositions();

                    Office+=pickMac_List(sp);
                    SharedPreferences.Editor editor = sps.edit();
                    editor.putString("office", Office);
                    editor.commit();
                    Log.e("Office", Office);

                    String serverLink = getResources().getString(R.string.serverLink);

                    String urlpostss=serverLink+getResources().getString(R.string.access);
                    Log.e("url", urlpostss+id+"&loc="+Office);
                    new JsonTask(getApplicationContext(),null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlpostss+id+"&loc="+Office);


                }
            });

        }


    }


    class JsonTask extends AsyncTask<String, Integer, String> {

        HttpURLConnection connection = null;
        BufferedReader reader = null;


        Context ctx;
        View v;
        int i=0;
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
            ////////////////
            String context1 = params[0];
            try {



                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream streams = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(streams));


                StringBuffer buffers = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffers.append(line);
                }
                String finalJsons = buffers.toString();
                Log.e("testing", finalJsons);




                // getting Integer
                return buffers.toString();
                //return  buffer.toString();

            } catch (MalformedURLException e) {

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
            ////////////////////
            ////////////////////
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  pd.dismiss();
                    Intent i= new Intent(AccessPoints.this,AttendancePage.class);
                    i.putExtra("id", id);
                    i.putExtra("name", names);
                    i.putExtra("desi", des);
                    i.putExtra("est", jls);
                    i.putExtra("st", sts);
                    i.putExtra("et", ets);
                    i.putExtra("pic", pic);
                    i.putExtra("inout", inout);
                    i.putExtra("loc", locationt);
                    i.putExtra("checkin", checkin);
                    i.putExtra("checkout", checkou);
                    AccessPoints.this.startActivity(i);
                    finish();

        }


    }




}
