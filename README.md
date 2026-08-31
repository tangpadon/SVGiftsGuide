<div align="center">

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
<img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
<img src="https://img.shields.io/badge/Data-JSON-FFD43B?style=for-the-badge&logo=json&logoColor=black" alt="JSON">
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License">

---

<h1>Stardew Valley Gift Guide</h1>

<p>
A lightweight Android companion app for Stardew Valley players.<br>
Quick reference guide to help you identify <strong>Loved</strong> and <strong>Liked</strong> gifts for every NPC,<br>
ensuring you never miss a chance to build friendships in Pelican Town.
</p>

</div>

---

## Introduction

Stardew Valley Gift Guide is a quick-reference Android app designed for players who want to maximize friendship levels without memorizing complex gift preferences. Simply select an NPC or search an item, and the app instantly tells you the best gifts to give.

Built with clean Java and structured around extracted game data, this app works offline and loads instantly.

---

## Data Integration

This project uses a hybrid data approach for maximum accuracy:

<div align="center">
<table>
<tr>
<td align="center">
<strong>NPC Gift Tastes</strong><br>
Extracted directly from<br>original game files<br>
<sub>NPCGiftTastes.xnb decoded to JSON</sub>
</td>
<td align="center" style="font-size:1.5rem;">+</td>
<td align="center">
<strong>Item Database</strong><br>
Mapped using<br>MateusAquino/stardewids<br>
<sub>Consistent Object IDs and metadata</sub>
</td>
</tr>
</table>
</div>

---

## Features

| Feature | Status | Description |
|---------|--------|-------------|
| **NPC Directory** | Complete | Comprehensive list of all villagers |
| **Gift Lookup** | Complete | Select an NPC to see their loved/liked gifts |
| **Item-to-NPC Mapping** | Complete | Search an item to find who loves/likes it |
| **Reverse Lookup** | Complete | Search your inventory to find who to give items to |
| **SVE Mod Support** | Complete | Supports Stardew Valley Expanded NPCs |
| **Data Parsing** | Complete | Java-based parser for complex gift taste strings |
| **UI/UX** | In Progress | Material Design interface under development |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| Platform | Android |
| Data Format | JSON (parsed from game assets) |
| NPC Data Source | NPCGiftTastes.xnb |
| Item Database | MateusAquino/stardewids |

---

## Screenshots

<div align="center">

<em>Screenshots coming soon</em>

</div>

---

<div align="center">

<img src="https://img.shields.io/badge/Status-Active-brightgreen?style=for-the-badge" alt="Status">

<sub>Built with care for the Stardew Valley community</sub>

</div>
