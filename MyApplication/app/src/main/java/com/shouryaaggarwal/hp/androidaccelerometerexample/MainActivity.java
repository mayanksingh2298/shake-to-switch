package com.shouryaaggarwal.hp.androidaccelerometerexample;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.Socket;


public class MainActivity extends Activity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaXMin = 0;
    private float deltaZMax = 0;
    private float softXThreshold = 15;
    private float hardXThreshold = 35;
    private float ZThreshold = 15;

    private boolean ongoingX = false;
    private boolean ongoingZ = false;

    private float coolDown = 0.5f;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private String resultant = "";

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, action;

    public Vibrator v;

        @Override
    public void onCreate(Bundle savedInstanceState) {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // success! we have an accelerometer

//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fail! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
//        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
//        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
//        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        action = (TextView) findViewById(R.id.action);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        super.onResume();
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void coolDownFinish(){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        resultant = "Flick Awaited";
        action.setText(resultant);
//        connect();
    }

    //onPause() do not unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);
    }
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    public void CheckX() {
        if (deltaXMax > Math.abs(deltaXMin)){
            if (deltaXMax > hardXThreshold)
                resultant = "Hard-Right";
            else
                resultant = "Right";
            action.setText(resultant);
//            connect();
            new send_message().execute(resultant);
        }
        else{
            if (deltaXMin < -hardXThreshold)
                resultant = "Hard-Left";
            else
                resultant = "Left";
            action.setText(resultant);
            //            connect();
            new send_message().execute(resultant);
        }
        deltaXMax = 0;
        deltaXMin = 0;
        sensorManager.unregisterListener(this);
        Utils.delay(coolDown, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                // Do something after delay
                coolDownFinish();
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();



        // get the change of the x,y,z values of the accelerometer

        deltaX = event.values[0];
        deltaY = event.values[1];
        deltaZ = event.values[2];

        // display the max x,y,z accelerometer values
        displayMaxValues();



        // if the change is below Threshold, it is not a flick
        if (Math.abs(deltaX) < softXThreshold) {
            deltaX = 0;
            if (ongoingX && !ongoingZ) {
                ongoingX = false;
                CheckX();
            }
            else if (ongoingX && ongoingZ) {
                ongoingX = false;
                ongoingZ = false;
                if ((Math.abs(deltaZMax) > deltaXMax) && (Math.abs(deltaZMax) > Math.abs(deltaXMin))) {
                    deltaZMax = 0;
                    deltaXMax = 0;
                    deltaXMin = 0;
                    resultant = "Play/Pause";
                    action.setText(resultant);
//                    connect();
                    new send_message().execute(resultant);

                    sensorManager.unregisterListener(this);
                    Utils.delay(coolDown, new Utils.DelayCallback() {
                        @Override
                        public void afterDelay() {
                            // Do something after delay
                            coolDownFinish();
                        }
                    });
                }
                else {
                    deltaZMax = 0;
                    CheckX();
                }
            }
        }
        if (Math.abs(deltaZ) < ZThreshold) {
            deltaZ = 0;
            if (ongoingZ && !ongoingX) {
                ongoingZ = false;
                deltaZMax = 0;
                resultant = "Play/Pause";
                action.setText(resultant);
//                connect();
                new send_message().execute(resultant);

                sensorManager.unregisterListener(this);
                Utils.delay(coolDown, new Utils.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        // Do something after delay
                        coolDownFinish();
                    }
                });
            }
        }
    }

    public void displayCleanValues() {
        //currentX.setText("0.0");
        currentY.setText("0.0");
        //currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
//        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
//        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > Math.max(deltaXMax, softXThreshold)) {
            ongoingX = true;
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaX < Math.min(deltaXMin, -softXThreshold)) {
            ongoingX = true;
            deltaXMin = deltaX;
            maxX.setText(Float.toString(deltaXMin));
        }
        if (Math.abs(deltaZ) > ZThreshold  ) {
            ongoingZ = true;
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
//    public void connect() {
//        Switch switch_view = (Switch)findViewById(R.id.connect_switch);
//        if (switch_view.isChecked()) {
//            Log.d("Button", "sending data");
//            try {
//                Socket socket;
//                DataOutputStream oos = null;
//
//                TextView ip_textview = (TextView) findViewById(R.id.ip);
//                TextView message_textview = (TextView) findViewById(R.id.action);
//
//                String host = ip_textview.getText().toString();
//                socket = new Socket(host, 12346);
//
//                oos = new DataOutputStream(socket.getOutputStream());
//                oos.writeUTF(message_textview.getText().toString());
//                oos.flush();
//
//                oos.close();
//                socket.close();
//
//            } catch (Exception e) {
//                Log.d("Exception", e.toString());
//            }
//        }
//    }
    private class send_message extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Switch switch_view = (Switch)findViewById(R.id.connect_switch);
            if (switch_view.isChecked()) {
                Log.d("Button", "sending data");
                try {
                    Socket socket;
                    DataOutputStream oos = null;

                    TextView ip_textview = (TextView) findViewById(R.id.ip);
                    TextView message_textview = (TextView) findViewById(R.id.action);

                    String host = ip_textview.getText().toString();
                    socket = new Socket(host, 12346);

                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeUTF(params[0]);
                    oos.flush();

                    oos.close();
                    socket.close();

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
//            TextView txt = (TextView) findViewById(R.id.output);
//            txt.setText("Executed"); // txt.setText(result);
//             might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
