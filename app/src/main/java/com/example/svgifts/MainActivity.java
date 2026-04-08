package com.example.svgifts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
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
    private final List<String> springItemNames = new ArrayList<>();
    private final List<String> summerItemNames = new ArrayList<>();
    private final List<String> fallItemNames = new ArrayList<>();
    private final List<String> winterItemNames = new ArrayList<>();
    AutoCompleteTextView autoCompleteSearch;
    TextView txtLoveResults, txtLikeResults;
    ImageView imgDisplay;
    Button btnCheck;
    Object selectedObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. เชื่อมตัวแปร UI
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);
        txtLoveResults = findViewById(R.id.txtLoveResults);
        txtLikeResults = findViewById(R.id.txtLikeResults);
        imgDisplay = findViewById(R.id.imgDisplay);
        btnCheck = findViewById(R.id.btnCheck);

        // 2. โหลดข้อมูล (ฟังก์ชันเดิมที่เขียนไว้)
        loadGameData();

        // 3. รวมข้อมูลทั้ง Item และ NPC ลงใน List เดียวกันเพื่อทำ Search
        List<Object> combinedList = new ArrayList<>();
        combinedList.addAll(allItems);
        combinedList.addAll(allNPCs);

        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, combinedList);
        autoCompleteSearch.setAdapter(adapter);

        // เก็บค่าเมื่อผู้ใช้เลือก
        autoCompleteSearch.setOnItemClickListener((parent, view, position, id) -> {
            selectedObject = parent.getItemAtPosition(position);
        });

        // ล้างข้อความเมื่อกดที่ช่องค้นหา
        autoCompleteSearch.setOnClickListener(v -> {
            autoCompleteSearch.setText("");
            selectedObject = null;
        });

        // 4. เมื่อกดปุ่มตรวจสอบ
        btnCheck.setOnClickListener(v -> {
            if (selectedObject instanceof StardewItem) {
                StardewItem item = (StardewItem) selectedObject;
                updateUIResults(item.getId(), item.getName());
                displayImage("items/" + item.getId() + ".png", item.getImageBase64());
            } else if (selectedObject instanceof NPCTaste) {
                NPCTaste npc = (NPCTaste) selectedObject;
                updateNPCUIResults(npc);
                displayImage("portraits/" + npc.getNpcName() + ".png", null);
            }
        });
    }

    private void displayImage(String path, String base64Data) {
        try {
            if (base64Data != null && !base64Data.isEmpty()) {
                byte[] decodedString = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgDisplay.setImageBitmap(decodedByte);
                imgDisplay.setVisibility(android.view.View.VISIBLE);
                return;
            }

            InputStream is = getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            if (bitmap != null) {
                // Crop to 64x64 if it's a portrait (sprite sheet)
                if (path.startsWith("portraits/") && bitmap.getWidth() >= 64 && bitmap.getHeight() >= 64) {
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, 64, 64);
                }
                imgDisplay.setImageBitmap(bitmap);
                imgDisplay.setVisibility(android.view.View.VISIBLE);
            } else {
                imgDisplay.setVisibility(android.view.View.GONE);
            }
        } catch (IOException e) {
            imgDisplay.setVisibility(android.view.View.GONE);
            Log.w("StardewTest", "Image not found: " + path);
        }
    }

    private void updateNPCUIResults(NPCTaste npc) {
        List<String> loveIds = new ArrayList<>();
        List<String> likeIds = new ArrayList<>();

        // รวม Universal เข้าไปก่อน
        loveIds.addAll(universalLoves);
        likeIds.addAll(universalLikes);

        // เพิ่มของเฉพาะตัว NPC
        for (String id : npc.getLoveIDs()) {
            if (!loveIds.contains(id)) loveIds.add(id);
        }
        for (String id : npc.getLikeIDs()) {
            if (!likeIds.contains(id)) likeIds.add(id);
        }

        txtLoveResults.setText(formatSeasonalItemList(loveIds));
        txtLikeResults.setText(formatSeasonalItemList(likeIds));
    }

    private CharSequence formatSeasonalItemList(List<String> itemIds) {
        if (itemIds.isEmpty()) return "ไม่มี";

        List<String> springIds = new ArrayList<>();
        List<String> summerIds = new ArrayList<>();
        List<String> fallIds = new ArrayList<>();
        List<String> winterIds = new ArrayList<>();
        List<String> otherIds = new ArrayList<>();

        for (String id : itemIds) {
            StardewItem item = getItemById(id);
            if (item != null) {
                String name = item.getName();
                boolean inSpring = springItemNames.contains(name);
                boolean inSummer = summerItemNames.contains(name);
                boolean inFall = fallItemNames.contains(name);
                boolean inWinter = winterItemNames.contains(name);

                if (inSpring && inSummer && inFall && inWinter) {
                    otherIds.add(id);
                } else {
                    boolean categorized = false;
                    if (inSpring) {
                        springIds.add(id);
                        categorized = true;
                    }
                    if (inSummer) {
                        summerIds.add(id);
                        categorized = true;
                    }
                    if (inFall) {
                        fallIds.add(id);
                        categorized = true;
                    }
                    if (inWinter) {
                        winterIds.add(id);
                        categorized = true;
                    }

                    if (!categorized) {
                        otherIds.add(id);
                    }
                }
            }
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        
        // Spring Section
        if (!springIds.isEmpty()) {
            appendSeasonalSection(ssb, "Spring Available:", 0xFFE91E63, springIds);
        }

        // Summer Section
        if (!summerIds.isEmpty()) {
            if (ssb.length() > 0) ssb.append("\n\n");
            appendSeasonalSection(ssb, "Summer Available:", 0xFFFF9800, summerIds);
        }

        // Fall Section
        if (!fallIds.isEmpty()) {
            if (ssb.length() > 0) ssb.append("\n\n");
            appendSeasonalSection(ssb, "Fall Available:", 0xFF795548, fallIds);
        }

        // Winter Section
        if (!winterIds.isEmpty()) {
            if (ssb.length() > 0) ssb.append("\n\n");
            appendSeasonalSection(ssb, "Winter Available:", 0xFF2196F3, winterIds);
        }

        // Other Section
        if (!otherIds.isEmpty()) {
            if (ssb.length() > 0) ssb.append("\n\n");
            int start = ssb.length();
            ssb.append("Other:\n");
            ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(formatItemList(otherIds));
        }

        return ssb;
    }

    private void appendSeasonalSection(SpannableStringBuilder ssb, String title, int color, List<String> ids) {
        int start = ssb.length();
        ssb.append(title).append("\n");
        ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new android.text.style.ForegroundColorSpan(color), start, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(formatItemList(ids));
    }

    private CharSequence formatItemList(List<String> itemIds) {
        if (itemIds.isEmpty()) return "ไม่มี";
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int size = (int) (txtLoveResults.getTextSize() * 1.1);

        for (int i = 0; i < itemIds.size(); i++) {
            String id = itemIds.get(i);
            StardewItem item = getItemById(id);
            if (item == null) continue;

            int start = ssb.length();
            ssb.append(" "); // Placeholder for icon
            Drawable d = getItemDrawable(item);
            if (d != null) {
                d.setBounds(0, 0, size, size);
                ssb.setSpan(new ImageSpan(d, ImageSpan.ALIGN_BASELINE), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ssb.append("\u00A0"); // Non-breaking space to glue icon to text
            ssb.append(item.getName());

            if (i < itemIds.size() - 1) {
                ssb.append("\t"); // Normal spaces here to allow wrap between items
            }
        }
        return ssb;
    }

    private CharSequence formatNpcList(List<String> npcNames) {
        if (npcNames.isEmpty()) return "ไม่มี";
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int size = (int) (txtLoveResults.getTextSize() * 1.1);

        for (int i = 0; i < npcNames.size(); i++) {
            String name = npcNames.get(i);
            int start = ssb.length();
            ssb.append(" "); // Placeholder for icon
            Drawable d = getPortraitDrawable(name);
            if (d != null) {
                d.setBounds(0, 0, size, size);
                ssb.setSpan(new ImageSpan(d, ImageSpan.ALIGN_BASELINE), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ssb.append("\u00A0"); // Non-breaking space to glue icon to text
            ssb.append(name);

            if (i < npcNames.size() - 1) {
                ssb.append("\t"); // Normal spaces here to allow wrap between items
            }
        }
        return ssb;
    }

    private Drawable getPortraitDrawable(String npcName) {
        try {
            InputStream is = getAssets().open("portraits/" + npcName + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            if (bitmap != null && bitmap.getWidth() >= 64 && bitmap.getHeight() >= 64) {
                // Crop the first 64x64 frame for the small icons in the list too
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, 64, 64);
                return new BitmapDrawable(getResources(), bitmap);
            }
            return new BitmapDrawable(getResources(), bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    private Drawable getItemDrawable(StardewItem item) {
        if (item.getImageBase64() != null && !item.getImageBase64().isEmpty()) {
            byte[] decodedString = android.util.Base64.decode(item.getImageBase64(), android.util.Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return new BitmapDrawable(getResources(), decodedByte);
        }
        return null;
    }

    private StardewItem getItemById(String id) {
        for (StardewItem item : allItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private String getItemNameById(String id) {
        StardewItem item = getItemById(id);
        return (item != null) ? item.getName() : null;
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

        txtLoveResults.setText(formatNpcList(lovers));
        txtLikeResults.setText(formatNpcList(likers));
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
                    String imageBase64 = itemDetails.optString("image", null);
                    
                    // ดึงชื่อภาษาอังกฤษจาก names -> data-en-US
                    JSONObject names = itemDetails.getJSONObject("names");
                    String name = names.getString("data-en-US");

                    // สร้าง Object และเก็บลง List
                    allItems.add(new StardewItem(id, name, imageBase64));

                    // โหลดข้อมูลฤดูกาลจาก JSON
                    JSONArray seasons = itemDetails.optJSONArray("seasons");
                    if (seasons != null) {
                        for (int j = 0; j < seasons.length(); j++) {
                            String season = seasons.getString(j);
                            if (season.equals("spring")) springItemNames.add(name);
                            else if (season.equals("summer")) summerItemNames.add(name);
                            else if (season.equals("fall")) fallItemNames.add(name);
                            else if (season.equals("winter")) winterItemNames.add(name);
                        }
                    }
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
