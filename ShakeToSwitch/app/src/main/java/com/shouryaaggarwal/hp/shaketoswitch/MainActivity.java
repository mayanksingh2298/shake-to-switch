package com.shouryaaggarwal.hp.shaketoswitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.io.DataOutputStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static float softXThreshold = 20;
    public static float hardXThreshold = 55;
    public static float ZThreshold = 30;
    public static boolean enableHardX = true;
    public static boolean enableSoftX = true;
    public static boolean enableZ = true;
    public static String host;
    private static boolean toggleSensor = false;

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

    private TextView action, HARDXTHRESHOLD, SOFTXTHRESHOLD, ZTHRESHOLD, ip_textview;
    private ToggleButton toggle;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        host = ip_textview.getText().toString();
        savedInstanceState.putBoolean("enableSoftX", enableSoftX);
        savedInstanceState.putBoolean("enableHardX", enableHardX);
        savedInstanceState.putBoolean("enableZ", enableZ);
        savedInstanceState.putFloat("softXThreshold", softXThreshold);
        savedInstanceState.putFloat("hardXThreshold", hardXThreshold);
        savedInstanceState.putFloat("ZThreshold", ZThreshold);
        savedInstanceState.putString("IP",host);
        savedInstanceState.putBoolean("toggleSensor", toggleSensor);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        enableSoftX = savedInstanceState.getBoolean("enableSoftX");
        enableHardX = savedInstanceState.getBoolean("enableHardX");
        enableZ = savedInstanceState.getBoolean("enableZ");
        softXThreshold = savedInstanceState.getFloat("softXThreshold");
        hardXThreshold = savedInstanceState.getFloat("hardXThreshold");
        ZThreshold = savedInstanceState.getFloat("ZThreshold");
        host = savedInstanceState.getString("IP");
        toggleSensor = savedInstanceState.getBoolean("toggleSensor");
        updateDisplay();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        GetPreferences();

        setContentView(R.layout.activity_main);
        initializeViews();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // success! we have an accelerometer
            if(toggleSensor)
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            else
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            // fail! we dont have an accelerometer!
        }


    }


    public void GetPreferences() {
        SharedPreferences settings = getSharedPreferences("settings",0);
        enableSoftX = settings.getBoolean("enableSoftX", true);
        enableHardX = settings.getBoolean("enableHardX", true);
        enableZ = settings.getBoolean("enableZ", true);
        softXThreshold = settings.getFloat("softXThreshold", 20);
        hardXThreshold = settings.getFloat("hardXThreshold", 55);
        ZThreshold = settings.getFloat("ZThreshold", 30);
        host = settings.getString("IP", "xx.xxx.x.xx");
        toggleSensor = settings.getBoolean("toggleSensor", false);
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
        
        action = (TextView) findViewById(R.id.action);

        ip_textview = (TextView) findViewById(R.id.ip);

        SOFTXTHRESHOLD = (TextView) findViewById(R.id.softXThreshold);
        HARDXTHRESHOLD = (TextView) findViewById(R.id.hardXThreshold);
        ZTHRESHOLD = (TextView) findViewById(R.id.ZThreshold);

        toggle = (ToggleButton) findViewById(R.id.sensor_switch);

        updateDisplay();

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSensor = isChecked;
                if (isChecked) {
                    unregisterSensor();
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                    coolDownFinish();
                } else {
                    unregisterSensor();
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    coolDownFinish();
                }
            }
        });
        }

     public void updateDisplay() {
         SOFTXTHRESHOLD.setText(Float.toString(softXThreshold));
         HARDXTHRESHOLD.setText(Float.toString(hardXThreshold));
         ZTHRESHOLD.setText(Float.toString(ZThreshold));
         ip_textview.setText(host);
         toggle.setChecked(toggleSensor);
         //toggle.setSelected(toggleSensor);
     }


    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        updateDisplay();
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void coolDownFinish(){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        resultant = "Flick Awaited";
        action.setText(resultant);
    }

    //onPause() do not unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        storePreferences();
//        sensorManager.unregisterListener(this);
    }

    protected void onStop() {
        super.onStop();
        storePreferences();
    }

    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        storePreferences();
    }


    public void storePreferences() {
        host = ip_textview.getText().toString();
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putBoolean("enableSoftX", enableSoftX);
        editor.putBoolean("enableHardX", enableHardX);
        editor.putBoolean("enableZ", enableZ);
        editor.putFloat("softXThreshold", softXThreshold);
        editor.putFloat("hardXThreshold", hardXThreshold);
        editor.putFloat("ZThreshold", ZThreshold);
        editor.putString("IP",host);
        editor.putBoolean("toggleSensor", toggleSensor);
        editor.commit();
    }

    public void CheckX() {
        boolean flick = false;
        if (deltaXMax > Math.abs(deltaXMin)){
            if (deltaXMax > hardXThreshold && enableHardX) {
                resultant = "Hard-Right";
                flick = true;
            }
            else if (enableSoftX) {
                resultant = "Right";
                flick = true;
            }
        }
        else{
            if (deltaXMin < -hardXThreshold && enableHardX) {
                resultant = "Hard-Left";
                flick = true;
            }
            else if (enableSoftX) {
                resultant = "Left";
                flick = true;
            }
        }
        deltaXMax = 0;
        deltaXMin = 0;
        if(flick) {
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


            // get the change of the x,y,z values of the accelerometer

            deltaX = event.values[0];
            deltaY = event.values[1];
            deltaZ = event.values[2];
//            Log.d("DeltaXmax",String.valueOf(deltaXMax));
//            Log.d("DeltaXmin",String.valueOf(deltaXMin));


            // display the max x,y,z accelerometer values
            displayMaxValues();


            // if the change is below Threshold, it is not a flick
            if (Math.abs(deltaX) < softXThreshold) {
                deltaX = 0;
                if (ongoingX && !ongoingZ) {
                    ongoingX = false;
                    CheckX();
                } else if (ongoingX && ongoingZ) {
                    ongoingX = false;
                    ongoingZ = false;
                    if ((Math.abs(deltaZMax) > deltaXMax) && (Math.abs(deltaZMax) > Math.abs(deltaXMin)) && enableZ) {
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
                    if(enableZ) {
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
            last_update = System.currentTimeMillis();
        }
    }



    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > Math.max(deltaXMax, softXThreshold)) {
            ongoingX = true;
            deltaXMax = deltaX;

        }
        if (deltaX < Math.min(deltaXMin, -softXThreshold)) {
            ongoingX = true;
            deltaXMin = deltaX;
        }
        if (Math.abs(deltaZ) > ZThreshold  ) {
            ongoingZ = true;
            deltaZMax = deltaZ;
        }
    }
    private class send_message extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Switch switch_view = (Switch)findViewById(R.id.connect_switch);
            if (switch_view.isChecked()) {
                Log.d("Button", "sending data");
                try {
                    Socket socket;
                    DataOutputStream oos = null;

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
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
