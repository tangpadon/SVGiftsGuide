package com.example.svgifts;

import androidx.annotation.NonNull;

public class StardewItem {
    private final String id;
    private final String name;
    private final String imageBase64;
    private final String categoryId;

    public StardewItem(String id, String name, String imageBase64, String categoryId) {
        this.id = id;
        this.name = name;
        this.imageBase64 = imageBase64;
        this.categoryId = categoryId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageBase64() { return imageBase64; }
    public String getCategoryId() { return categoryId; }

    public String getCleanId() {
        return sanitizeId(id);
    }

    public static String sanitizeId(String id) {
        if (id == null) return "";
        String clean = id;
        
        // 1. Remove prefixes (Stardew 1.6+)
        if (clean.startsWith("(") && clean.contains(")")) {
            int closeParen = clean.indexOf(")");
            if (closeParen < 5) { // Sanity check for prefix length
                clean = clean.substring(closeParen + 1);
            }
        }

        // 2. Remove SMAPI/SVE prefixes
        clean = clean.replace("FlashShifter.StardewValleyExpandedCP_", "");
        clean = clean.replace("FlashShifter.StardewValleyExpanded_", ""); // Alternative prefix
        
        // 3. Normalize underscores to spaces and trim
        return clean.replace("_", " ").trim();
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
