# Portal

A privacy-first bookmark launcher for Android. Portal gives you a fullscreen tile grid that opens websites directly in your browser — no tracking, no cloud, no accounts required.

---

## What it does

The idea is simple: replace bloated social media apps with clean icon tiles. Tap a tile, your browser opens. That's it.

The grid is fully customizable. Tiles can be moved, resized, and colored freely. Each page has its own layout preset, and the bottom bar buttons can be given custom icons and colors too. Everything stays on your device.

---

## Features

**Grid and tiles**

Each page is a grid of tiles. You can drag to move or resize them, lock the layout to prevent accidental changes, and switch between a standard (10×18) or dense (12×24) grid per page. Tile spacing and screen margins are all adjustable. Vertical scroll mode lets tiles extend past the screen height if you need more space.

Tiles support icons from the bundled Simple Icons library (thousands of SVG logos), your gallery, or plain text. Each tile has its own color, icon zoom, and optional dark mode inversion.

**Navigation**

Multiple pages can be swiped horizontally. Page order can be rearranged in settings. Loop mode connects the last page back to the first so you can swipe through continuously.

The bottom bar has five buttons, each with a configurable color and icon. The bar can always be visible, collapse with a swipe, or hide completely and reappear on a gesture.

**Everything else**

App lock with a password and a one-time recovery code. Optional click statistics stored locally. JSON backup and restore. Dark, light, and system theme.

---

## Install

**F-Droid** *(coming soon)*

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/com.bisc.portal)

**GitHub Releases**

Download the latest APK from the [Releases](https://github.com/BISCLab/Portal/releases) page.

---

## Build from source

Requires Android Studio Hedgehog or later, JDK 17, Android SDK 35. No API keys or secret config needed.

```bash
git clone https://github.com/BISCLab/Portal.git
cd Portal
./gradlew assembleRelease
```

---

## Privacy and permissions

Portal requests no permissions at all. When you tap a tile, Android passes the URL to your browser via a standard Intent — all network activity happens in the browser, not in this app.

No external servers are contacted. Icons are either bundled with the app or loaded from your gallery. There is no analytics, no crash reporting, no telemetry, and no Google Play Services dependency. The app works on GrapheneOS and other de-Googled devices without modification.

---

## License

GPL-3.0 — see [LICENSE](LICENSE).

---

## Developer

BISC Lab. · [bisc.lab@pm.me](mailto:bisc.lab@pm.me)

Bitcoin: `bc1qf6f60r6m6fw9tpagu2u6w440lkvgyc8862hk8c`
