package tpm.employee.chatting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 3/22/2017.
 */

public class StudentNotificationDetails extends AppCompatActivity {
    String title,msg,myReply;
    TextView T,M,Reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stunotifydetails);
        Bundle b = getIntent().getExtras();
        title = b.getString("title");
        msg = b.getString("message");
        myReply = b.getString("reply");
        T=(TextView) findViewById(R.id.title);
        M=(TextView) findViewById(R.id.msg);
        Reply = (TextView) findViewById(R.id.reply);
        T.setText(title);
        M.setText(msg);
        Reply.setText(myReply);
    }
}
