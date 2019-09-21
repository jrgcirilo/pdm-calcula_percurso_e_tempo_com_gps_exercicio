package com.example.calcula_percurso_e_tempo_com_gps_exercicio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private double latAt, latAnt, longAt, longAnt;
    private float dst=0;
    private static final int REQUEST_PERMISSION_GPS = 1001;

    private Button atrgpsButton;
    private Button atvgpsButton;
    private Button dstgpsButton;
    private Button inipercButton;
    private Button psqButton;
    private Button termpercButton;
    private Chronometer tmppercChronometer;
    private EditText psqEditText;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView dstpercTextView;
    private TextView dstpercvlrTextView;
    private TextView tmppercTextView;

    Location locAnt = new Location(LocationManager.GPS_PROVIDER);
    Location locAt = new Location(LocationManager.GPS_PROVIDER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();

        atrgpsButton = findViewById(R.id.atrgpsButton);
        atvgpsButton = findViewById(R.id.atvgpsButton);
        dstgpsButton = findViewById(R.id.dstgpsButton);
        inipercButton = findViewById(R.id.inipercButton);
        psqButton = findViewById(R.id.psqButton);
        termpercButton = findViewById(R.id.termpercButton);
        tmppercChronometer =findViewById(R.id.tmppercChronometer);
        tmppercChronometer.setFormat("%s");
        psqEditText = findViewById(R.id.psqEditText);
        dstpercTextView = findViewById(R.id.dstpercTextView);
        dstpercvlrTextView = findViewById(R.id.dstpercvlrTextView);
        tmppercTextView = findViewById(R.id.tmppercTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location){
                latAt = location.getLatitude();
                longAt = location.getLongitude();
                locAt.setLatitude(latAt);
                locAt.setLongitude(longAt);
                dst += location.distanceTo(locAnt);
                locAnt = location;
                dstpercvlrTextView.setText(getString(R.string.dst, dst));
                if (locAt==null){
                    Toast.makeText(getApplicationContext(),"Localização não encontrada", Toast.LENGTH_SHORT).show();
                }
        }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){

            }

            @Override
            public void onProviderEnabled (String provider){

            }

            @Override
            public void onProviderDisabled (String provider){

            }


        };

        atrgpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"GPS autorizado", Toast.LENGTH_SHORT).show();
                }
                else{
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            REQUEST_PERMISSION_GPS
                    );
                }
            }
        });

        atvgpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                2000,
                                1,
                                locationListener
                        );
                        Toast.makeText(getApplicationContext(),"GPS ativado", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(getApplicationContext(),"Erro ao ativar GPS", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Autorize o GPS", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dstgpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isOn){
                    locationManager.removeUpdates(locationListener);
                    Toast.makeText(getApplicationContext(),"GPS desativado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"GPS desligado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        inipercButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(
                        MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    tmppercChronometer.setBase(SystemClock.elapsedRealtime());
                    onChronometerTick(tmppercChronometer);
                    Toast.makeText(getApplicationContext(),"Percurso iniciado", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Autorize o GPS", Toast.LENGTH_SHORT).show();
                }

            }
        });

        termpercButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dst = 0;
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    tmppercChronometer.stop();
                    Toast.makeText(getApplicationContext(),"Percurso terminado", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Autorize o GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        psqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Uri uri = Uri.parse(
                            String.format(Locale.getDefault(),
                                    "geo:%f,%f?q=" + psqEditText.getText(),
                                    latAnt, longAnt)
                    );
                    Intent intent = new Intent (Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            }
        });

    }

    public void onChronometerTick (Chronometer chronometer) {
        chronometer.start();
        dstpercvlrTextView.setText(getString(R.string.dst, dst));
        tmppercChronometer.setBase(SystemClock.elapsedRealtime());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_GPS){
            if (grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            2000,
                            1,
                            locationListener
                    );
                }
            }else {
                Toast.makeText(getApplicationContext(),"Sem GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
}

