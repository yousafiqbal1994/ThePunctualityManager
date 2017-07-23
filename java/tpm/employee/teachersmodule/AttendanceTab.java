package tpm.employee.teachersmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tpm.employee.R;
import tpm.employee.chatting.CustomCenterCrop;
import tpm.employee.chatting.UserDetails;

/**
 * Created by YouCaf Iqbal on 3/18/2017.
 */
public class AttendanceTab extends AppCompatActivity {
    FloatingSearchView mSearchView;
    private String mLastQuery = "";
    public ArrayList<UserDetails> ChatPersons= new ArrayList<UserDetails>();
    public ArrayAdapter<UserDetails> adapter;
    public String RECEIVER_NAME,Sender_NAME,ReceiverID,SenderID;
    ListView list;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendancetablayout_teacher);
        mSearchView =  (FloatingSearchView) findViewById(R.id.floating_search_view);
        adapter = new MyListAdapter();
        list = (ListView) findViewById(R.id.attendacneListView);
        registerClickCallback();
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (newQuery.equals("")) {
//                    Toast.makeText(NewMessageActivity.this,"Search Clear",Toast.LENGTH_SHORT).show();
                } else {
                    mLastQuery = newQuery;
                    mSearchView.showProgress();
                    new chatSearchListAsyn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
            }
            @Override
            public void onFocusCleared() {
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
            }
        });
    }


    private void registerClickCallback() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                final UserDetails currentPerson = ChatPersons.get(position);
                RECEIVER_NAME= currentPerson.getUserName();
                Sender_NAME=getMyName();
                ReceiverID=currentPerson.getUserID();
                SenderID=getMyID();

                Intent intent = new Intent(AttendanceTab.this, MarkAttendance.class);
                intent.putExtra("RECEIVER_NAME", RECEIVER_NAME);
                intent.putExtra("Sender_NAME", Sender_NAME);
                intent.putExtra("ReceiverID", ReceiverID);
                intent.putExtra("SenderID",SenderID);
                startActivity(intent);
//                Toast.makeText(getActivity(),"Sender ID: "+SenderID+" Receiver ID: "+ReceiverID+" Sender Name: "+ Sender_NAME+" Receiver Name: "+RECEIVER_NAME,Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void populateListView() {
        list.setAdapter(adapter);
    }
    public class chatSearchListAsyn extends AsyncTask<Void,Void,Void> {
        JSONObject json =null;
        @Override
        protected Void doInBackground(Void... voids) {
            String serverLink = getResources().getString(R.string.serverLink);
            OkHttpClient client =  new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("searchString",mLastQuery);
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(serverLink+getResources().getString(R.string.getNameandID))
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
            ChatPersons.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(json!=null){
                showItems(json);
                populateListView();
            }else{
                populateListView();
            }
            mSearchView.hideProgress();
        }

    }

    private void showItems(JSONObject json) {
        ChatPersons.clear();
        String myID = getMyID();
        try {
            JSONObject jObject = null;
            for (int i = 1; i < json.length() + 1; i++) {
                jObject = json.getJSONObject(i+"");
                String userID = jObject.getString("userID");
                String userName = jObject.getString("userName");
                if (!userID.equals(myID)) {
                    UserDetails userDetails  =new UserDetails(userID,userName);
                    ChatPersons.add(userDetails);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("exception a",e.toString());
        }

    }

    public class MyListAdapter extends ArrayAdapter<UserDetails> {

        public MyListAdapter() {
            super(AttendanceTab.this, R.layout.search_item_view,ChatPersons);
        }
        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {

            View itemView = null;
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                itemView = vi.inflate(R.layout.search_item_view, null);
            } else {
                itemView = convertView;
            }
            ViewHolder viewHolder = new ViewHolder();
            // Find the Donor to work with.
            final UserDetails currentPerson = ChatPersons.get(position);
            viewHolder.Name = (TextView) itemView.findViewById(R.id.Name);
            viewHolder.Name.setText(currentPerson.getUserName());
//            viewHolder.imageView = (CircleImageView) itemView.findViewById(R.id.chatImage);
            ////////////////
            viewHolder.imageView = (CircleImageView) itemView.findViewById(R.id.chatImage);
            String url = getResources().getString(R.string.ImagesURL);
            Glide.with(AttendanceTab.this).load(url+currentPerson.getUserID()+".jpg").asBitmap().placeholder(R.drawable.dp).override(60, 60)
                    .transform(new CenterCrop(AttendanceTab.this), new CustomCenterCrop(AttendanceTab.this)).into(viewHolder.imageView);
            ////////////////
            return itemView;

        }
        public class ViewHolder {

            TextView Name;
            CircleImageView imageView;
        }
        private String getFirstLettersOfName(String name) {
            String fullName = name.trim();
            String[] array = fullName.split(" ");
            if(array.length==1){
                String value = array[0].charAt(0)+""+array[0].charAt(array[0].length()-1)+"";
                return  value;
            }else{
                String valuee = "";
                for (String anArray : array) {
                    valuee = valuee + anArray.charAt(0);
                }
                return  valuee;
            }
        }
    }

    private String getMyID() {
        SharedPreferences profile = AttendanceTab.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("id","0");

    }
    private String getMyName() {
        SharedPreferences profile = AttendanceTab.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        return profile.getString("name","0");
    }
}
