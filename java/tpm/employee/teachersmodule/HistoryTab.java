package tpm.employee.teachersmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 3/18/2017.
 */
public class HistoryTab extends Fragment {
    public ArrayList<NotificationDetails> notificationsList= new ArrayList<NotificationDetails>();
    public ArrayAdapter<NotificationDetails> adapter;
    SharedPreferences prefs;
    public Context context;
    ListView list;
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.historytablayout,container,false);
        if (container == null) {
            return null;
        }
        list = (ListView) v.findViewById(R.id.NotificationsListView);
        refreshListView();
        registerClickCallback(); // Open Chat Box when clicked
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }

    private void refreshListView(){
        notificationsList.clear();
        addAllthePeople();
//        adjustPeopleInOrder();
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void addAllthePeople() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
//        context = con;
        Map<String, ?> allEntries = prefs.getAll();
        if(allEntries.size()>0){
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String jsonn = prefs.getString(entry.getKey(),"");
                String keyofCurrentObject = entry.getKey();
                NotificationDetails obj = gson.fromJson(jsonn, NotificationDetails.class);
                if(obj!=null){
                    String initial = keyofCurrentObject.charAt(0)+""+keyofCurrentObject.charAt(1);
                    if(initial.equals("nf")){
                        NotificationDetails notifDetails  =new NotificationDetails(obj.nID,obj.nTitle,obj.nMessage,obj.nFrom,obj.nTo,obj.sendingTime);
                        notificationsList.add(notifDetails);
                    }
                }
            }
        }

    }

    public class MyListAdapter extends ArrayAdapter<NotificationDetails> {

        MyListAdapter() {
            super(getActivity(), R.layout.history_item_view, notificationsList);
        }
        @NonNull
        @Override
        public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {

            View itemView = null;
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                itemView = vi.inflate(R.layout.history_item_view, null);
            } else {
                itemView = convertView;
            }

            ViewHolder viewHolder = new ViewHolder();

            ///////////////
            final NotificationDetails currentPerson = notificationsList.get((position));

            viewHolder.Msg = (TextView) itemView.findViewById(R.id.Message);
            viewHolder.Msg.setText(currentPerson.getnMessage());
            viewHolder.title = (TextView) itemView.findViewById(R.id.title);
            viewHolder.title.setText(currentPerson.getnTitle());
            return itemView;
        }
    }
    public class ViewHolder {
        TextView title;
        TextView Msg;
    }

    private void registerClickCallback() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                final NotificationDetails currentPerson = notificationsList.get(position);
//                Toast.makeText(getActivity(),"Fetch Comments from database",Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(),currentPerson.getnID(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),DetailedNotificationActivtiy.class);
                intent.putExtra("id",currentPerson.getnID());
                intent.putExtra("title",currentPerson.getnTitle());
                intent.putExtra("message",currentPerson.getnMessage());
                startActivity(intent);

            }
        });
    }

}
