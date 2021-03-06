package com.matthewericpeter.triptracker;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip implements Serializable {
    public Trip(){
        name = "";
        lat = new ArrayList<>();
        lng = new ArrayList<>();
    }
    String name;
    List<Double> lat;
    List<Double> lng;
    Double startLat;
    Double startLng;
    Double endLat;
    Double endLng;
    Date startTime;
    Date endTime;
}
