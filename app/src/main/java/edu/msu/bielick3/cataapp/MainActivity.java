package edu.msu.bielick3.cataapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.MapFragment;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.MapViewListener;
import com.mapbox.mapboxsdk.views.util.TilesLoadedListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends Activity {

    private MapView mv;
    private String route;
    private Button routesButton;
    private Button button2;
    private TextView timerText;
    private long timerStart;
    private Handler handler = new Handler();
    private Runnable timer = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            timerText.setText("Last Update: "+((System.currentTimeMillis()-timerStart)/1000));
      /* and here comes the "trick" */
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = (TextView)findViewById(R.id.timerText);

        routesButton = (Button) findViewById(R.id.routes);
        routesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), routeSelect.class);
                startActivityForResult(intent, 1);
            }
        });

        mv = (MapView) findViewById(R.id.mapview);
        mv.setUserLocationEnabled(true);
        mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.NONE);
        mv.setCenter(new ILatLng() {
            @Override
            public double getLatitude() {
                return 42.7369792;
            }

            @Override
            public double getLongitude() {
                return -84.48386540000001;
            }

            @Override
            public double getAltitude() {
                return 0;
            }
        });
        mv.setZoom(13);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                route = data.getStringExtra("route");
                displayRoute(route);
            }
        }
    }

    private ArrayList<Marker> prevBus = new ArrayList<Marker>();
    public void displayRoute(String route) {



        final String routeTemp = route;
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://sizzling-fire-5776.firebaseio.com/routes");

        final Query query = ref.orderByChild("RouteNumber").equalTo(route);

        final ValueEventListener vehicleListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    final ArrayList<String> buses = new ArrayList<String>();;
                    for(DataSnapshot route : dataSnapshot.getChildren()) {
                        DataSnapshot busNumber = route.child("BusNumber");
                        buses.add(busNumber.getValue().toString());
                    }

                    Firebase busRef = new Firebase("https://sizzling-fire-5776.firebaseio.com/vehicles");
                    busRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Marker> busMarkerArray = new ArrayList<Marker>();
                            for (String bus : buses) {
                                String latitude = dataSnapshot.child(bus).child("Lat").getValue().toString();
                                String longitude = dataSnapshot.child(bus).child("Long").getValue().toString();
                                LatLng pos = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                Marker m = new Marker("", "", pos);
                                m.setMarker(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bus_marker));
                                busMarkerArray.add(m);
                            }
                            for (Marker m : prevBus) {
                                mv.removeMarker(m);
                            }
                            prevBus.clear();
                            for (Marker m : busMarkerArray) {
                                mv.addMarker(m);
                            }
                            timerStart = System.currentTimeMillis();
                            handler.postDelayed(timer, 1000);
                            mv.invalidate();
                            prevBus = busMarkerArray;

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        query.addValueEventListener(vehicleListener);

        routesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.removeEventListener(vehicleListener);
                Intent intent = new Intent(getApplicationContext(), routeSelect.class);
                startActivityForResult(intent, 1);
            }
        });
    }
}