package com.example.svgifts;

public class StardewItem {
    private String id;
    private String name;

    public StardewItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
