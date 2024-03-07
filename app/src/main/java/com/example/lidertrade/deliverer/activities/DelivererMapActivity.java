package com.example.lidertrade.deliverer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.helpers.DirectionsJSONParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DelivererMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    FirebaseFirestore db;
    Double distance;
    Marker marker;
    private final int FINE_PERMISSION_CODE = 1;
    private final String GOOGLE_MAP_API_KEY = "AIzaSyAo1ihuyi1fwZ8f-sdkB4zvTCshKWCqRp8";
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    LatLng origin, destination,currLoc ;
    CollectionReference selling;
    private CardView hybridMapBtn, terrainMapBtn, satelliteMapBtn;
    Location currentLocation;
    SearchView searchView;
    private GoogleMap myMap;
    private AdminOrderModel orderPendingListFragmentModel;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverer_map_activity);

        //        Variables
        hybridMapBtn = findViewById(R.id.idBtnHybridMap);
        terrainMapBtn = findViewById(R.id.idBtnTerrainMap);
        satelliteMapBtn = findViewById(R.id.idBtnSatelliteMap);
        searchView = findViewById(R.id.idSearchView);
        db = FirebaseFirestore.getInstance();
        selling = db.collection("Orders");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent = getIntent();
        orderPendingListFragmentModel = (AdminOrderModel) intent.getSerializableExtra("orderModel");

        getLastLocation();
        mapChanger();
        searchInMap();

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        currLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(currLoc).title("Sizning joylashuvingiz")
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.baseline_location_on_24)));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 13));

        getAllCustomersLocations();
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getPosition().equals(currLoc)){
                    distance = SphericalUtil.computeDistanceBetween(currLoc, marker.getPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(DelivererMapActivity.this);
                    builder.setMessage(MessageFormat.format("Masofa (ko'chish): {0}km. Ushbu manzilni tanlaysizmi?",
                            String.format("%.2f", distance / 1000)));
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ha", (DialogInterface.OnClickListener) (dialog, which) -> {
                        origin = currLoc;
                        destination = marker.getPosition();
                        String url = getDirectionsUrl(origin, destination);
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);
                    });
                    builder.setNegativeButton("Yo'q", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return false;
            }
        });
    }

    private void getAllCustomersLocations() {
        if (orderPendingListFragmentModel != null){
            selling.document(orderPendingListFragmentModel.getOrderId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists() &&
                            documentSnapshot.get("sellerGeoPoint")!=null &&
                            currLoc!=documentSnapshot.get("sellerGeoPoint"))
                    {
                        GeoPoint geoPoint = (GeoPoint) documentSnapshot.get("sellerGeoPoint");
                        assert geoPoint != null;
                        LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        myMap.addMarker(new MarkerOptions().position(location)
                                .title(Objects.requireNonNull(documentSnapshot.get("customerName")).toString())
                                .icon(BitmapFromVector(getApplicationContext(), R.drawable.order_location)));
                    }
                }
            });

        }else{
            selling.whereNotEqualTo("packageStatus", -1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && !value.isEmpty()) {
                        List<DocumentSnapshot> list = value.getDocuments();
                        for (DocumentSnapshot d : list) {
                            if (d.exists() && d.get("sellerGeoPoint")!=null) {
                                GeoPoint geoPoint = (GeoPoint) d.get("sellerGeoPoint");
                                assert geoPoint != null;
                                LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                myMap.addMarker(new MarkerOptions().position(location)
                                        .title(Objects.requireNonNull(d.get("customerName")).toString())
                                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.order_location)));
                            }
                        }
                    } else {
                        Toast.makeText(DelivererMapActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();
            for (int i = 0; i < result.size(); i++) {
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }
            if (points.size() != 0)
                myMap.addPolyline(lineOptions);
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = BASE_URL + output + "?" + parameters + "&key=" + GOOGLE_MAP_API_KEY;
        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(DelivererMapActivity.this);
                }
            }
        });
    }
    private void searchInMap() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(DelivererMapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!addressList.isEmpty()){
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }else {
                        Toast.makeText(DelivererMapActivity.this, "So'rov bo'yicha hech narsa topilmadi", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void mapChanger(){
        // adding on click listener for our hybrid map button.
        hybridMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change
                // the type of map to hybrid.
                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        // adding on click listener for our terrain map button.
        terrainMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change
                // the type of terrain map.
                myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        // adding on click listener for our satellite map button.
        satelliteMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change the
                // type of satellite map.
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}