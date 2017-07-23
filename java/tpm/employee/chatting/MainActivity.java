package tpm.employee.chatting;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.cv.MainCVActivity;
import tpm.employee.R;
import tpm.employee.service.AlarmReceiver;
import tpm.employee.service.LogsActivity;
import tpm.employee.service.LogsSummaryAndDetails;
import tpm.employee.service.MyService;

public class MainActivity extends AppCompatActivity{
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavigationView;
    public FragmentManager mFragmentManager;
    public ProgressDialog pDialog;
    public FragmentTransaction mFragmentTransaction;
    CircleImageView dp;
    final Handler handler = new Handler();
//    TextView _userName;
    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymain);
        pDialog = new ProgressDialog(MainActivity.this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        assert mNavigationView != null;
        View headerLayout = mNavigationView.getHeaderView(0); // 0-index header
        dp = (CircleImageView)headerLayout.findViewById(R.id.userImage);
//        _userName = (TextView) headerLayout.findViewById(R.id.name);
        setValues();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabsView()).commit();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new TabsView()).commit();
                }
                if (menuItem.getItemId() == R.id.nav_item_cv) {
                    Intent intent = new Intent(MainActivity.this,MainCVActivity.class);
                    startActivity(intent);
                }

//                if (menuItem.getItemId() == R.id.nav_item_logout) {
//                    showLogoutDialog();
//                }

                if (menuItem.getItemId() == R.id.nav_item_details) {
                    Intent intent = new Intent(MainActivity.this,LogsSummaryAndDetails.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.nav_item_appinfo) {
                    Intent intent = new Intent(MainActivity.this,AppInfo.class);
                    startActivity(intent);
                }
//                if (menuItem.getItemId() == R.id.nav_item_logs) {
//                    //Toast.makeText(MainActivity.this,"logs",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MainActivity.this,LogsActivity.class);
//                    startActivity(intent);
//                }
                return false;
            }

        });
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        dp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Start the service that listens for Unread messages // (When users login and internet is available)
        startService(new Intent(this, UnReadMessageService.class));

    }

    private String getMyOffices() {
        SharedPreferences profile = MainActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("office","0");

    }

    private String getMyID() {
        SharedPreferences profile = MainActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");

    }
    private String getMyName() {
        SharedPreferences profile = MainActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("name","0");
    }
    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    public void setValues() {
//        _userName.setText(getMyName());
        SharedPreferences userDetails = MainActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        String imageString = userDetails.getString("picture","");
        if (imageString.length() >= 80) {
            // decode string back to image and set it
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            dp.setImageBitmap(decodedByte);
        }
    }
    public void showLogoutDialog(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new JsonTaskFlagSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private class JsonTaskFlagSet  extends AsyncTask<Void, Void, Void> {
        String res;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Logging out...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ////////////////
            String serverLink = getResources().getString(R.string.serverLink);
            OkHttpClient client =  new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            FormBody.Builder formBuilder = new FormBody.Builder() .add("id",getMyID());
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(serverLink+getResources().getString(R.string.logout))
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(res!=null && !res.equals("")){
                if(res.equals("1")){
                    //Toast.makeText(MainActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    SharedPreferences userProfile = MainActivity.this.getSharedPreferences("ProfileDetails", MODE_PRIVATE);
                    SharedPreferences.Editor profilerEditor = userProfile.edit();
                    profilerEditor.remove("logFlag").apply();
                    stopAlarm();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 3s
                            stopService(new Intent(MainActivity.this, UnReadMessageService.class)); // So that it does not receive any notifications
                            stopService(new Intent(MainActivity.this, MyService.class));
                            pDialog.dismiss();
                            finish();
                        }
                    }, 3000);
                }
            }
        }
    }

    private void stopAlarm() {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
