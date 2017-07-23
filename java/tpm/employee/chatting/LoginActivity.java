package tpm.employee.chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.vstechlab.easyfonts.EasyFonts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.R;

public class LoginActivity extends AppCompatActivity {
    public String userName,password; // variables to be send to server
    public ProgressDialog pDialog;
    public String iDofLoggedINUser,nameofLoggedINUser;
    EditText _usernameText;
    EditText _passwordText;
    FancyButton loginButton;
    ShimmerTextView LoginText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userProfile = LoginActivity.this.getSharedPreferences("ProfileDetails", MODE_PRIVATE);
        String UserRegistered = userProfile.getString("registered", "false");
        if (!UserRegistered.equals("false")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_mylogin);
        _usernameText =  (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);

        loginButton = (FancyButton) findViewById(R.id.btn_login);
        loginButton.setFontIconSize(20);
        loginButton.setTextSize(20);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        LoginText = (ShimmerTextView) findViewById(R.id.LoginText);
        LoginText.setTypeface(EasyFonts.captureIt(this));
        Shimmer signUpTextShrimmer = new Shimmer();
        signUpTextShrimmer.start(LoginText);
        signUpTextShrimmer.setDuration(3000);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isNetworkAvailable() && _passwordText.getText().length()!=0 && _usernameText.getText().length()!=0){
                    new AsyncLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else {
                    if(!isNetworkAvailable()){
                        Toast.makeText(getBaseContext(),"No internet", Toast.LENGTH_SHORT).show();}
                    else {
                        validate();
                    }
                }
            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncLogin extends AsyncTask<Void,Void,Void> {
        JSONObject json =null;
        @Override
        protected Void doInBackground(Void... voids) {
            login();
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!validate()) {
                onLoginFailed();
                return;
            }
            else {
                loginButton.setEnabled(false);
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Authenticating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(json!=null){
                try {
                    JSONObject jObject = json.getJSONObject("1");
                    iDofLoggedINUser=jObject.getString("id");
                    nameofLoggedINUser=jObject.getString("name");
                    onLoginSuccess();
                } catch (JSONException e) {
                    onLoginFailed();
                    e.printStackTrace();
                }
            }
            else{
                onLoginFailed();
            }
        }
        public void login() {
            if(_usernameText.getText().length()==0 || _passwordText.getText().length()==0){
                return;
            }
            else {
                String serverLink = getResources().getString(R.string.serverLink);
                userName = _usernameText.getText().toString().trim().toLowerCase();
                password = _passwordText.getText().toString();
                OkHttpClient client =  new OkHttpClient.Builder()
                        .connectTimeout(50, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .build();
                FormBody.Builder formBuilder = new FormBody.Builder() .add("username",userName)
                        .add("password",password);
                Log.e("sexy",userName+","+password);
                RequestBody formBody = formBuilder.build();
                Request request = new Request.Builder()
                        .url(serverLink+getResources().getString(R.string.AuthenticateUser))
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
            }
        }
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        addProfile(); // Store profile details in shared preference
        pDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addProfile() {
        SharedPreferences userProfile = LoginActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        SharedPreferences.Editor profilerEditor = userProfile.edit();
        profilerEditor.putString("registered","true");
        profilerEditor.putString("id",iDofLoggedINUser);
        profilerEditor.putString("image",iDofLoggedINUser);
        profilerEditor.putString("name",nameofLoggedINUser);
        profilerEditor.apply();
    }


    public void onLoginFailed() {
        pDialog.dismiss();
        Toast.makeText(getBaseContext(),"Login failed", Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("Enter valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }
        if (password.isEmpty()) {
            _passwordText.setError("Enter a valid password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
