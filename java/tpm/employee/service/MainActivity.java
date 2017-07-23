package tpm.employee.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import tpm.employee.R;

public class MainActivity extends AppCompatActivity {

    TextView textResult;
    ProgressBar progressBar;

    private MyBroadcastReceiver myBroadcastReceiver;
    private MyBroadcastReceiver_Update myBroadcastReceiver_Update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = (TextView)findViewById(R.id.result);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        //prepare MyParcelable passing to intentMyIntentService
        String msgToIntentService = "Android-er";

        //Start MyIntentService
        Intent intentMyIntentService = new Intent(this, MyService.class);
        intentMyIntentService.putExtra(MyService.EXTRA_KEY_IN, msgToIntentService);
        startService(intentMyIntentService);

        myBroadcastReceiver = new MyBroadcastReceiver();
        myBroadcastReceiver_Update = new MyBroadcastReceiver_Update();

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(MyService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        IntentFilter intentFilter_update = new IntentFilter(MyService.ACTION_MyUpdate);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver_Update, intentFilter_update);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //un-register Broa
        // dcastReceiver
//        unregisterReceiver(myBroadcastReceiver);
//        unregisterReceiver(myBroadcastReceiver_Update);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(MyService.EXTRA_KEY_OUT);
            textResult.setText(result);
        }
    }

    public class MyBroadcastReceiver_Update extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(MyService.EXTRA_KEY_UPDATE, 0);
            progressBar.setProgress(update);


        }
    }

}
