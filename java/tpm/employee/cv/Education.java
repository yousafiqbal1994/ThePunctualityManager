package tpm.employee.cv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 4/8/2017.
 */
public class Education extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Button uni_from,uni_to,colg_from,colg_to,scl_from,scl_to;
    String buttonType=null;
    TextView uni_start_date,uni_end_date,colg_start_date,colg_end_date,scl_start_date,scl_end_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.education);
        getSupportActionBar().setTitle("Education");
        uni_start_date = (TextView) findViewById(R.id.uni_start_date);
        uni_end_date = (TextView) findViewById(R.id.uni_end_date);
        colg_start_date = (TextView) findViewById(R.id.colg_start_date);
        colg_end_date = (TextView) findViewById(R.id.colg_end_date);
        scl_start_date = (TextView) findViewById(R.id.scl_start_date);
        scl_end_date = (TextView) findViewById(R.id.scl_end_date);

        uni_from = (Button) findViewById(R.id.uni_from);
        uni_to = (Button) findViewById(R.id.uni_to);
        colg_from = (Button) findViewById(R.id.colg_from);
        colg_to = (Button) findViewById(R.id.colg_to);
        scl_from = (Button) findViewById(R.id.scl_from);
        scl_to = (Button) findViewById(R.id.scl_to);

        uni_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Education.this,
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
                        Education.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="uni_to";
            }
        });
        colg_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Education.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="colg_from";
            }
        });
        colg_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Education.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="colg_to";
            }
        });
        scl_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Education.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="scl_from";
            }
        });
        scl_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Education.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                buttonType="scl_to";
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cv_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            Toast.makeText(Education.this,"Check fields before saving, Thanks",Toast.LENGTH_SHORT).show();
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
        if(buttonType.equals("colg_from")){
            String date = String.valueOf(year);
            colg_start_date.setText(date);
        }
        if(buttonType.equals("colg_to")){
            String date = String.valueOf(year);
            colg_end_date.setText(date);
        }
        if(buttonType.equals("scl_from")){
            String date = String.valueOf(year);
            scl_start_date.setText(date);
        }
        if(buttonType.equals("scl_to")){
            String date = String.valueOf(year);
            scl_end_date.setText(date);
        }
    }
}
