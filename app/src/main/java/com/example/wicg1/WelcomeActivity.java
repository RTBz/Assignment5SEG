package com.example.wicg1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeMessage;
    String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Bundle extras = getIntent().getExtras();

        accountType = extras.getString("ACCOUNT_TYPE");
        final String accountUserName = extras.getString("ACCOUNT_USERNAME");

        welcomeMessage = findViewById(R.id.WelcomeMessage);
        welcomeMessage.setText("Welcome "+accountUserName+"! You are now connected as a "+accountType+".");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accountType.equals("Administrator")){
                    Intent intent=new Intent(WelcomeActivity.this, AdminActivity.class);
                    intent.putExtra("username",accountUserName);
                    startActivity(intent);
                }else if (accountType.equals("Employee")){
                    Intent intent=new Intent(WelcomeActivity.this, EmployeeActivity.class);
                    intent.putExtra("username",accountUserName);
                    startActivity(intent);
                }else if (accountType.equals("Patient")){
                    Intent intent=new Intent(WelcomeActivity.this, PatientActivity.class);
                    intent.putExtra("username",accountUserName);
                    startActivity(intent);
                }
            }
        } , 2000);

    }
}
