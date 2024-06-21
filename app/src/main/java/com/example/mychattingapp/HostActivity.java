package com.example.mychattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostActivity extends AppCompatActivity {
    EditText editNickName;
    EditText editMessage;
    TextView textMessageLog;
    TextView textIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        editNickName = findViewById(R.id.editHostName);
        editMessage = findViewById(R.id.editHostInput);
        textMessageLog = findViewById(R.id.textHostMessage);
        textIP = findViewById(R.id.hostIP);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        textIP.setText(ipAddress);

        editMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String nick = editNickName.getText().toString();
                    String message = editMessage.getText().toString();
                    String log = nick + " : " + message;
                    String beforeLog = textMessageLog.getText().toString();
                    beforeLog  = beforeLog + "\n " + log;
                    textMessageLog.setText(beforeLog);
                    editMessage.setText("");
                }
                return false;
            }
        });

    }
}