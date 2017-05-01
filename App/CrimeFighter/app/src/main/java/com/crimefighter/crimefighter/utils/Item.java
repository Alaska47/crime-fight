package com.crimefighter.crimefighter.utils;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * Created by anees on 3/25/2017.
 */

public class Item {
    public int id;
    public String itemName;
    public String distance;
    public String description;
    public Location dd;

    public Item(int id, String name, double distance, String desc, Location d) {
        itemName = name;
        if (distance < 0.05) {
            this.distance = "Near You";
        } else {
            String ddd = new DecimalFormat("#.##").format(distance);
            this.distance = ddd + " MI";
        }
        this.id = id;
        this.description = desc;
        this.dd = d;
    }
}
