package edu.msu.bielick3.cataapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        DataSnapshot test = route.child("BusNumber");
                        buses.add(test.getValue().toString());
                    }

                    Firebase busRef = new Firebase("https://sizzling-fire-5776.firebaseio.com/vehicles");
                    busRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Marker> markerArray = new ArrayList<Marker>();
                            for (String bus : buses) {
                                String latitude = dataSnapshot.child(bus).child("Lat").getValue().toString();
                                String longitude = dataSnapshot.child(bus).child("Long").getValue().toString();
                                LatLng pos = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                Marker m = new Marker("", "", pos);
                                m.setMarker(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.bus_marker));
                                markerArray.add(m);
                            }
                            mv.clear();
                            for(Marker m : markerArray) {
                                mv.addMarker(m);
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                query.removeEventListener(this);
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