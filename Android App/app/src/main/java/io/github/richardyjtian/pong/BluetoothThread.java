//package io.github.richardyjtian.pong;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.content.Intent;
//import android.util.Log;
//
//import java.io.IOException;
//import java.util.UUID;
//
//import static android.content.ContentValues.TAG;
//
//public class BluetoothThread extends Thread {
//    private static final int REQUEST_ENABLE_BT = 23;
//    private final BluetoothServerSocket mmServerSocket;
//
//    public BluetoothThread(Activity activity) {
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            // Device doesn't support Bluetooth
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//
//        // Use a temporary object that is later assigned to mmServerSocket
//        // because mmServerSocket is final.
//        BluetoothServerSocket tmp = null;
//        try {
//            // MY_UUID is the app's UUID string, also used by the client code.
//            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, UUID.fromString("Pong"));
//        } catch (IOException e) {
//            Log.e(TAG, "Socket's listen() method failed", e);
//        }
//        mmServerSocket = tmp;
//    }
//
//    public void run() {
//        BluetoothSocket socket = null;
//        // Keep listening until exception occurs or a socket is returned.
//        while (true) {
//            try {
//                socket = mmServerSocket.accept();
//            } catch (IOException e) {
//                Log.e(TAG, "Socket's accept() method failed", e);
//                break;
//            }
//
//            if (socket != null) {
//                // A connection was accepted. Perform work associated with
//                // the connection in a separate thread.
//                manageMyConnectedSocket(socket);
//                mmServerSocket.close();
//                break;
//            }
//        }
//    }
//
//    // Closes the connect socket and causes the thread to finish.
//    public void cancel() {
//        try {
//            mmServerSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not close the connect socket", e);
//        }
//    }
//}
