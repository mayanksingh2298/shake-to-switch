package com.shouryaaggarwal.hp.androidaccelerometerexample;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Context context;

    private float deltaXMax = 0;
    private float deltaXMin = 0;
    private float deltaZMax = 0;
    private float XThreshold = 25;
    private float ZThreshold = 25;
    private float YThreshold = 25;
    private boolean ongoing = false;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private String resultant = "";

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, action;

    public Vibrator v;

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        context = getApplicationContext();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fail! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        action = (TextView) findViewById(R.id.action);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void CheckX() {
        if (deltaXMax > Math.abs(deltaXMin)){
            resultant = "Next";
            action.setText(resultant);
        }
        else{
            resultant = "Previous";
            action.setText(resultant);
        }
        deltaXMax = 0;
        deltaXMin = 0;
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
        // display the max x,y,z accelerometer values
        displayMaxValues();



        // get the change of the x,y,z values of the accelerometer
        deltaX = (lastX - event.values[0]);
        deltaY = (lastY - event.values[1]);
        deltaZ = (lastZ - event.values[2]);



        // if the change is below Threshold, it is not a flick
        if (Math.abs(deltaX) < XThreshold) {
            deltaX = 0;
            if (ongoing) {
                ongoing = false;
                CheckX();
            }
        }
        if (Math.abs(deltaY) < YThreshold)
            deltaY = 0;
        if (Math.abs(deltaZ) < ZThreshold)
            deltaZ = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            //v.vibrate(50);
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            ongoing = true;
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaX < deltaXMin) {
            ongoing = true;
            deltaXMin = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
    //    if (Math.abs(deltaY) > Math.abs(deltaYMax)) {
    //        deltaYMax = deltaY;
    //        maxY.setText(Float.toString(deltaYMax));
    //    }
        if (Math.abs(deltaZ) > ZThreshold) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
            resultant = "Play/Pause";
            action.setText(resultant);
        }
    }
}
