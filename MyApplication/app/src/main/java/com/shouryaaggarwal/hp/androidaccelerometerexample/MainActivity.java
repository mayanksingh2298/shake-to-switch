package com.shouryaaggarwal.hp.androidaccelerometerexample;

import android.app.ActionBar;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.io.DataOutputStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static float softXThreshold = 20;
    public static float hardXThreshold = 55;
    public static float ZThreshold = 20;
    public static boolean enableHardX = true;
    public static boolean enableSoftX = true;
    public static String host;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaXMin = 0;
    private float deltaZMax = 0;

    private boolean ongoingX = false;
    private boolean ongoingZ = false;

    private float coolDown = 0.5f;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private String resultant = "";

    private long last_update = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, action, HARDXTHRESHOLD, SOFTXTHRESHOLD, ZTHRESHOLD;
    private ToggleButton toggle;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("enableSoftX", enableSoftX);
        savedInstanceState.putBoolean("enableHardX", enableHardX);
        savedInstanceState.putFloat("softXThreshold", softXThreshold);
        savedInstanceState.putFloat("hardXThreshold", hardXThreshold);
        savedInstanceState.putFloat("ZThreshold", ZThreshold);
        savedInstanceState.putString("IP",host);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        enableSoftX = savedInstanceState.getBoolean("enableSoftX");
        enableHardX = savedInstanceState.getBoolean("enableHardX");
        softXThreshold = savedInstanceState.getFloat("softXThreshold");
        hardXThreshold = savedInstanceState.getFloat("hardXThreshold");
        host = savedInstanceState.getString("IP");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fail! we dont have an accelerometer!
        }

        //initialize vibration

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeViews() {
//        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
//        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
//        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        action = (TextView) findViewById(R.id.action);


        SOFTXTHRESHOLD = (TextView) findViewById(R.id.softXThreshold);
        HARDXTHRESHOLD = (TextView) findViewById(R.id.hardXThreshold);
        ZTHRESHOLD = (TextView) findViewById(R.id.ZThreshold);

        toggle = (ToggleButton) findViewById(R.id.sensor_switch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    unregisterSensor();
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                    coolDownFinish();
                    softXThreshold = 20;
                    hardXThreshold = 35;
                    ZThreshold = 15;
                } else {
                    unregisterSensor();
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    coolDownFinish();
                    softXThreshold = 20;
                    hardXThreshold = 55;
                    ZThreshold = 20;
                }
            }
        });
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
    public void mayankCheck(){
            if (deltaX > hardXThreshold){
                resultant = "Hard-Right";
                action.setText(resultant);
                new send_message().execute(resultant);
                unregisterSensor();
                Utils.delay(coolDown, new Utils.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        // Do something after delay
                        coolDownFinish();
                    }
                });
            }else if(deltaX > softXThreshold){
                resultant = "Right";
                action.setText(resultant);
                new send_message().execute(resultant);
                unregisterSensor();
                Utils.delay(coolDown, new Utils.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        // Do something after delay
                        coolDownFinish();
                    }
                });
            }else if(deltaX < -hardXThreshold){
                resultant = "Hard-Left";
                action.setText(resultant);
                new send_message().execute(resultant);
                unregisterSensor();
                Utils.delay(coolDown, new Utils.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        // Do something after delay
                        coolDownFinish();
                    }
                });
            }else if(deltaX < -softXThreshold){
                resultant = "Left";
                action.setText(resultant);
                new send_message().execute(resultant);
                unregisterSensor();
                Utils.delay(coolDown, new Utils.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        // Do something after delay
                        coolDownFinish();
                    }
                });
            }
    }
    public void CheckX() {
        if (deltaXMax > Math.abs(deltaXMin)){
            if (deltaXMax > hardXThreshold && enableHardX) {
                resultant = "Hard-Right";
                action.setText(resultant);
                new send_message().execute(resultant);
            }
            else if (enableSoftX) {
                resultant = "Right";
                action.setText(resultant);
                new send_message().execute(resultant);
            }
        }
        else{
            if (deltaXMin < -hardXThreshold && enableHardX) {
                resultant = "Hard-Left";
                action.setText(resultant);
                new send_message().execute(resultant);
            }
            else if (enableSoftX) {
                resultant = "Left";
                action.setText(resultant);
                new send_message().execute(resultant);
            }
        }
        deltaXMax = 0;
        deltaXMin = 0;
        unregisterSensor();
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

    private void unregisterSensor(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long curTime = System.currentTimeMillis();
        if ((curTime - last_update)>10) {

            // clean current values
            displayCleanValues();
            // display the current x,y,z accelerometer values
            displayCurrentValues();

            // get the change of the x,y,z values of the accelerometer

            deltaX = event.values[0];
            deltaY = event.values[1];
            deltaZ = event.values[2];
//            Log.d("DeltaXmax",String.valueOf(deltaXMax));
//            Log.d("DeltaXmin",String.valueOf(deltaXMin));

            SOFTXTHRESHOLD.setText(Float.toString(softXThreshold));
            HARDXTHRESHOLD.setText(Float.toString(hardXThreshold));
            ZTHRESHOLD.setText(Float.toString(ZThreshold));


            // display the max x,y,z accelerometer values
            displayMaxValues();


            // if the change is below Threshold, it is not a flick
//            mayankCheck();
            if (Math.abs(deltaX) < softXThreshold) {
                deltaX = 0;
                if (ongoingX && !ongoingZ) {
                    ongoingX = false;
                    CheckX();
                } else if (ongoingX && ongoingZ) {
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
                        unregisterSensor();
                        Utils.delay(coolDown, new Utils.DelayCallback() {
                            @Override
                            public void afterDelay() {
                                // Do something after delay
                                coolDownFinish();
                            }
                        });
                    } else {
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
            last_update = System.currentTimeMillis();
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

                    host = ip_textview.getText().toString();
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
