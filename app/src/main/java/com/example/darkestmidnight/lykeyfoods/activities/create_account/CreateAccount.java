package com.example.darkestmidnight.lykeyfoods.activities.create_account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.darkestmidnight.lykeyfoods.R;

public class CreateAccount extends AppCompatActivity {

    Button CancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // hide action bar
        if(this.getSupportActionBar()!=null) {
            this.getSupportActionBar().hide();
        }

        CancelButton = (Button) findViewById(R.id.cancelCreateAccBtn);

        // when cancel button is pressed
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancel will be treated as go back from base android
                CreateAccount.super.onBackPressed();
            }
        });
    }
}
