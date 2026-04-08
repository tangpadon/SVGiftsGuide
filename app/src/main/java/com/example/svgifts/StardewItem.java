package com.example.svgifts;

public class StardewItem {
    private String id;
    private String name;
    private String imageBase64;

    public StardewItem(String id, String name, String imageBase64) {
        this.id = id;
        this.name = name;
        this.imageBase64 = imageBase64;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageBase64() { return imageBase64; }

    @Override
    public String toString() {
        return name;
    }
}
