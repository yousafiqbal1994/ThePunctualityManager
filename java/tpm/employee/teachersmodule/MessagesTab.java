package tpm.employee.teachersmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import tpm.employee.R;
import tpm.employee.chatting.ChatDetails;
import tpm.employee.chatting.ChattingActivity;
import tpm.employee.chatting.CustomCenterCrop;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by YouCaf Iqbal on 3/18/2017.
 */
public class MessagesTab extends Fragment {

    public ArrayList<ChatDetails> ChatPersons= new ArrayList<ChatDetails>();
    public ArrayList<ChatDetails> tempChatPersons= new ArrayList<ChatDetails>();
    int counter =0;
    private BroadcastReceiver receiver = null;
    public ArrayAdapter<ChatDetails> adapter;
    FloatingActionButton fab;
    SharedPreferences prefs;
    public Context context;
    ListView list;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.teachermessages,container,false);
        if (container == null) {
            return null;
        }
        registerBroadcastReceiver();
        list = (ListView) v.findViewById(R.id.ChatsListView);
        refreshListView();
        registerClickCallback(); // Open Chat Box when clicked
        //registerForContextMenu(list); // Delete Option on long clicks
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewTeacherMessageActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }
    private void registerBroadcastReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                if(success){
                    refreshListView();
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("message"));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onPause();
    }
    private void refreshListView(){
        ChatPersons.clear();
        addAllthePeople();
        adjustPeopleInOrder();
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
            tempChatPersons.clear();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String jsonn = prefs.getString(entry.getKey(),"");
                String keyofCurrentObject = entry.getKey();
                ChatDetails obj = gson.fromJson(jsonn, ChatDetails.class);
                if(obj!=null){
                    String initials = keyofCurrentObject.charAt(0)+""+keyofCurrentObject.charAt(1);
                    if(initials.equals("mp")){
                        SharedPreferences chatter = getActivity().getSharedPreferences("MostRecentChatter",MODE_PRIVATE);
                        String chatterID = chatter.getString("chatterID","");
                        if(chatterID.equals(keyofCurrentObject)){
                            ChatDetails chatDetails  =new ChatDetails(obj.sname,obj.senderID,obj.receiverID,obj.rname);
                            ChatPersons.add(chatDetails); ///// always 0th index
                        }else{
                            counter++;
                            ChatDetails chatDetails  =new ChatDetails(obj.sname,obj.senderID,obj.receiverID,obj.rname);
                            tempChatPersons.add(chatDetails);
                        }
                    }
                }
            }
        }
    }

    public void adjustPeopleInOrder(){
        for(int i = 0;i<counter;i++){
            ChatPersons.add(tempChatPersons.get(i));
        }
        counter =0;
    }
    public void addPerson(ChatDetails person, String name, Context c) {
        Context con = c;
        prefs = PreferenceManager.getDefaultSharedPreferences(con);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(person);
        name = "mp"+name;
        prefsEditor.putString(name, json);
        prefsEditor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("message"));
    }
    public class MyListAdapter extends ArrayAdapter<ChatDetails> {

        MyListAdapter() {
            super(getActivity(), R.layout.chat_item_view, ChatPersons);
        }
        @NonNull
        @Override
        public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {

            View itemView = null;
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                itemView = vi.inflate(R.layout.chat_item_view, null);
            } else {
                itemView = convertView;
            }

            ViewHolder viewHolder = new ViewHolder();

            ///////////////
            final ChatDetails currentPerson = ChatPersons.get((position));
            ///////////////////////////////////////
            int unreadCounter=0;
            SharedPreferences messagesCounterPreference = getActivity().getSharedPreferences("MessagesCounter", MODE_PRIVATE);
            Map<String, ?> allEntries = messagesCounterPreference.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if(currentPerson.getReceiverID().equals(entry.getKey())){
                    unreadCounter =  messagesCounterPreference.getInt(entry.getKey(),-1);
                }
            }

            viewHolder.unreadMsgsCounter =(TextView) itemView.findViewById(R.id.counter);
            if(unreadCounter>0){
                if(unreadCounter>=99){
                    viewHolder.unreadMsgsCounter.setText(99+"");
                }else {
                    viewHolder.unreadMsgsCounter.setText(unreadCounter+"");
                }
            }else{
                viewHolder.unreadMsgsCounter.setBackground(null);
            }
            ///////////////////////////////////////
            viewHolder.lastMsg = (TextView) itemView.findViewById(R.id.lastMessage);
            SharedPreferences lastMessagePreference = getActivity().getSharedPreferences("LastMessage", MODE_PRIVATE);
            Map<String, ?> allMsgEntries = lastMessagePreference.getAll();

            for (Map.Entry<String, ?> entry : allMsgEntries.entrySet()) {
                if(currentPerson.getReceiverID().equals(entry.getKey())){
                    viewHolder.lastMsg.setText(entry.getValue().toString().trim());
                }
            }
            ///////////////////////////////////////////////
            viewHolder.Name = (TextView) itemView.findViewById(R.id.Name);
            viewHolder.Name.setText(currentPerson.getRname());
//            viewHolder.imageView.setText(getFirstLettersOfName(currentPerson.getRname()));
            String myImage = getActivity().getSharedPreferences("ProfileDetails", MODE_PRIVATE).getString("id","");
            String imageToBeLoaded = "";
            if(myImage.equals(currentPerson.getReceiverID())){
                imageToBeLoaded =  currentPerson.getSenderID()+".jpg";
            }else{
                imageToBeLoaded =  currentPerson.getReceiverID()+".jpg";
            }
            viewHolder.DonorImage = (CircleImageView) itemView.findViewById(R.id.chatImage);
            String url = getResources().getString(R.string.ImagesURL);

            Glide.with(MessagesTab.this).load(url+imageToBeLoaded).asBitmap().placeholder(R.drawable.dp).override(60, 60)
                    .transform(new CenterCrop(getActivity()), new CustomCenterCrop(getActivity())).into(viewHolder.DonorImage);
            //////////////

            return itemView;
        }
    }
    public class ViewHolder {
        TextView Name;
        CircleImageView DonorImage;
        TextView unreadMsgsCounter;
        TextView lastMsg;
    }

    private void registerClickCallback() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(),ChattingActivity.class);
                final ChatDetails currentPerson = ChatPersons.get(position);
                intent.putExtra("RECEIVER_NAME", currentPerson.getRname());
                intent.putExtra("Sender_NAME", currentPerson.getSname());
                intent.putExtra("ReceiverID", currentPerson.getReceiverID());
                intent.putExtra("SenderID",currentPerson.getSenderID());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                Toast.makeText(getActivity(),"Delete Clicked",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
