package com.cmput301f20t14.bookbox.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import static com.google.android.gms.vision.barcode.Barcode.EAN_13;
import static com.google.android.gms.vision.barcode.Barcode.ISBN;
import static com.google.android.gms.vision.barcode.Barcode.UPC_A;

/**
 * This is the scanning activity. It makes use of a surface view and
 * a barcode detector and cameraSource for the scanning functionality
 * of the app. Activity is finished when a valid barcode is scanned.
 * Control is then returned to HomeActivity class. The scanning
 * feature makes use of the Google Vision API.
 * @author Olivier Vadiavaloo
 * @version 2020.10.24
 * @see BarcodeDetector
 * @see CameraSource
 * */
public class ScanningActivity extends AppCompatActivity {
    private String username;
    private SurfaceView scannerPreview;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_scanning);

        // get username extra
        username = getIntent().getExtras().getString(User.USERNAME);

        // set up the bottom navigation bar
        bottomNavigationView();

        // get SurfaceView object
        scannerPreview = (SurfaceView) findViewById(R.id.preview_camera);

        // initialise barcodeDectector
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(EAN_13)
                .build();

        // Build CameraSource object
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .build();

        // add a callback to the SurfaceHolder of the preview and request
        // permissions to launch camera if permission is not granted yet
        scannerPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                handlePermissions();

                // try starting the camera source and catch any exception
                // finish activity if camera couldn't start
                try {
                    cameraSource.start(scannerPreview.getHolder());
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
                    final String barcode = scannedBarcodes.valueAt(0).displayValue.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // build Alert dialog to show user the scanned barcode
                            // if user presses "Confirm", the Scanning activity finishes
                            // and returns the scanned barcode to the previous activity
                            // If the user presses "Cancel", the scanning activity resume
                            AlertDialog dialog = new AlertDialog.Builder(ScanningActivity.this)
                                    .setTitle("ISBN: " + barcode)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @SuppressLint("MissingPermission")
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ScanningActivity.this, ScanningActivity.class);
                                            intent.putExtra(User.USERNAME, username);
                                            startActivityForResult(intent, HomeActivity.REQUEST_CODE_SCANNING);
                                        }
                                    })
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // finish activity and pass back barcode as extra
                                            Intent intent = new Intent();
                                            intent.putExtra(HomeActivity.BARCODE, barcode);
                                            setResult(CommonStatusCodes.SUCCESS, intent);
                                            finish();
                                        }
                                    })
                                    .create();

                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            cameraSource.stop();
                                        }
                            });
                            dialog.show();
                        }
                    });
                }
            }
        });

    }

    /**
     * This method handles permission checks. It requests
     * for the permission to use the device's camera
     * @author  Olivier Vadiavaloo
     * @version 2020.10.24
     * */
    private void handlePermissions() {
        if (ActivityCompat.checkSelfPermission(ScanningActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ScanningActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
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