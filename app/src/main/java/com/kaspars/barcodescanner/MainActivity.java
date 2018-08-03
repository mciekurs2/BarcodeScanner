package com.kaspars.barcodescanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.IOException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 123;
    private static final int MY_CAMERA_PERMISSION_CODE = 456;
    private RecyclerView rw;
    private ListAdapter adapter;
    private DatabaseReference rootRef;
    private ArrayList<BarcodeData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rw = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rw.setLayoutManager(manager);
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("barcodes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    BarcodeData data = ds.getValue(BarcodeData.class);
                    list.add(data);
                }

                adapter = new ListAdapter(list);
                rw.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

/*    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_CAMERA_PERMISSION_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }*/

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_CAMERA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Uri uri = data.getData();

            //Toast.makeText(getApplicationContext(), String.valueOf(bitmap.getByteCount()), Toast.LENGTH_SHORT).show();



            //lai dabuutu vsionImage no bitmap
            FirebaseVisionImage image = null;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //dabū instanci(nepieciešams lai atpazītu barcodes)
            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                    .getVisionBarcodeDetector();

            //izsauc detectImage method un saglabaa datus sarakstaa
            Task<List<FirebaseVisionBarcode>> task = detector.detectInImage(image);
            task.addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                @Override
                public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                    //Toast.makeText(getApplicationContext(), "Suss", Toast.LENGTH_SHORT).show();
                    for (FirebaseVisionBarcode barcode: firebaseVisionBarcodes) {
                        //Toast.makeText(getApplicationContext(), String.valueOf(barcode.getValueType()), Toast.LENGTH_LONG).show();

                        Date currentTime = Calendar.getInstance().getTime();
                        String time = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentTime);

                        BarcodeData data = new BarcodeData(barcode.getRawValue(),
                                String.valueOf(barcode.getValueType()), time);
                        rootRef.child("barcodes").child(currentTime.toString()).setValue(data);


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), String.valueOf(e.getMessage()), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scan_item:

                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

                if(!hasPermissions(this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    if(hasPermissions(this, PERMISSIONS)){
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

                return true;
            case R.id.info_item:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog(){
        String[] types = {"1: TYPE_UNKNOWN", "2: TYPE_CONTACT_INFO", "3: TYPE_EMAIL",
                "4: TYPE_ISBN", "5: TYPE_PHONE", "6: TYPE_PRODUCT", "7: TYPE_SMS", "8: TYPE_TEXT",
                "9: TYPE_URL", "10: TYPE_WIFI", "11: TYPE_GEO", "12: TYPE_CALENDAR_EVENT", "13: TYPE_DRIVER_LICENSE"};
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("List of barcode types");
        build.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do stuff....
            }
        }).create().show();


    }
}
