package com.example.mychattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GuestActivity extends AppCompatActivity {
    TextView textJoinIP;
    TextView textMessageLog;
    EditText editGuestName;
    EditText editInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        textJoinIP = findViewById(R.id.joinIP);
        textMessageLog = findViewById(R.id.textGuestMessage);
        editGuestName = findViewById(R.id.editGuestName);
        editInput = findViewById(R.id.editGuestInput);
        editInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String nick = editGuestName.getText().toString();
                    String message = editInput.getText().toString();
                    String log = nick + " : " + message;
                    String beforeLog = textMessageLog.getText().toString();
                    beforeLog  = beforeLog + "\n " + log;
                    textMessageLog.setText(beforeLog);
                    editInput.setText("");
                }
                return false;
            }
        });
    }
}