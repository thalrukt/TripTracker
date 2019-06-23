package com.matthewericpeter.triptracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WaypointActivity extends AppCompatActivity {
    //Get Database reference for "Waypoints" (list of all waypoints)
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference wayRef = rootRef.child("Waypoints");
    List<Waypoint> inWaypoints = new ArrayList<Waypoint>();
    List<Waypoint> outWaypoints = new ArrayList<Waypoint>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        inWaypoints = (List<Waypoint>) i.getSerializableExtra("LIST");
        outWaypoints = (List<Waypoint>) i.getSerializableExtra("DISPLAY_LIST");

        setContentView(R.layout.activity_waypoint);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //call function to create buttons from saved waypoints here
        for (int count = 0; count < inWaypoints.size(); count++) {
            addWaypointButton(inWaypoints.get(count));
        }

        wayRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addWaypoints(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //If a new Waypoint has been changed, get the changes and update
                //TODO:Write a method to find&update waypointa(s) that have been changed

                System.out.println("CHANGE CHILD LISTENER ");
                Waypoint newLoc = dataSnapshot.getValue(Waypoint.class);
                //System.out.println("Title: " + newLoc.name);
                System.out.println("Name: " + newLoc.name);
                System.out.println("Latitude: " + newLoc.latitude);
                System.out.println("Longitude: " + newLoc.longitude);

                // Waypoint newPt = new Waypoint(_name, newLoc);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Tells us if a waypoint is removed. We probably wont need this outside of testing
                //Child Removed:TODO:determine if method is needed (delete if possible)
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Tells us if a waypoint is moved. We probably wont need this outside of testing
                //Child Moved:TODO:determine if method is needed (delete if possible)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Error catch if the database can't be loaded, probably should notify the user as this
                //would mostly be caused by a bad connection
                Toast.makeText(WaypointActivity.this, "Failed to connect to server, check connection and try again", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("LIST", (Serializable) outWaypoints);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void addWaypoints(DataSnapshot dataSnapshot) {
        //get waypoint object from dataSnapshot
        final Waypoint newLoc = dataSnapshot.getValue(Waypoint.class);
        addWaypointButton(newLoc);
    }
    public void addWaypointButton(Waypoint w){
        LinearLayout ll = findViewById(R.id.layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        //create button
        final Button btn = new Button(this);

        //give the button the waypoint as a tag*might not be necessary?*
        btn.setTag(w);

        //Check if waypoint is on the Display list, mark the button if so
        if (waypointDisplayed(w) != null) {
            String DisplayText = w.name + "\t(Displayed)";
            btn.setText(DisplayText);
        } else {
            btn.setText(w.name);
        }

        btn.setLayoutParams(params);
        //add button to linear layout
        ll.addView(btn);

        //create onClick Listener for button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Waypoint w = (Waypoint)btn.getTag();
                Waypoint temp = waypointDisplayed(w);
                if(temp != null){
                    outWaypoints.remove(temp);
                    btn.setText(w.name);
                    Toast.makeText(v.getContext(),"Removed waypoint: " + w.name,
                            Toast.LENGTH_LONG).show();
                }
                else{
                    outWaypoints.add(w);
                    String DisplayText = w.name + "\t(Displayed)";
                    btn.setText(DisplayText);
                    Toast.makeText(v.getContext(),"Added waypoint: " + w.name,
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    public Waypoint waypointDisplayed(Waypoint w){
        for (Waypoint temp : outWaypoints){
            if (temp.name.equals(w.name)){
                return temp;
            }
        }
        return null;
    }
}
