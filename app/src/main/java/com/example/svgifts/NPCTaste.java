package com.example.svgifts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NPCTaste {
    private String npcName;
    private List<String> loveIDs;
    private List<String> likeIDs;

    public NPCTaste(String name, String rawDataFromJSON) {
        this.npcName = name;
        String[] parts = rawDataFromJSON.split("/");

        this.loveIDs = new ArrayList<>();
        this.likeIDs = new ArrayList<>();

        // แก้ไขการดึง Love IDs (Index 1)
        if (parts.length > 1) {
            String[] ids = parts[1].split(" ");
            for (String id : ids) {
                if (!id.trim().isEmpty()) { // เช็คว่าไม่ใช่ค่าว่าง
                    this.loveIDs.add(id.trim());
                }
            }
        }

        // แก้ไขการดึง Like IDs (Index 3)
        if (parts.length > 3) {
            String[] ids = parts[3].split(" ");
            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    this.likeIDs.add(id.trim());
                }
            }
        }
    }

    public String getNpcName() { return npcName; }
    public List<String> getLoveIDs() { return loveIDs; }
    public List<String> getLikeIDs() { return likeIDs; }
}