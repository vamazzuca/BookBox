package com.cmput301f20t14.bookbox.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import static com.google.android.gms.vision.barcode.Barcode.ALL_FORMATS;

/**
 * This is the scanning activity. It makes use of a surface view and
 * a barcode detector and cameraSource for the scanning functionality
 * of the app. Activity is finished when a valid barcode is scanned.
 * Control is then returned to HomeActivity class. The scanning
 * feature makes use of the Google Vision API.
 * @author Olivier Vadiavaloo, Carter Sabadash
 * @version 2020.11.23
 * @see BarcodeDetector
 * @see CameraSource
 * */
public class ScanningActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private String username;
    private SurfaceView scannerPreview;
    private TextView scannedContent;
    private Button keepScanning;
    private Button confirm;
    private String barcode;
    private BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_scanning);
        
        // do this before creating the CameraSource
        handlePermissions(); // will continue in continueSetup()

        // get username extra
        username = getIntent().getExtras().getString(User.USERNAME);

        // get textview that will contain the scanned content
        scannedContent = (TextView) findViewById(R.id.scanned_content);

        // get buttons
        keepScanning = (Button) findViewById(R.id.keep_scanning);
        confirm = (Button) findViewById(R.id.confirm_scan);

        // set up the bottom navigation bar
        bottomNavigationView();
    }

    /**
     * We need to wait for camera permissions before continuing to create the CameraSource
     */
    public void continueSetup() {
        // get SurfaceView object
        scannerPreview = (SurfaceView) findViewById(R.id.preview_camera);

        // initialise barcodeDectector
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(ALL_FORMATS)
                .build();

        // Build CameraSource object
        final CameraSource cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        // add a callback to the SurfaceHolder of the preview and request
        // permissions to launch camera if permission is not granted yet
        setUpHolder(cameraSource);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish activity and pass back barcode as extra
                Intent intent = new Intent();
                intent.putExtra(HomeActivity.BARCODE, barcode);
                setResult(CommonStatusCodes.SUCCESS, intent);
                finish();
            }
        });

        keepScanning.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                handlePermissions();
                // try starting the camera source and catch any exception
                // finish activity if camera couldn't start
                try {
                    cameraSource.start(scannerPreview.getHolder());
                    setUpDetector(cameraSource);
                    scannedContent.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ScanningActivity.this, "An Error occurred", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    /**
     * Add callback to the surface holder of the scannerPreview
     */
    public void setUpHolder(final CameraSource cameraSource) {
        scannerPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                // try starting the camera source and catch any exception
                // finish activity if camera couldn't start
                try {
                    cameraSource.start(scannerPreview.getHolder());
                    setUpDetector(cameraSource);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ScanningActivity.this, "An Error occurred", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }

        });
    }

    public void setUpDetector(final CameraSource cameraSource) {
        // set the processor to scan any barcode showing up
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            // on receiving a detection, finish the activity and return the scanned barcode
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                // get all the detected barcodes
                final SparseArray<Barcode> scannedBarcodes = detections.getDetectedItems();

                // if there was any scanned barcode, display an alert dialog with a positive
                // and a negative button. The positive button finishes the activity on clicking
                // while the negative button relauches the scanning feature
                if (scannedBarcodes.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // stop camerasource
                            cameraSource.stop();
                            barcode = scannedBarcodes.valueAt(0).displayValue.toString();
                            scannedContent.setText(barcode);
                            confirm.setEnabled(true);
                            confirm.setAlpha(1.0f);
                        }
                    });
                }
            }
        });
    }

    /**
     * This method handles permission checks. It requests
     * for the permission to use the device's camera
     * @author  Olivier Vadiavaloo, Carter Sabadash
     * @version 2020.11.23
     * */
    private void handlePermissions() {
        if (ActivityCompat.checkSelfPermission(ScanningActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ScanningActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        } else { continueSetup(); }
    }

    /**
     * This is part of the Interface to return the results of the camera permission request
     * For now we continue to setup for either result (it's just a blank activity if denied)
     * @param requestCode The requestCode
     * @param permissions The permissions asked for
     * @param grantResults If the permissions asked for were granted
     * @author Carter Sabadash
     * @version 2020.11.23
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        continueSetup();
    }

    /**
     * Implementation of the bottom navigation bar for switching to different
     * activity views, such as home, profile, notifications and lists
     * References: https://www.youtube.com/watch?v=JjfSjMs0ImQ&feature=youtu.be
     * @author Alex Mazzuca
     * @author Carter Sabadash
     * @version 2020.10.25
     */
    private void bottomNavigationView(){
        //Home Navigation bar implementation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.home_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.lists_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ListsActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_bottom_nav:
                        finish();
                        return true;
                    case R.id.notification_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // returns from launched a potential succession of
        // Scanning activities and will ultimately pass back
        // the scanned barcode to the initial activity that
        // started the Scanning activity (for example, HomeActivity
        if (resultCode == CommonStatusCodes.SUCCESS) {
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
        }
    }

}