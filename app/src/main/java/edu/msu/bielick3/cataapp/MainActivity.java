package edu.msu.bielick3.cataapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

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

    private WebView mWebView;
//    JavaScriptInterface JSInterface;

    // MapBox
    private MapView mv;
    private UserLocationOverlay myLocationOverlay;
    private String currentMap = null;
    private String route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mv = (MapView) findViewById(R.id.mapview);
        mv.setUserLocationEnabled(true);
        //mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW_BEARING);
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

    public void routeSelect(View view) {
        Intent intent = new Intent(this, routeSelect.class);
        startActivityForResult(intent, 1);
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
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://sizzling-fire-5776.firebaseio.com/routes");

        Query query = ref.orderByChild("RouteNumber").equalTo(route);

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    ArrayList<String> routeStops = null;
                    for(DataSnapshot route : dataSnapshot.getChildren()) {
                        DataSnapshot stops = route.child("Stops");
                        for(DataSnapshot oneStop : stops.getChildren()) {
                            Log.d("TEST", oneStop.child("StopId").getValue().toString());
                            routeStops.add(oneStop.child("StopId").getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}