package com.gekocorp.gecko;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText eTName = findViewById(R.id.eTName);
        Button btnProceed = findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,LogPage.class);
                AppGlobalData.driverName = eTName.getText().toString();
                if(!AppGlobalData.driverName.equals("")){
                    startActivity(i);
                    finish();
                }

            }
        });
    }
}
