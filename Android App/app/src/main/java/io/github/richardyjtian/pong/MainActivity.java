package io.github.richardyjtian.pong;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // pongView is the view of the game, and handles game logic and screen touches
    PongView pongView;
    int SCALE = 6;

    Toast currentToast;

    BluetoothAdapter bluetoothAdapter;
    Intent enableBtIntent;
    private static final int REQUEST_ENABLE_BT = 23;

    static final int STATE_CONNECTING = 1;
    static final int STATE_CONNECTED = 2;
    static final int STATE_CONNECTION_FAILED = 3;
    static final int STATE_MESSAGE_RECEIVED = 4;

    private static final String TM4_MAC_ADDRESS = "00:06:66:86:5F:D0";
    private static final UUID SERIAL_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID

    SendReceive sendReceive;
    String currRcvStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the resolution into a Point object
        Point size = new Point();
        // LCD Touchscreen of TM47 is x = 320 by y = 240
        // LGG4 is x = 2392 by y = 1440
        // size.x = 1920, size.y = 1440
        size.x = 320*SCALE;
        size.y = 240*SCALE;

        Point phone_size = new Point();
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(phone_size);

        // Initialize pongView and set it as the view
        pongView = new PongView(this, size.x, size.y, phone_size.x, phone_size.y);
        setContentView(pongView);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            // Bluetooth is enabled
            Toast.makeText(this, "Bluetooth is enabled and connecting", Toast.LENGTH_SHORT).show();
            // Create a bluetooth device from the TM4's MAC Address
            BluetoothDevice TM4BluetoothDevice = bluetoothAdapter.getRemoteDevice(TM4_MAC_ADDRESS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TM4BluetoothDevice.createBond();
            }
            // Start a client thread using the TM4 bluetooth device
            ClientClass clientClass = new ClientClass(TM4BluetoothDevice);
            clientClass.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK) {
                // Bluetooth is enabled
                Toast.makeText(this, "Bluetooth is enabled and connecting", Toast.LENGTH_SHORT).show();
                // Create a bluetooth device from the TM4's MAC Address
                BluetoothDevice TM4BluetoothDevice = bluetoothAdapter.getRemoteDevice(TM4_MAC_ADDRESS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TM4BluetoothDevice.createBond();
                }
                // Start a client thread using the TM4 bluetooth device
                ClientClass clientClass = new ClientClass(TM4BluetoothDevice);
                clientClass.start();
            } else if(resultCode == RESULT_CANCELED){
                // Bluetooth enabling is cancelled
                Toast.makeText(this, "Bluetooth is necessary to play", Toast.LENGTH_SHORT).show();
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void overrideToast(String message){
        if(currentToast != null){
            currentToast.cancel();
        }
        currentToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        currentToast.show();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case STATE_CONNECTING:
                    overrideToast("Connecting");
                    break;
                case STATE_CONNECTED:
                    overrideToast("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    overrideToast("Connection failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    //Complicated logic to deal with 1 byte send/rcv of TM4
//                    byte[] buffer = (byte[]) msg.obj;
//                    String newRcvStr = new String(buffer, 0, msg.arg1);
//                    if(newRcvStr.contains("!")){
//                        String[] newRcvStrSplit = newRcvStr.split("!");
//                        if(newRcvStrSplit.length == 0){
//                            Toast.makeText(MainActivity.this, currRcvStr, Toast.LENGTH_SHORT).show();
//                            currRcvStr = "";
//                            break;
//                        }
//                        Toast.makeText(MainActivity.this, currRcvStr + newRcvStrSplit[0], Toast.LENGTH_SHORT).show();
//
//                        if(newRcvStrSplit.length == 1){
//                            currRcvStr = "";
//                            break;
//                        }
//                        int i;
//                        for(i = 1; i < newRcvStrSplit.length - 1; i++){
//                            Toast.makeText(MainActivity.this, newRcvStrSplit[i], Toast.LENGTH_SHORT).show();
//                        }
//                        if(newRcvStr.endsWith("!")){
//                            Toast.makeText(MainActivity.this, newRcvStrSplit[i], Toast.LENGTH_SHORT).show();
//                            currRcvStr = "";
//                        }
//                        else{
//                            currRcvStr += newRcvStrSplit[i];
//                        }
//                    }
//                    else{
//                        currRcvStr += newRcvStr;
//                    }
                    int dir = (int) msg.obj;
                    if(dir > 0)
                        pongView.mTopBat.setMovementState(pongView.mTopBat.RIGHT);
                    else
                        pongView.mTopBat.setMovementState(pongView.mTopBat.LEFT);
                    break;
            }
            return true;
        }
    });

    private class ClientClass extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1){
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
                pongView.mSendReceive = sendReceive;

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    public class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run(){
            while(true){
                try {
                    int dir = inputStream.read();
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, -1, -1, dir).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the pongView resume method to execute
        pongView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the pongView pause method to execute
        pongView.pause();
    }
}
