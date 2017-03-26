package com.crimefighter.crimefighter.utils;

/**
 * Created by anees on 3/25/2017.
 */

public class Item {
    String itemName;
    String distance;

    public Item(String name, double distance) {
        itemName = name;
        if (distance < 0.01) {
            this.distance = "Near you";
        } else {
            this.distance = Double.toString(distance);
        }
    }
}
