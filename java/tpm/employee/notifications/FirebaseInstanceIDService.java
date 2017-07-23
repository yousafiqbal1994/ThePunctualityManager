package tpm.employee.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Dell pc on 9/23/2016.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static String tokenn;
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        tokenn = token;
        Log.e("token" , token);
        registerToken(token);
    }
    private void registerToken(String token) {
        Log.e("response" , "Latest FCM \n + " + token);
   }
}
