package com.google.firebase.codelab.friendlychat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicholas on 2016-06-24.
 */

//Basic SQLite Database Handler class to craete tables,
//populate them, etc.
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 23;

    // Database Name
    private static final String DATABASE_NAME = "activityPhotosManager";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String TABLE_PHOTO_INFO = "photoInfo";
    private static final String TABLE_LAST_TIME_CHECKED = "timeOfLastCheck";

    // activities Table Columns names
    private static final String KEY_ID = "id";
    private static final String FIELD_DESCRIPTION = "descriptiveText";
    private static final String FIELD_OWNER = "ownerName";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_IMAGE = "image";
    private static final String FIELD_TIME_ADDED = "timeadded";
    private static final String FIELD_TIME_OF_LAST_CHECK = "timeChecked";
    private static final String FIELD_LATITUDE = "latitude";
    private static final String FIELD_LONGITUDE = "longitude";

    //Create tables when needed, such as. first time app is run on a device
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHOTOS_TABLE = "CREATE TABLE " + TABLE_PHOTO_INFO + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIELD_DESCRIPTION + " TEXT,"
                + FIELD_OWNER + " TEXT,"
                + FIELD_STATUS + " TEXT,"
                + FIELD_IMAGE + " BLOB,"
                + FIELD_TIME_ADDED + " TEXT,"
                + FIELD_LATITUDE + " TEXT,"
                + FIELD_LONGITUDE + " TEXT" + ")";
        db.execSQL(CREATE_PHOTOS_TABLE);

        String CREATE_TIME_CHECK_TABLE = "CREATE TABLE " + TABLE_LAST_TIME_CHECKED + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIELD_TIME_OF_LAST_CHECK + " TEXT" + ")";
        db.execSQL(CREATE_TIME_CHECK_TABLE);
    }

    //This code is executed when we increase the version number.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_TIME_CHECKED);
        // Create tables again
        onCreate(db);
    }

    //We use the last time checked to help determine which photos of activities on the cloud
    //are new
    public String getLastTimeChecked() {
        SQLiteDatabase db = this.getWritableDatabase();
        String timeOfLastCheck = "";
        try{
            String[] allColumns = new String[] { KEY_ID,
                    FIELD_TIME_OF_LAST_CHECK };

            Cursor c = db.query(TABLE_LAST_TIME_CHECKED, allColumns, null, null, null,
                    null, null);
            if (c.moveToFirst()) {
                do {
                    timeOfLastCheck = c.getString(c.getColumnIndex(FIELD_TIME_OF_LAST_CHECK));
                } while (c.moveToNext());
            }
        }
        catch (Exception e) {
            Log.v("Activities App","Error when retrieving data, method getLastTimeChecked()");
        }
        db.close();
        return timeOfLastCheck;
    }

    //After finding new activities and storing data on a device, we need to update
    //the value of last time checked
    public void updateLastTimeChecked(String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_TIME_OF_LAST_CHECK, time);

        String whereClause = "id = ?";
        String[] whereArgs = new String[] {
                String.valueOf(1)
        };
        db.update(TABLE_LAST_TIME_CHECKED, values, whereClause, whereArgs);
        db.close();
    }

    //This method takes an activity object and uses the data to insert a row on the  last time
    // checked table, this has to be done the first time you look for new activities
    public void addLastTimeChecked(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_TIME_OF_LAST_CHECK, time);
        db.insert(TABLE_LAST_TIME_CHECKED, null, values);
        db.close();
    }


    //This method takes an activity object and uses the data to insert a row on the table
    public void addPhotoInfo(PhotoInfo photoInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_DESCRIPTION, photoInfo.getDescription());
        values.put(FIELD_OWNER, photoInfo.getOwner());
        values.put(FIELD_STATUS, photoInfo.getStatus());
        values.put(FIELD_LATITUDE, photoInfo.getLatitude());
        values.put(FIELD_LONGITUDE, photoInfo.getLongitude());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (photoInfo.getImage() != null) {
            photoInfo.getImage().compress(Bitmap.CompressFormat.PNG, 0, stream);
            values.put(FIELD_IMAGE, stream.toByteArray());
            db.insert(TABLE_PHOTO_INFO, null, values);
            db.close();
        }
    }

    //This method takes data entered and updates a specific record on the photo info table based on id number
    public void updatePhoto(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_STATUS, status);

        String whereClause = "id = ?";
        String[] whereArgs = new String[] {
                String.valueOf(id)
        };

        db.update(TABLE_PHOTO_INFO, values, whereClause, whereArgs);
        db.close();
    }

    //This method deletes a specific  record based on id
    public void deletePhoto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String whereClause = "id = ?";
            String[] whereArgs = new String[] {
                    String.valueOf(id)
            };
            db.delete(TABLE_PHOTO_INFO, whereClause, whereArgs);
        }
        catch (Exception e) {
            Log.v("Activity Photo App","Error when attempting delete of record id:" + id);
        }
        db.close();
    }

    //This method retrieves a list of all Activity records.
    public List <PhotoInfo> getAllPhotoInfoRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<PhotoInfo> photoInfoRecords = new ArrayList<PhotoInfo>();
        try{
            String[] allColumns = new String[] { KEY_ID,
                    FIELD_DESCRIPTION, FIELD_OWNER, FIELD_STATUS, FIELD_IMAGE };

            Cursor c = db.query(TABLE_PHOTO_INFO, allColumns, null, null, null,
                    null, null);
            if (c.moveToFirst()) {
                do {
                    PhotoInfo photoInfo = new PhotoInfo("","","", null, "", "");
                    photoInfo.setDescription(c.getString(c.getColumnIndex(FIELD_DESCRIPTION)));
                    photoInfo.setOwner(c.getString(c.getColumnIndex(FIELD_OWNER)));
                    photoInfo.setStatus(c.getString(c.getColumnIndex(FIELD_STATUS)));
                    byte[] retrievedImage = c.getBlob(c.getColumnIndex(FIELD_IMAGE));
                    photoInfo.setImage(BitmapFactory.decodeByteArray(retrievedImage, 0, retrievedImage.length));
                    photoInfo.setLatitude(c.getString(c.getColumnIndex(FIELD_LATITUDE)));
                    photoInfo.setLongitude(c.getString(c.getColumnIndex(FIELD_LONGITUDE)));
                    photoInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    photoInfoRecords.add(photoInfo);
                } while (c.moveToNext());
            }
        }
        catch (Exception e) {
            Log.v("Join activities App","Error when retrieving data, method getAllPhotoInfoRecords()");
        }
        db.close();
        return photoInfoRecords;
    }

    //This method gets a list of all activities the user has joined
    public List <PhotoInfo> getAllJoinedActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<PhotoInfo> photoInfoRecords = new ArrayList<PhotoInfo>();
        try{
            String[] allColumns = new String[] { KEY_ID,
                    FIELD_DESCRIPTION, FIELD_OWNER, FIELD_STATUS, FIELD_IMAGE, FIELD_LATITUDE, FIELD_LONGITUDE};

            String whereClause = "status = ?";
            String[] whereArgs = new String[] {
                    "Joined"
            };

            Cursor c = db.query(TABLE_PHOTO_INFO, allColumns, whereClause, whereArgs, null,
                    null, null);
            if (c.moveToFirst()) {
                do {
                    PhotoInfo photoInfo = new PhotoInfo("","","", null, "", "");
                    photoInfo.setDescription(c.getString(c.getColumnIndex(FIELD_DESCRIPTION)));
                    photoInfo.setOwner(c.getString(c.getColumnIndex(FIELD_OWNER)));
                    photoInfo.setStatus(c.getString(c.getColumnIndex(FIELD_STATUS)));
                    byte[] retrievedImage = c.getBlob(c.getColumnIndex(FIELD_IMAGE));
                    photoInfo.setImage(BitmapFactory.decodeByteArray(retrievedImage, 0, retrievedImage.length));
                    photoInfo.setLatitude(c.getString(c.getColumnIndex(FIELD_LATITUDE)));
                    photoInfo.setLongitude(c.getString(c.getColumnIndex(FIELD_LONGITUDE)));
                    photoInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    photoInfoRecords.add(photoInfo);
                } while (c.moveToNext());
            }
        }
        catch (Exception e) {
            Log.v("Activities App","Error when retrieving data, method getAllJoinedActivities()");
        }
        db.close();
        return photoInfoRecords;
    }

    //This gets a list of all new activities, which have been downloaded from the cloud
    public List <PhotoInfo> getAllNewActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<PhotoInfo> photoInfoRecords = new ArrayList<PhotoInfo>();
        try{
            String[] allColumns = new String[] { KEY_ID,
                    FIELD_DESCRIPTION, FIELD_OWNER, FIELD_STATUS, FIELD_IMAGE, FIELD_LATITUDE, FIELD_LONGITUDE };

            String whereClause = "status = ?";
            String[] whereArgs = new String[] {
                    "New"
            };
            Cursor c = db.query(TABLE_PHOTO_INFO, allColumns, whereClause, whereArgs, null,
                    null, null);
            if (c.moveToFirst()) {
                do {
                    PhotoInfo photoInfo = new PhotoInfo("","","", null, "", "");
                    photoInfo.setDescription(c.getString(c.getColumnIndex(FIELD_DESCRIPTION)));
                    photoInfo.setOwner(c.getString(c.getColumnIndex(FIELD_OWNER)));
                    photoInfo.setStatus(c.getString(c.getColumnIndex(FIELD_STATUS)));
                    byte[] retrievedImage = c.getBlob(c.getColumnIndex(FIELD_IMAGE));
                    photoInfo.setImage(BitmapFactory.decodeByteArray(retrievedImage, 0, retrievedImage.length));
                    photoInfo.setLatitude(c.getString(c.getColumnIndex(FIELD_LATITUDE)));
                    photoInfo.setLongitude(c.getString(c.getColumnIndex(FIELD_LONGITUDE)));
                    photoInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    photoInfoRecords.add(photoInfo);
                } while (c.moveToNext());
            }
        }
        catch (Exception e) {
            Log.v("Activities App","Error when retrieving data, method getAllNewActivities()");
        }
        db.close();
        return photoInfoRecords;
    }

}
