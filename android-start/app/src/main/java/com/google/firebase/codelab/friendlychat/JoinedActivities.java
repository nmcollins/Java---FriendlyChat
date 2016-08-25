package com.google.firebase.codelab.friendlychat;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nicholas on 2016-07-30.
 */

//Class used to manage viewing of activities the user has chosen to join
public class JoinedActivities extends AppCompatActivity {
    DatabaseHandler db = new DatabaseHandler(this);
    PhotoInfo currentPhotoInfoRecord = null;
    List<PhotoInfo> photoInfoRecords;
    List<PhotoInfo> rawPhotoInfoRecords;
    int imageNumber = 0;

    //Retrieve list of joined activities and let the user swipe to view them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joined_activities);
        try {
            String displayText = "";
            rawPhotoInfoRecords = db.getAllJoinedActivities();
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
                imageViewMainImage.setOnTouchListener(new OnSwipeTouchListener(JoinedActivities.this) {
                //Swipe in one direction to move to the next item in the list
                public void onSwipeRight() {
                    if (imageNumber < photoInfoRecords.size()) {
                        imageNumber++;
                        TextView descriptionText = (TextView) findViewById(R.id.tvDescription);
                        ImageView imageViewMainImage = (ImageView) findViewById(R.id.iVCurrentImage);
                        currentPhotoInfoRecord = photoInfoRecords.get(imageNumber);
                        descriptionText.setText(currentPhotoInfoRecord.getDescription());
                        imageViewMainImage.setImageBitmap(currentPhotoInfoRecord.getImage());
                    }
                }
                //Swipe in the other direction to move to the previous item in the list
                public void onSwipeLeft() {
                    if (imageNumber > 0) {
                        imageNumber--;
                        TextView descriptionText = (TextView) findViewById(R.id.tvDescription);
                        ImageView imageViewMainImage = (ImageView) findViewById(R.id.iVCurrentImage);
                        currentPhotoInfoRecord = photoInfoRecords.get(imageNumber);
                        descriptionText.setText(currentPhotoInfoRecord.getDescription());
                        imageViewMainImage.setImageBitmap(currentPhotoInfoRecord.getImage());
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
