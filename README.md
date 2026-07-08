# Portal

**A privacy-first web bookmark launcher for Android.**

Portal replaces bloated social media apps with a clean, fullscreen tile grid that opens websites directly in your browser — no tracking, no cloud, no accounts.

---

## Features

**Grid & Layout**
- Fullscreen tile grid inspired by Windows Phone Metro
- Multiple pages (sections) with custom names and ordering
- Per-page grid preset: 10×18 (default) or 12×24 (dense)
- Adjustable tile spacing, margins (top, bottom, left, right)
- Infinite vertical scroll mode per layout
- Horizontal page wrap (loop from last page back to first)
- Drag-to-move and resize tiles in edit mode
- Layout lock to prevent accidental changes

**Tiles**
- Open any URL in your preferred browser
- Custom icons: Simple Icons library (1000+ SVG logos), gallery image, or emoji fallback
- Icon zoom slider per tile
- Tile color: any hex color with full color wheel
- Auto-invert icons in dark mode (per tile or globally)
- Optional section header tiles (icon, icon+text, or text-only)

**Bottom Bar**
- 5 fully customizable buttons (back, settings, add, info, forward)
- Color picker per button
- Custom icon per button (same Simple Icons library as tiles)
- Collapsible, always-visible, or fully hidden bar with gesture reveal
- Auto-collapse after a few seconds

**Navigation & Behavior**
- Back/forward arrows with optional dim at page ends
- Horizontal page wrap (arrows and swipe)
- Arrow dim toggle (arrows always full opacity option)
- App lock with password and one-time reset code

**Privacy**
- Zero permissions except `INTERNET`
- No Google Play Services, no Firebase, no analytics
- No crash reporting, no telemetry
- Works on GrapheneOS and other de-Googled devices
- All data stays on-device (Room database + DataStore)

**Other**
- Dark / Light / System theme
- Statistics: optional click tracking with hourly chart (never leaves device)
- Backup & restore (JSON export/import)
- Bitcoin donation support (optional, in-app address display)

---

## Install

### F-Droid *(coming soon)*
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/com.bisc.portal)

### GitHub Releases
Download the latest APK from the [Releases](https://github.com/bisclab/portal/releases) page.

---

## Build from source

Requirements: Android Studio Hedgehog or later, JDK 17, Android SDK 35.

```bash
git clone https://github.com/bisclab/portal.git
cd portal
./gradlew assembleRelease
```

The debug build can be installed directly:
```bash
./gradlew installDebug
```

No API keys, no secret config files required. The project builds as-is.

---

## Permissions

| Permission | Reason |
|------------|--------|
| `INTERNET` | Opens URLs in the browser |

That is the complete list.

---

## Privacy

Portal does not contact any server on its own. Tile icons are loaded from `file:///android_asset/` (bundled) or from a URI you pick from your gallery. No favicon fetching, no external requests made by the app itself. When you tap a tile, your browser opens the URL — network activity from that point is your browser's.

Statistics (click counts) are stored locally in the app's private database and never leave the device.

---

## License

GPL-3.0 — see [LICENSE](LICENSE).

---

## Developer

BISC Lab. · [bisc.lab@pm.me](mailto:bisc.lab@pm.me)

Bitcoin donations: `bc1qf6f60r6m6fw9tpagu2u6w440lkvgyc8862hk8c`
