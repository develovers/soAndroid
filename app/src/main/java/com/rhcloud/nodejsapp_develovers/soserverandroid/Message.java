package com.rhcloud.nodejsapp_develovers.soserverandroid;

/**
 * Created by hervemuneza on 7/10/15.
 */
public class Message {

    private String fromName, message;
    private boolean isSelf;
    private String latitude;
    private String longitude;

    public Message() {
    }

    public Message(String fromName, String message, boolean isSelf, String lat, String lon) {
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
        this.latitude = "lat: "+lat.substring(0,6);
        this.longitude = "long: "+lon.substring(0,7);
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}