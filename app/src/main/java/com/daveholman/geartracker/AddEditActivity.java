package com.daveholman.geartracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.daveholman.geartracker.data.GearData;
import com.daveholman.geartracker.data.UserGearData;
import com.daveholman.geartracker.utilities.DateUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddEditActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Test code to add a new record
        UserGearData userGearData = new UserGearData();
        userGearData.setUpdateDate(DateUtility.GetUTCdatetimeAsString());
        ArrayList<GearData> gearDatas = new ArrayList<GearData>();

        GearData gearData1 = new GearData();
        gearData1.setDescription("Cool guitar");
        gearData1.setImageUrl("fsdfsdfsdf");
        gearData1.setItemId("1");
        gearData1.setManufacturer("Carlos");
        gearData1.setName("Original Acoustic");
        gearData1.setYear(1979);
        gearDatas.add(gearData1);

        GearData gearData2 = new GearData();
        gearData2.setDescription("Neat guitar");
        gearData2.setImageUrl("aasfwefwsd");
        gearData2.setItemId("2");
        gearData2.setManufacturer("Fender");
        gearData2.setName("Strat");
        gearData2.setYear(1989);
        gearDatas.add(gearData2);

        userGearData.setGearDataList(gearDatas);
        mDatabase.child("gear").child(mUser.getUid()).setValue(userGearData);

    }

}
