package com.daveholman.geartracker.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class GearData
{
    public String uid;
    private String name;
    private String manufacturer;
    private String description;
    private int year;
    private String imageUrl;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public GearData() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public GearData(String uid, String name, String manufacturer) {
        this.uid = uid;
        this.name = name;
        this.manufacturer = manufacturer;
    }

    public String getUid() {
        return uid;
    }

    public void setUId(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("manufacturer", manufacturer);
        result.put("description", description);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("imageurl", imageUrl);

        return result;
    }
    // [END post_to_map]
}
