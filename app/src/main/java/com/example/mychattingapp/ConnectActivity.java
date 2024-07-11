package com.example.mychattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectActivity extends AppCompatActivity {
    Button btnConnect;
    EditText textIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        btnConnect = findViewById(R.id.btnConnect);
        textIP = findViewById(R.id.editConnectIP);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                intent.putExtra("IP",textIP.getText().toString());
                startActivity(intent);
            }
        });
    }
}