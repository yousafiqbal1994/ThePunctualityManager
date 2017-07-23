package tpm.employee.cv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import tpm.employee.R;

/**
 * Created by YouCaf Iqbal on 4/7/2017.
 */

public class MainCVActivity extends AppCompatActivity {
    Button contactBtn,educationBtn,experienceBtn,achievementsBtn,academicProjectsBtn,interestsBtn,statmntBtn,skillsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cv_layout);
        statmntBtn = (Button) findViewById(R.id.statmentbtn);
        skillsBtn = (Button) findViewById(R.id.skillsbtn);
        contactBtn   = (Button) findViewById(R.id.contactbtn);
        educationBtn = (Button) findViewById(R.id.educationbtn);
        experienceBtn = (Button) findViewById(R.id.experiencebtn);
        achievementsBtn = (Button) findViewById(R.id.achievmentsbtn);
        academicProjectsBtn = (Button) findViewById(R.id.academicprojectsbtn);
        interestsBtn = (Button) findViewById(R.id.interestsbtn);

        statmntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,PersonalStatement.class);
                startActivity(intent);
            }
        });
        skillsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,Skills.class);
                startActivity(intent);
            }
        });
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,Contact.class);
                startActivity(intent);
            }
        });
        educationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,Education.class);
                startActivity(intent);

            }
        });
        experienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,WorkExperience.class);
                startActivity(intent);
            }
        });
        achievementsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,Achievements.class);
                startActivity(intent);
            }
        });
        academicProjectsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,AcademicProjects.class);
                startActivity(intent);
            }
        });
        interestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCVActivity.this,Interests.class);
                startActivity(intent);
            }
        });
    }
}
