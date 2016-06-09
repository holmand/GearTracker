package com.daveholman.geartracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daveholman.geartracker.models.GearData;
import com.daveholman.geartracker.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class GearFormActivity extends BaseActivity {

    private static final String TAG = "GearFormActivity";
    private static final String REQUIRED = "Required";
    private static final String INVALID = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mManufacturerField;
    private EditText mYearField;
    private EditText mNotesField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_form);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mTitleField = (EditText) findViewById(R.id.field_title);
        mNotesField = (EditText) findViewById(R.id.field_notes);
        mManufacturerField = (EditText) findViewById(R.id.field_manufacturer);
        mYearField = (EditText) findViewById(R.id.field_year);

        findViewById(R.id.fab_submit_gear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGear();
            }
        });
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
                            writeNewPost(userId, user.username, title, notes, manufacturer, year);
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
    private void writeNewPost(String userId, String username, String title, String notes, String manufacturer, int year) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

       String key = mDatabase.child("gear").push().getKey();
        GearData gearData = new GearData(userId, title, manufacturer, year );
        Map<String, Object> gearValues = gearData.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/gear/" + key, gearValues);
        childUpdates.put("/user-gear/" + userId + "/" + key, gearValues);

        mDatabase.updateChildren(childUpdates);

    }
    // [END write_fan_out]
}
