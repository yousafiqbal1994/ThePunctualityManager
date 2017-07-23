package tpm.employee.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 3/12/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    public String encodedPhotoString;
    FormBody.Builder formBuilder =null;
    public ProgressDialog pDialog;
    String sendingTo;
    CircleImageView dp;
    public Bitmap myDP=null;
    AlertDialog.Builder alert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        alert = new AlertDialog.Builder(this);
        dp = (CircleImageView)findViewById(R.id.dp);
        dp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), CroppingActivity.class);
                startActivity(intent);
                ProfileActivity.this.finish();
            }
        });

        myDP = CroppingActivity.finalImage;
        CheckImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPreviousImage();
    }

    private void CheckImage() {
        if(myDP!=null){
            setPreviousImage();
            encodePhoto();
            formBuilder = new FormBody.Builder().add("imageName", getMyImage());
            formBuilder.add("imageString",encodedPhotoString);
            Log.e("picmodule","imageName" +getMyImage());
            Log.e("picmodule","imageString" +encodedPhotoString);
            Log.e("picmodule",getMyImage()+ " and "+ encodedPhotoString);
            new UpdateAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            setPreviousImage();
        }
    }


    private void setPreviousImage() {
        SharedPreferences userDetails = ProfileActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        String imageString = userDetails.getString("picture","");
        if (imageString.length() >= 80) {
            // decode string back to image and set it
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            dp.setImageBitmap(decodedByte);
        }

    }

    public void encodePhoto() {
        myDP=  getResizedBitmap(myDP,400,400);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        myDP.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        encodedPhotoString = Base64.encodeToString(byte_arr, 0);
    }
    public void savePhototoSharedPrefrence() {
        SharedPreferences userProfile = ProfileActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        SharedPreferences.Editor profileDP = userProfile.edit();
        profileDP.putString("picture",encodedPhotoString);
        profileDP.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myDP =  null;
        CroppingActivity.finalImage =  null;
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private class UpdateAsync extends AsyncTask<Void, Void, Void> {

        JSONObject json = null;
        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.UpdateImage))
                    .post(formBody)
                    .build();


            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(50, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .build();
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
            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage(ProfileActivity.this.getResources().getString(R.string.updateskana)+"...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            sendingTo = getResources().getString(R.string.UpdateImage);
            Log.e("picmodule", "Sending to  = "+sendingTo);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            Log.e("picmodule", "Response = " + json);
            if (json != null) {
                // photo is updated
                savePhototoSharedPrefrence();
                dp.setImageBitmap(myDP);
                myDP =null;
                CroppingActivity.finalImage =  null;
                Toast.makeText(getApplicationContext(),"Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                myDP =null;
                Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
    private String getMyImage() {
        SharedPreferences profile = ProfileActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");

    }
}
