package com.google.firebase.codelab.friendlychat;

import android.graphics.Bitmap;

/**
 * Created by Nicholas on 2016-06-24.
 */


//Class to contain info for photos of activities and associated data
public class PhotoInfo {

    private int _id;
    private String _fileName;
    private String _description;
    private String _owner;
    private String _status;
    private Bitmap _image;
    private String _latitude;
    private String _longitude;

    // constructor
    public PhotoInfo(String description, String owner, String status, Bitmap image, String latitude, String longitude) {
      //  this._fileName = fileName;
        this._description = description;
        this._owner = owner;
        this._status = status;
        this._image = image;
        this._latitude = latitude;
        this._longitude = longitude;

    }
    // getting last name
    public String getFileName(){
        return this._fileName;
    }

    // set the last name
    public void setFileName(String fileName){
        this._fileName = fileName;
    }


    public String getDescription(){
        return this._description;
    }

    // set the last name
    public void setDescription(String description){
        this._description = description;
    }

    //get the first name
    public String getOwner(){
        return this._owner;
    }

    //set the first name
    public void setOwner(String owner){
        this._owner = owner;
    }

    //get the mark
    public String getStatus(){
        return this._status;
    }

    //set the mark
    public void setStatus(String status){
        this._status = status;
    }

    //get the id
    public int getId(){
        return this._id;
    }

    //set the id
    public void setId(int id) {
        this._id = id;
    }

    //get the id
    public String getLatitude(){
        return this._latitude;
    }

    //set the id
    public void setLatitude(String latitude) {
        this._latitude = latitude;
    }

    //get the id
    public Bitmap getImage(){
        return this._image;
    }

    //set the id
    public void setImage(Bitmap image) {
        this._image = image;
    }

    //get the id
    public String getLongitude(){
        return this._longitude;
    }

    //set the id
    public void setLongitude(String longitude) {
        this._longitude = longitude;
    }

}
