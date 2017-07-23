package tpm.employee.cv;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 4/9/2017.
 */
public class Skills extends AppCompatActivity {

    FloatingActionButton fab;
    LinearLayout LLEnterText;
    int _intMyLineCount;

    private List<EditText> editTextList = new ArrayList<EditText>();
    private List<LinearLayout> linearlayoutList=new ArrayList<LinearLayout>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skillsabilities);
        getSupportActionBar().setTitle("Skills/Abilities");
        LLEnterText=(LinearLayout) findViewById(R.id.layout_main_workexp);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLEnterText.addView(linearlayout(_intMyLineCount));
                _intMyLineCount++;
            }
        });
    }

    private LinearLayout linearlayout(int _intID)
    {
        LayoutInflater inflator= LayoutInflater.from(Skills.this);
        View vieww= inflator.inflate(R.layout.skillslinearlayout, null);
        LinearLayout llll = (LinearLayout) vieww;
        llll.setId(_intID);
        EditText editText = (EditText) llll.findViewById(R.id.editTexxt);
        editText.setId(_intID);
        editTextList.add(editText);
        llll.setOrientation(LinearLayout.VERTICAL);
        linearlayoutList.add(llll);
        return llll;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cv_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            Toast.makeText(Skills.this,"Check fields before saving, Thanks",Toast.LENGTH_SHORT).show();
            for (EditText editText : editTextList) {
                String edtTexts = editText.getText().toString();
                Log.e("testingsss",edtTexts);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
