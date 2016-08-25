/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

//Class based on the online tutorial for Firebase, https://codelabs.developers.google.com/codelabs/firebase-android/#0
//This is a chat app, it has been adapted for purposes of this assignment
public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String activityBitmap;
    private String latitude;
    private String longitude;
    private String timeAdded;

    public FriendlyMessage() {
    }

    //A FriendlyMessage will contain information about an activity; the name "Friendly" being used
    //because this is something to be shared with other people
    public FriendlyMessage(String text, String name, String photoUrl, String activityBitmap, String latitude, String longitude, String timeAdded) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.activityBitmap = activityBitmap;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeAdded = timeAdded;
    }

    //Get the text associated with an activity
    public String getText() {
        return text;
    }

    //Set the text associated with an activity
    public void setText(String text) {
        this.text = text;
    }

    //Get name
    public String getName() {
        return name;
    }

    //Set name
    public void setName(String name) {
        this.name = name;
    }

    //Get Bitmap
    public String getActivityBitmap() {
        return activityBitmap;
    }

    //Set Bitmap
    public void setActivityBitmap(String activityBitmap) {
        this.activityBitmap = activityBitmap;
    }

    //Get url
    public String getPhotoUrl() {
        return photoUrl;
    }

    //Set url
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    //Get Latitude
    public String getLatitude() {
        return latitude;
    }

    //Set Latitude
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    //Get Longitude
    public String getLongitude() {
        return longitude;
    }

    //Set Longitude
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    //Get time added
    public String getTimeAdded() {
        return timeAdded;
    }

    //Set time added
    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }
}
