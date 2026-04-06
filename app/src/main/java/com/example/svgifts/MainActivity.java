package com.example.svgifts;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<StardewItem> allItems = new ArrayList<>();
    private final List<NPCTaste> allNPCs = new ArrayList<>();
    private final List<String> universalLoves = new ArrayList<>();
    private final List<String> universalLikes = new ArrayList<>();
    AutoCompleteTextView autoCompleteItem;
    TextView txtLoveResults, txtLikeResults;
    Button btnCheck;
    StardewItem selectedItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. เชื่อมตัวแปร UI
        autoCompleteItem = findViewById(R.id.autoCompleteItem);
        txtLoveResults = findViewById(R.id.txtLoveResults);
        txtLikeResults = findViewById(R.id.txtLikeResults);
        btnCheck = findViewById(R.id.btnCheck);

        // 2. โหลดข้อมูล (ฟังก์ชันเดิมที่เขียนไว้)
        loadGameData();
        testSearch("66", "Amethyst");   // ลองเช็ค Amethyst
        testSearch("190", "Cauliflower");   // ลองเช็ค Cauliflower
        testSearch("330", "Clay");      // ลองเช็ค Clay (ของที่คนส่วนใหญ่เกลียด)

        // 3. ตั้งค่าการค้นหาไอเทม (AutoComplete)
        ArrayAdapter<StardewItem> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, allItems);
        autoCompleteItem.setAdapter(adapter);

        // เก็บค่าเมื่อผู้ใช้เลือกไอเทมจากรายการ
        autoCompleteItem.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = (StardewItem) parent.getItemAtPosition(position);
        });

        // 4. เมื่อกดปุ่มตรวจสอบ
        btnCheck.setOnClickListener(v -> {
            if (selectedItem != null) {
                updateUIResults(selectedItem.getId(), selectedItem.getName());
            }
        });
    }
    private void updateUIResults(String itemId, String itemName) {
        List<String> lovers = new ArrayList<>();
        List<String> likers = new ArrayList<>();

        boolean isUniversalLove = universalLoves.contains(itemId);
        boolean isUniversalLike = universalLikes.contains(itemId);

        for (NPCTaste npc : allNPCs) {
            if (npc.getLoveIDs().contains(itemId)) {
                lovers.add(npc.getNpcName());
            } else if (npc.getLikeIDs().contains(itemId)) {
                likers.add(npc.getNpcName());
            } else if (isUniversalLove) {
                lovers.add(npc.getNpcName());
            } else if (isUniversalLike) {
                likers.add(npc.getNpcName());
            }
        }

        txtLoveResults.setText(lovers.isEmpty() ? "ไม่มี" : String.join(", ", lovers));
        txtLikeResults.setText(likers.isEmpty() ? "ไม่มี" : String.join(", ", likers));
    }
    private void testSearch(String itemId, String itemName) {
        Log.d("StardewTest", "--- กำลังค้นหา: " + itemName + " (ID: " + itemId + ") ---");

        List<String> lovers = new ArrayList<>();
        List<String> likers = new ArrayList<>();

        boolean isUniversalLove = universalLoves.contains(itemId);
        boolean isUniversalLike = universalLikes.contains(itemId);

        for (NPCTaste npc : allNPCs) {
            // เช็คว่าอยู่ใน List ของที่ Love หรือไม่
            if (npc.getLoveIDs().contains(itemId)) {
                lovers.add(npc.getNpcName());
            }
            // เช็คว่าอยู่ใน List ของที่ Like หรือไม่
            else if (npc.getLikeIDs().contains(itemId)) {
                likers.add(npc.getNpcName());
            } else if (isUniversalLove) {
                lovers.add(npc.getNpcName());
            } else if (isUniversalLike) {
                likers.add(npc.getNpcName());
            }
        }

        Log.d("StardewTest", "💜 Love: " + (lovers.isEmpty() ? "ไม่มี" : String.join(", ", lovers)));
        Log.d("StardewTest", "😊 Like: " + (likers.isEmpty() ? "ไม่มี" : String.join(", ", likers)));
        Log.d("StardewTest", "------------------------------------------");
    }

    private void loadGameData() {
        try {
            // 1. อ่านและ Parse objects.json (เป็น JSONArray)
            String objectsJsonString = loadJSONFromAsset("objects.json");
            if (objectsJsonString != null) {
                JSONArray objectsArray = new JSONArray(objectsJsonString);
                for (int i = 0; i < objectsArray.length(); i++) {
                    JSONObject itemDetails = objectsArray.getJSONObject(i);
                    String id = itemDetails.getString("id");
                    
                    // ดึงชื่อภาษาอังกฤษจาก names -> data-en-US
                    JSONObject names = itemDetails.getJSONObject("names");
                    String name = names.getString("data-en-US");

                    // สร้าง Object และเก็บลง List
                    allItems.add(new StardewItem(id, name));
                }
            }

            // 2. อ่านและ Parse NPCGiftTastes.json
            String npcJsonString = loadJSONFromAsset("NPCGiftTastes.json");
            if (npcJsonString != null) {
                JSONObject npcRoot = new JSONObject(npcJsonString);
                JSONObject content = npcRoot.getJSONObject("content");

                // โหลด Universal Love/Like
                if (content.has("Universal_Love")) {
                    String[] ids = content.getString("Universal_Love").split(" ");
                    for (String id : ids) {
                        if (!id.trim().isEmpty()) universalLoves.add(id.trim());
                    }
                }
                if (content.has("Universal_Like")) {
                    String[] ids = content.getString("Universal_Like").split(" ");
                    for (String id : ids) {
                        if (!id.trim().isEmpty()) universalLikes.add(id.trim());
                    }
                }

                Iterator<String> npcKeys = content.keys();
                while (npcKeys.hasNext()) {
                    String npcName = npcKeys.next();
                    // ข้ามค่า Universal ต่างๆ ไปก่อน
                    if (npcName.startsWith("Universal_")) continue;

                    String rawData = content.getString(npcName);

                    // ใช้ Class NPCTaste ที่เราสร้างไว้จัดการข้อมูล
                    allNPCs.add(new NPCTaste(npcName, rawData));
                }
            }

            Log.d("StardewTest", "โหลดไอเทมได้: " + allItems.size() + " ชิ้น");
            Log.d("StardewTest", "โหลด NPC ได้: " + allNPCs.size() + " คน");

        } catch (Exception e) {
            Log.e("StardewTest", "เกิดข้อผิดพลาดในการอ่านไฟล์: " + e.getMessage(), e);
        }
    }

    // ฟังก์ชันช่วยอ่านไฟล์จากโฟลเดอร์ assets
    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e("StardewTest", "Error reading asset: " + fileName, ex);
            return null;
        }
        return json;
    }

}
