package tpm.employee.teachersmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 3/23/2017.
 */

public class MarkAttendance extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    CheckBox chkIos;
    TextView id,name;
    Button dateButton,attendancBtn;
    private TextView checktime_textview,checkouttime_textview;
    Button checkinButton,checkoutButton;
    String check=null;
    private TextView dateTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markattendance);
        checktime_textview = (TextView) findViewById(R.id.checktime_textview);
        checkouttime_textview = (TextView) findViewById(R.id.checkouttime_textview);
        checkinButton = (Button) findViewById(R.id.time_button);
        checkoutButton = (Button) findViewById(R.id.checkoutButton);
        dateTextView = (TextView) findViewById(R.id.date_textview);
        dateButton = (Button) findViewById(R.id.date_button);
        attendancBtn= (Button) findViewById(R.id.attendanc);
        id = (TextView) findViewById(R.id.id);
        name = (TextView) findViewById(R.id.name);
        chkIos = (CheckBox) findViewById(R.id.chkAndroid);
        Intent i = getIntent();

        id.setText(i.getStringExtra("RECEIVER_NAME"));
        name.setText(i.getStringExtra("ReceiverID"));
        chkIos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                }
            }
        });

        attendancBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MarkAttendance.this,"Mark Attendance",Toast.LENGTH_SHORT).show();
            }
        });
        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check="in";
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        MarkAttendance.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );

                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check="out";
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        MarkAttendance.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );

                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MarkAttendance.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "Date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dateTextView.setText(date);
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        if(tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if(check.equals("in")){
            String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
            String minuteString = minute < 10 ? "0"+minute : ""+minute;
            String secondString = second < 10 ? "0"+second : ""+second;
            String time = "Check IN time: "+hourString+"h"+minuteString+"m"+secondString+"s";
            checktime_textview.setText(time);
        }
        if(check.equals("out")){
            String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
            String minuteString = minute < 10 ? "0"+minute : ""+minute;
            String secondString = second < 10 ? "0"+second : ""+second;
            String time = "Check OUT time: "+hourString+"h"+minuteString+"m"+secondString+"s";
            checkouttime_textview.setText(time);
        }

    }
}
