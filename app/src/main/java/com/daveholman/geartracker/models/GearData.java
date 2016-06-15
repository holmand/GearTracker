package com.daveholman.geartracker.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class GearData
{
    public String uid;
    private String title;
    private String manufacturer;
    private String notes;
    private int year;
    private String imageUrl;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public GearData() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public GearData(String uid, String title, String manufacturer, int year, String notes, String imageUrl, int starCount) {
        this.uid = uid;
        this.title = title;
        this.manufacturer = manufacturer;
        this.year = year;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.starCount = starCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUId(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        result.put("title", title);
        result.put("manufacturer", manufacturer);
        result.put("notes", notes);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("imageurl", imageUrl);

        return result;
    }
    // [END post_to_map]
}
