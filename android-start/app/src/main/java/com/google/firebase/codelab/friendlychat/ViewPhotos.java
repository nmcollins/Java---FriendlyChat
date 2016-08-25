package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by Ncollins9293 on 6/24/2016.
 */

//View photos of new activities and swipe to join or not depending on swipe direction
public class ViewPhotos extends AppCompatActivity implements SensorEventListener
{
    DatabaseHandler db = new DatabaseHandler(this);

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    private SeekBar seekbar = null;

    public double currentLatitude;
    public double currentLongitude;


    LocationManager myLocationManager;
    String PROVIDER = LocationManager.GPS_PROVIDER;


    PhotoInfo currentPhotoInfoRecord = null;

    List<PhotoInfo> photoInfoRecords;
    List<PhotoInfo> rawPhotoInfoRecords;
    int imageNumber = 0;


    @Override

    //Get form Database new activities for user to say what to join or not by swiping
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photos);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);   //get last known location, if available
        Location location = myLocationManager.getLastKnownLocation(PROVIDER);
        showMyLocation(location);

        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(seekBarChanged);

        try {
            rawPhotoInfoRecords = db.getAllNewActivities();  //Get activities the user has not responded to yet.
            photoInfoRecords = rawPhotoInfoRecords;
            TextView descriptionText = (TextView) findViewById(R.id.tvDescription);
            ImageView imageViewMainImage = (ImageView) findViewById(R.id.iVCurrentImage);
            if (photoInfoRecords.size() == 0) {
                descriptionText.setText("ERROR" + "\n\n" + "No Data Found!");
            } else
            {
                currentPhotoInfoRecord = photoInfoRecords.get(0);
                descriptionText.setText(currentPhotoInfoRecord.getDescription());
                imageViewMainImage.setImageBitmap(currentPhotoInfoRecord.getImage());            }
                imageViewMainImage.setOnTouchListener(new OnSwipeTouchListener(ViewPhotos.this) {

                //Similar routines for Swiping right or left; but we need to update the
                //database differently.  Also, increment imageNumber so we will move on to
                //and display the next image.
                public void onSwipeRight() {
                    imageNumber++;
                    TextView descriptionText = (TextView) findViewById(R.id.tvDescription);
                    ImageView imageViewMainImage = (ImageView) findViewById(R.id.iVCurrentImage);
                    Toast.makeText(ViewPhotos.this, "Not joined", Toast.LENGTH_SHORT).show();
                    db.updatePhoto(currentPhotoInfoRecord.getId(), "Disliked");
                    currentPhotoInfoRecord = photoInfoRecords.get(imageNumber);
                    descriptionText.setText(currentPhotoInfoRecord.getDescription());
                    imageViewMainImage.setImageBitmap(currentPhotoInfoRecord.getImage());
                }
                public void onSwipeLeft() {
                    imageNumber++;
                    TextView descriptionText = (TextView) findViewById(R.id.tvDescription);
                    ImageView imageViewMainImage = (ImageView) findViewById(R.id.iVCurrentImage);
                    Toast.makeText(ViewPhotos.this, "Joined!", Toast.LENGTH_SHORT).show();
                    db.updatePhoto(currentPhotoInfoRecord.getId(), "Joined");
                    currentPhotoInfoRecord = photoInfoRecords.get(imageNumber);
                    descriptionText.setText(currentPhotoInfoRecord.getDescription());
                    imageViewMainImage.setImageBitmap(currentPhotoInfoRecord.getImage());
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    //Calculate the distance between two points based on latitude and longitude co-ordinates
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    //The SeekBar allows the user to specify a radius from their current position defining
    //a circle in which to look for activities
    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        //When the user touches the SeekBar to change the radius, get the value and
        //update the list of activities based on which ones are within the defined
        //circle.
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int k = progress;
            TextView searchDistance = (TextView) findViewById(R.id.searchDistance);
            searchDistance.setText(String.valueOf(k));

            rawPhotoInfoRecords = db.getAllNewActivities();
            if (photoInfoRecords != null) {
                photoInfoRecords.clear();
            }
            for (int i=0; i < rawPhotoInfoRecords.size(); i++) {
                String itemLatitude = rawPhotoInfoRecords.get(i).getLatitude();
                String itemLongitude = rawPhotoInfoRecords.get(i).getLongitude();
                if (itemLatitude != null && itemLongitude != null) {
                    double itemDoubleLat = Double.parseDouble(itemLatitude);
                    double itemDoubleLong = Double.parseDouble(itemLongitude);
                    double distanceBetween = distance(itemDoubleLat, itemDoubleLong, currentLatitude, currentLongitude);
                    if (distanceBetween <= k) {
                        photoInfoRecords.add(rawPhotoInfoRecords.get(i));
                    }
                }
            }
            //Now we know what activities are in the desired radius, update the ImageView
            //with the first one in that list
            if (photoInfoRecords != null) {
                if (photoInfoRecords.size() > 0) {
                    imageNumber = 0;
                    currentPhotoInfoRecord = photoInfoRecords.get(0);
                    TextView descriptionText = (TextView) findViewById(R.id.tvDescription);
                    ImageView imageViewMainImage = (ImageView) findViewById(R.id.iVCurrentImage);
                    descriptionText.setText(currentPhotoInfoRecord.getDescription());
                    imageViewMainImage.setImageBitmap(currentPhotoInfoRecord.getImage());
                    Log.i("OnSeekBarChangeListener", "onProgressChanged");
                }
            }
        }
    };

    //Set latitude and longitude to zero if any problems with Geolocation service mean
    //data is not available
    private void showMyLocation(Location l){
        if(l == null) {
            currentLatitude = 0;
            currentLatitude = 0;
        }
        else{
            currentLatitude = l.getLatitude();
            currentLongitude = l.getLongitude();
        }
    }


    //Methods for managing location sensor - it is common practice to set these up
    //Make sure the magnetometer and acceleratometer are used by the sensormanager after
    //a service interruption or pause
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    //Deregister sensors during a psue
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    //Check for events from sensors used to determine locatoin
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            mCurrentDegree = -azimuthInDegress;
        }
    }

    //Method stub not really needed in this case
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


}
