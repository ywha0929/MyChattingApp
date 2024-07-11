package com.example.mychattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class GuestActivity extends AppCompatActivity {
    TextView textJoinIP;
    TextView textMessageLog;
    EditText editGuestName;
    EditText editInput;
    SendThread sendThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        textJoinIP = findViewById(R.id.joinIP);
        textMessageLog = findViewById(R.id.textGuestMessage);
        editGuestName = findViewById(R.id.editGuestName);
        editInput = findViewById(R.id.editGuestInput);

        Intent intent = getIntent();
        try {
            sendThread = new SendThread(intent.getStringExtra("IP"));
            sendThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                    sendThread.message.add(log);
                }
                return false;
            }
        });
    }
    class SendThread extends Thread {

        Queue<String> message = new LinkedList();
        String ip;
        Socket socket;
        SendThread(String ip) throws IOException {
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip,12345);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ReceiveThread receiveThread = new ReceiveThread(socket);
            receiveThread.start();
            while(true) {
                if(message.isEmpty()) {
                    continue;
                }
                else {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    try {
                        dataOutputStream.writeUTF(message.poll());
                        message.remove();
                        socket.getOutputStream().write(  byteArrayOutputStream.toByteArray() );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        class ReceiveThread extends Thread {
            Socket socket;
            ReceiveThread(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                while(true) {
                    try {
                        if(socket.getInputStream().available() <= 0) {
                            continue;
                        }
                        else {
                            byte[] buffer = new byte[1500];
                            socket.getInputStream().read(buffer);
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                            String message = dataInputStream.readUTF();
                            textMessageLog.post(new Runnable() {
                                @Override
                                public void run() {
                                    String origin = textMessageLog.getText().toString();
                                    origin = origin + "\n"+message;
                                    textMessageLog.setText(origin);
                                }
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}