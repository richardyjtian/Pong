package io.github.richardyjtian.pong;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    // pongView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    PongView pongView;

    int SCALE = 6;

    private static final int REQUEST_ENABLE_BT = 23;

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


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK) {
                // Bluetooth is enabled
            } else if(resultCode == RESULT_CANCELED){
                // Bluetooth enabling is cancelled

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
