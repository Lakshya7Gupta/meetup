package com.example.sid.meetup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE=123;//identify and tracks our permission request
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "554a5aef59db92292545a0320803b06e";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;



    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;  // starts or stops requesting location updates
    LocationListener mLocationListener;  //component that will be notified if the location is actually changed i.e it will do the checking on updates of device location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(WeatherController.this,ChangeCityController.class);
                startActivity(myIntent);

            }
        });
    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {  //gets implemented just after onCreate and just before user interacts with the activity
        super.onResume();
        Log.d("Clima", "onResume() called");

        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("City");

        if(city!=null)
        {
            getWeatherForNewCity(city);

        }else {
            Log.d("Clima", "Getting weather for current location ");
            getWeatherForCurrentLocation();
        }

    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city)
    {
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);
    }

    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);//requesting location service in form of location manager object

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //here we get our location data
                Log.d("Clima", "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());//extracts longitude
                String latitude = String.valueOf(location.getLatitude());//extracts latitude
                Log.d("Clima", "longitude is: " + longitude);
                Log.d("Clima", "latitude is: " + latitude);

                //supplying query parameters
                RequestParams params = new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);





            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Clima", "onStatusChanged() callback received. Status: " + status);
                Log.d("Clima", "2 means AVAILABLE, 1: TEMPORARILY_UNAVAILABLE, 0: OUT_OF_SERVICE");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Clima", "onProviderEnabled() callback received. Provider: " + provider);

            }



            @Override
            public void onProviderDisabled(String s) {
                //in case we do not get our location data it will help to debug our app i.e location provider is disabled in our app
                Log.d("Clima", "onProviderDisabled() callback received");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);//requests location permission from the user

            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER,MIN_TIME,MIN_DISTANCE, mLocationListener);//instructs location manager to start requesting updates


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this checks if user granted our app with location permission ,our app will be notified from the OS via this callback

        if(requestCode==REQUEST_CODE)
        {//checks if result in the callback pertains to our permission request
            //note response is stored in grant result parameter
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Log.d("Clima", "onRequestPermissionsResult() :Permission granted!");
                getWeatherForCurrentLocation();
            }
            else
            {
                Log.d("Clima", "Permission denied !");

            }

        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private  void letsDoSomeNetworking(RequestParams params)
    {  //handling http requests
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                Log.d("Clima","Success! JSON: " + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);

            }
            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
                Log.e("Clima","Fail " + e.toString());
                Log.d("Clima","Status code " + statusCode);
                Toast.makeText(WeatherController.this,"Request Failed",Toast.LENGTH_SHORT).show();
            }

        });

    }


    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather){

        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        int resourceID = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);

    }


    // TODO: Add onPause() here:
    @Override
    protected void onPause() {
        super.onPause();
        //in order to stop checking for location updates when our app is closing or no loger in foreground
        //help us preserve device battery life
        if (mLocationManager != null)
            mLocationManager.removeUpdates(mLocationListener);

    }
}
