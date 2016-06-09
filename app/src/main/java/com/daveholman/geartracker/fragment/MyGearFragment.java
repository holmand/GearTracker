package com.daveholman.geartracker.fragment;

import com.daveholman.geartracker.fragment.GearListFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyGearFragment extends GearListFragment {

    public MyGearFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-gear")
                .child(getUid());
    }
}
