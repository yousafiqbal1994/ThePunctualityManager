package tpm.employee.cv;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 4/8/2017.
 */
public class Achievements extends AppCompatActivity {

    FloatingActionButton fab;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievements);
        getSupportActionBar().setTitle("Achievements");

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
            Toast.makeText(Achievements.this,"Check fields before saving, Thanks",Toast.LENGTH_SHORT).show();
            fab.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
