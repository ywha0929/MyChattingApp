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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import kotlinx.coroutines.channels.Send;

public class HostActivity extends AppCompatActivity {
    EditText editNickName;
    EditText editMessage;
    TextView textMessageLog;
    TextView textIP;
    SendThread sendThread;

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

        try {
            sendThread = new SendThread();
            sendThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        editMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String nick = editNickName.getText().toString();
                    String message = editMessage.getText().toString();
                    String log = nick + " : " + message;
                    String beforeLog = textMessageLog.getText().toString();
                    beforeLog = beforeLog + "\n " + log;
                    textMessageLog.setText(beforeLog);
                    editMessage.setText("");
                    sendThread.message.add(log);
                }
                return false;
            }
        });

    }

    class SendThread extends Thread {
        ServerSocket serverSocket = new ServerSocket(12345);
        Queue<String> message = new LinkedList();
        Socket socket;

        SendThread() throws IOException {

        }

        @Override
        public void run() {
            while (socket == null) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ReceiveThread receiveThread = new ReceiveThread(socket);
            receiveThread.start();
            while (true) {
                if (message.isEmpty()) {
                    continue;
                } else {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    try {
                        dataOutputStream.writeUTF(message.poll());
                        message.remove();
                        socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
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
                while (true) {
                    try {
                        if (socket.getInputStream().available() <= 0) {
                            continue;
                        } else {
                            byte[] buffer = new byte[1500];
                            socket.getInputStream().read(buffer);
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                            String message = dataInputStream.readUTF();
                            textMessageLog.post(new Runnable() {
                                @Override
                                public void run() {
                                    String origin = textMessageLog.getText().toString();
                                    origin = origin + "\n" + message;
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