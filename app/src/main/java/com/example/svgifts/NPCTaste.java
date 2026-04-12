package com.example.svgifts;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class NPCTaste {
    private final String npcName;
    private final List<String> loveIDs;
    private final List<String> likeIDs;

    public NPCTaste(String name, String rawDataFromJSON) {
        this.npcName = name;
        this.loveIDs = new ArrayList<>();
        this.likeIDs = new ArrayList<>();
        appendData(rawDataFromJSON);
    }

    public void appendData(String rawDataFromJSON) {
        String[] parts = rawDataFromJSON.split("/");

        // SVE NPCs have a different structure: {{i18n:...}}/ID ID ID
        // Vanilla NPCs have: ID ID ID (Indices are different if we split by /)
        
        int loveIndex = 1;
        int likeIndex = 3;

        // Check if the first part is a tag like {{i18n:...}}
        // If it's NOT, then it might be a vanilla-style list where indices are even
        // Actually, looking at Abigail: "/Loves//Likes//Dislikes//Hates//Neutral"
        // Split by / gives: ["", "Loves", "", "Likes", "", "Dislikes", "", "Hates", "", "Neutral"]
        // Abigail Index 1 = Loves, Index 3 = Likes. This matches.

        // Append Love IDs
        if (parts.length > loveIndex) {
            String[] ids = parts[loveIndex].trim().split(" ");
            for (String id : ids) {
                if (id.isEmpty()) continue;
                String cleanId = sanitizeId(id);
                if (!cleanId.isEmpty() && !this.loveIDs.contains(cleanId)) {
                    this.loveIDs.add(cleanId);
                }
            }
        }

        // Append Like IDs
        if (parts.length > likeIndex) {
            String[] ids = parts[likeIndex].trim().split(" ");
            for (String id : ids) {
                if (id.isEmpty()) continue;
                String cleanId = sanitizeId(id);
                if (!cleanId.isEmpty() && !this.likeIDs.contains(cleanId)) {
                    this.likeIDs.add(cleanId);
                }
            }
        }
    }

    private String sanitizeId(String id) {
        return StardewItem.sanitizeId(id);
    }

    public String getNpcName() { return npcName; }
    public List<String> getLoveIDs() { return loveIDs; }
    public List<String> getLikeIDs() { return likeIDs; }

    @NonNull
    @Override
    public String toString() {
        return npcName;
    }
}
