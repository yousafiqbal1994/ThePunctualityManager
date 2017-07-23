package tpm.employee.cv;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 4/8/2017.
 */
public class WorkExperience extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    FloatingActionButton fab;
    String buttonType=null;
    Button uni_from,uni_to;
    TextView uni_start_date,uni_end_date;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workexperience);
        getSupportActionBar().setTitle("Work Experience");
        uni_start_date = (TextView) findViewById(R.id.uni_start_date);
        uni_end_date = (TextView) findViewById(R.id.uni_end_date);

        uni_from = (Button) findViewById(R.id.uni_from);
        uni_to = (Button) findViewById(R.id.uni_to);

        uni_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        WorkExperience.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="uni_from";
            }
        });
        uni_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        WorkExperience.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="uni_to";
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.layout_main_workexp);
        linearLayout.setVisibility(View.GONE);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cv_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            Toast.makeText(WorkExperience.this,"Check fields before saving, Thanks",Toast.LENGTH_SHORT).show();
            fab.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(buttonType.equals("uni_from")){
            String date = String.valueOf(year);
            uni_start_date.setText(date);
        }
        if(buttonType.equals("uni_to")){
            String date = String.valueOf(year);
            uni_end_date.setText(date);
        }
    }
}
