package tpm.employee.service;

public class MyWiFiStateListener{

}
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO Auto-generated method stub
//        String action = intent.getAction();
//        Log.e("TEMP", action);
//        if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION )){
//            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
//
//            if (SupplicantState.isValidState(state)
//                    && state == SupplicantState.COMPLETED) {
//                Log.e("new", String.valueOf(state));
//                boolean connected = checkConnectedToDesiredWifi(context);
//            }
//        }
//
//
//    }
//    private boolean checkConnectedToDesiredWifi(Context context) {
//        boolean connected = false;
//
//        String desiredMacAddress = "2c:e4:12:46:8c:3a";
//
//        WifiManager wifiManager =
//                (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//        WifiInfo wifi = wifiManager.getConnectionInfo();
//        if (wifi != null) {
//            // get current router Mac address
//            String bssid = wifi.getBSSID();
//            connected = desiredMacAddress.equals(bssid);
//        }
//
//        return connected;
//    }
//
//}
