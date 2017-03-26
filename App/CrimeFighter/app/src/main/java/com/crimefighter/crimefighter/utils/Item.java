package com.crimefighter.crimefighter.utils;

/**
 * Created by anees on 3/25/2017.
 */

public class Item {
    public int id;
    public String itemName;
    public String distance;
    public String description;

    public Item(int id, String name, double distance, String desc) {
        itemName = name;
        if (distance < 0.01) {
            this.distance = "Near You";
        } else {
            this.distance = Double.toString(distance) + " MI";
        }
        this.id = id;
        this.description = desc;
    }
}
