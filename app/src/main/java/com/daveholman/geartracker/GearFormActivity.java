package com.daveholman.geartracker;

import android.*;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.daveholman.geartracker.models.GearData;
import com.daveholman.geartracker.models.User;
import com.daveholman.geartracker.utilities.DownloadImageTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GearFormActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "GearFormActivity";
    private static final String REQUIRED = "Required";
    private static final String INVALID = "Invalid";

    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMS = 102;

    private Uri mFileUri = null;
    private Uri mDownloadUrl = null;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    // [START declare_ref]
    private StorageReference mStorageRef;
    // [END declare_ref]

    private EditText mTitleField;
    private EditText mManufacturerField;
    private EditText mYearField;
    private EditText mNotesField;
    private ImageView mImageView;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_form);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        // Initialize Firebase Storage Ref
        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]


        mTitleField = (EditText) findViewById(R.id.field_title);
        mNotesField = (EditText) findViewById(R.id.field_notes);
        mManufacturerField = (EditText) findViewById(R.id.field_manufacturer);
        mYearField = (EditText) findViewById(R.id.field_year);
        mImageView = (ImageView) findViewById(R.id.image_gear);

        findViewById(R.id.fab_camera_gear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        findViewById(R.id.fab_submit_gear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitGear();
                }
        });
    }
    @AfterPermissionGranted(RC_STORAGE_PERMS)
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String perm = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission),
                    RC_STORAGE_PERMS, perm);
            return;
        }

        // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Choose file storage location
        File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
        mFileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        // Launch intent
        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
    }

    private void submitGear() {
        final String title = mTitleField.getText().toString();
        final String notes = mNotesField.getText().toString();
        final String manufacturer = mManufacturerField.getText().toString();
        final int year;

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Notes is required
        if (TextUtils.isEmpty(notes)) {
            mNotesField.setError(REQUIRED);
            return;
        }

        // Manufacturer is required
        if (TextUtils.isEmpty(manufacturer)) {
            mManufacturerField.setError(REQUIRED);
            return;
        }

        int parsedYear = 0;

        try {
            parsedYear = Integer.parseInt(mYearField.getText().toString());
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            if(parsedYear > currentYear +1)
            {
                mYearField.setError(INVALID);
                return;
            }

            year = parsedYear;

        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            mYearField.setError(INVALID);
            return;
        }

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(GearFormActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, notes, manufacturer, year, mDownloadUrl.getEncodedPath(), 0);
                        }

                        // Finish this Activity, back to the stream
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String notes, String manufacturer, int year, String imageUrl, int starCount) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

       String key = mDatabase.child("gear").push().getKey();
        GearData gearData = new GearData(userId, title, manufacturer, year, notes, imageUrl, starCount );
        Map<String, Object> gearValues = gearData.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/gear/" + key, gearValues);
        childUpdates.put("/user-gear/" + userId + "/" + key, gearValues);

        mDatabase.updateChildren(childUpdates);

    }
    // [END write_fan_out]

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child("photos")
                .child(fileUri.getLastPathSegment());
        // [END get_child_ref]

        // Upload file to Firebase Storage
        // [START_EXCLUDE]
        showProgressDialog();
        // [END_EXCLUDE]
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");
                        

                        // Get the public download URL
                        mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();

                        new DownloadImageTask(mImageView).execute(mDownloadUrl.toString());

                        // [START_EXCLUDE]
                        hideProgressDialog();
                     //   updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        mDownloadUrl = null;

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        Toast.makeText(GearFormActivity.this, "Error: upload failed",
                                Toast.LENGTH_SHORT).show();
                     //   updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                });
    }


}
