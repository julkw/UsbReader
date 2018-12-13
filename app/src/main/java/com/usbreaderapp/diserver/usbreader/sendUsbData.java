package com.usbreaderapp.diserver.usbreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.usbreaderapp.diserver.usbreader.ServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class sendUsbData extends AppCompatActivity {

    enum Button {
        UP, DOWN, LEFT, RIGHT, X, A, Y, B, R1, RTRIGGER, LTRIGGER, L1, START, SELECT
    }

    int numberOfButtons = 14;
    int[] buttonsPressed;

    String userID = "userID";
    String appID = "appID";
    String gameID = "gameID";

    // This needs to be in accordance with the server
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000SSS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_usb_data);
        buttonsPressed = new int[numberOfButtons];
        for (int i = 0; i < numberOfButtons; ++i) {
            buttonsPressed[i] = 0;
        }
        if(getGameControllerIds().isEmpty()) {
            System.out.println("No controller connected");
        }
        // TODO: check compatibility of input device (has correct keys)
    }

    protected String buttonsString() {
        String buttons = "";
        for (int button : buttonsPressed) {
            buttons += Integer.toString(button);
        }
        return buttons;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int newButtonValue = event.getAction() == KeyEvent.ACTION_UP ? 0 : 1;
        Date now = new Date();
        String timestamp = dateFormat.format(now);

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                buttonsPressed[Button.UP.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                buttonsPressed[Button.DOWN.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                buttonsPressed[Button.LEFT.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                buttonsPressed[Button.RIGHT.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                buttonsPressed[Button.X.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                buttonsPressed[Button.A.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                buttonsPressed[Button.Y.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                buttonsPressed[Button.B.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                buttonsPressed[Button.R1.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                buttonsPressed[Button.RTRIGGER.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_L2:
                buttonsPressed[Button.LTRIGGER.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_L1:
                buttonsPressed[Button.L1.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_START:
                buttonsPressed[Button.START.ordinal()] = newButtonValue;
                break;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                buttonsPressed[Button.SELECT.ordinal()] = newButtonValue;
                break;
            default:
                return super.dispatchKeyEvent(event);
        }

        JSONObject buttonEvent = new JSONObject();
        try {
            buttonEvent.put("userID", userID);
            buttonEvent.put("appID", appID);
            buttonEvent.put("timestamp", timestamp);
            buttonEvent.put("competition", gameID);
            buttonEvent.put("event", buttonsString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ServerConnection connection = new ServerConnection();
        connection.execute(buttonEvent);

        return true;
    }

    // To debug with controller connected use physical phone and Wifi
    // Instructions: https://developer.android.com/studio/command-line/adb#wireless
    // this function is copied from https://developer.android.com/training/game-controllers/controller-input
    public ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }
}