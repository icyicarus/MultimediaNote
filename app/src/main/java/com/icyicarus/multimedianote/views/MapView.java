package com.icyicarus.multimedianote.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icyicarus.multimedianote.R;
import com.icyicarus.multimedianote.Variables;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MapView extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    protected Marker oldMarker = null;
    protected GoogleMap mGoogleMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected FloatingSearchView floatingSearchView;
    protected String extraLatitude = " ";
    protected String extraLongitude = " ";
    protected long markerClickTime = 0;
    protected RequestQueue queue;
    protected String mapApiKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.view_map);
        floatingSearchView = (FloatingSearchView) findViewById(R.id.floatingSearchViewGoogleMap);
        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
//                getCoordinate(currentQuery);
                getCoordinateWithVolley(currentQuery);
            }
        });

        extraLatitude = getIntent().getStringExtra(Variables.EXTRA_NOTE_LATITUDE);
        extraLongitude = getIntent().getStringExtra(Variables.EXTRA_NOTE_LONGITUDE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map_note);
        mapFragment.getMapAsync(MapView.this);

        try {
            ApplicationInfo info = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            mapApiKey = info.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.e("No API key");
            finish();
        }

        queue = Volley.newRequestQueue(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapView.this).addConnectionCallbacks(MapView.this).addOnConnectionFailedListener(MapView.this).addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        if (oldMarker != null) {
            Intent i = getIntent();
            i.putExtra("latitude", String.valueOf(oldMarker.getPosition().latitude));
            i.putExtra("longitude", String.valueOf(oldMarker.getPosition().longitude));
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        AndPermission.onRequestPermissionsResult(MapView.this, requestCode, permissions, grantResults, new PermissionListener() {
//            @Override
//            public void onSucceed(int requestCode, List<String> grantPermissions) {
//                onMapReady(mGoogleMap);
//            }
//
//            @Override
//            public void onFailed(int requestCode, List<String> deniedPermissions) {
//                Toast.makeText(MapView.this, "Permission Denied, Please Check", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//
//        });
//    }

    // OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(MapView.this);
        mGoogleMap.setOnMarkerClickListener(MapView.this);
        mGoogleMap.setOnMarkerDragListener(MapView.this);
        mGoogleMap.setPadding(0, 150, 0, 0);
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AndPermission.with(MapView.this).requestCode(1).permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).send();
            } else {
                if (oldMarker != null) {
                    oldMarker.remove();
                }
                if (!Objects.equals(extraLatitude, " ") && !Objects.equals(extraLongitude, " ")) {
                    oldMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(extraLatitude), Double.parseDouble(extraLongitude))).draggable(true).flat(false));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldMarker.getPosition(), 16));
//                    getAddress(oldMarker.getPosition());
                    getAddressWithVolley(oldMarker.getPosition());
                } else {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                }
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AndPermission.with(MapView.this).requestCode(1).permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).send();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation == null) {
                Toast.makeText(MapView.this, "Unable to locate, please check location service status", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapView.this, "Connection suspended", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    // GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapView.this, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    // GoogleMap.OnMarkerClickListener
    @Override
    public boolean onMarkerClick(Marker marker) {
        if ((System.currentTimeMillis() - markerClickTime) > 1000) {
            Toast.makeText(this, "Press again to delete", Toast.LENGTH_SHORT).show();
            markerClickTime = System.currentTimeMillis();
        } else {
            oldMarker.remove();
            oldMarker = null;
        }
        return true;
    }

    // GoogleMap.OnMapClickListener
    @Override
    public void onMapClick(LatLng latLng) {
        if (oldMarker != null)
            oldMarker.remove();
//        getAddress(latLng);
        getAddressWithVolley(latLng);
        oldMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).flat(true));
    }

    // GoogleMap.OnMarkerDragListener
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        getAddress(marker.getPosition());
        getAddressWithVolley(marker.getPosition());
    }

    public void getAddressWithVolley(LatLng latLng) {
        String url = "https://maps.google.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=" + mapApiKey;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final String address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            floatingSearchView.setSearchText(address);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("error");
                Logger.e(error.toString());
            }
        });
        queue.add(request);
    }

    public void getCoordinateWithVolley(String address) {
        address = address.replace(" ", "+");
        String url = "https://maps.google.com/maps/api/geocode/json?address=" + address + "&key=" + mapApiKey;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    final double latitude = jsonObject.getDouble("lat");
                    final double longitude = jsonObject.getDouble("lng");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (oldMarker != null)
                                oldMarker.remove();
                            oldMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true).flat(false));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e(error.toString());
            }
        });
        queue.add(request);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldMarker.getPosition(), 16));
    }
}
