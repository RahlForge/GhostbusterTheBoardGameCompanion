# Quick Start Guide

## Prerequisites Checklist

Before building the app, ensure you have:

- [ ] **Android Studio** (Hedgehog 2023.1.1 or later)
- [ ] **JDK 17** (bundled with Android Studio)
- [ ] **Android SDK** with API Level 34
- [ ] **Gradle 8.2+** (will be auto-downloaded)

## Step-by-Step Setup

### 1. Open the Project

```
1. Launch Android Studio
2. Click "Open"
3. Navigate to: C:\Users\rahlf\source\repos\GhostbustersTheBoardGameCompanion
4. Click "OK"
```

### 2. Wait for Gradle Sync

Android Studio will automatically:
- Download Gradle wrapper
- Download dependencies
- Index project files

This may take 2-5 minutes on first launch.

### 3. Configure Android SDK (if needed)

If you see SDK errors:
```
1. File > Project Structure
2. SDK Location tab
3. Set Android SDK Location
4. Click "Apply"
```

### 4. Create an Emulator (if you don't have one)

```
1. Tools > Device Manager
2. Click "Create Device"
3. Select "Pixel 6" or similar
4. Choose System Image: API 34 (Android 14)
5. Click "Finish"
```

### 5. Run the App

```
1. Click the green "Run" button (or Shift+F10)
2. Select your device/emulator
3. Click "OK"
4. Wait for build and installation
```

## First Time Usage

### Creating Your First Game

1. **Launch App** → You'll see an empty game list
2. **Tap the + button** → Opens Game Setup screen
3. **Enter Game Name** → e.g., "Campaign Night"
4. **Select Game Type** → Choose "Ghostbusters" or "Ghostbusters II"
5. **Select Expansions** (GB2 only) → Optional
6. **Select Characters** → Choose 1-4 Ghostbusters
7. **Tap "Create Game"** → Game is saved!

### Using a Character Sheet

1. **Tap on a game** → Opens game detail
2. **Tap on a character** → Opens character sheet
3. **Manage XP** → Tap on XP bar to adjust
4. **Use Tokens** → Tap proton streams or action icons
5. **Trap Ghosts** → Tap + button in Ghost Trap section
6. **Level Up** → Automatic! Abilities unlock as you gain XP

## Troubleshooting

### Build Errors

**Problem:** "SDK not found"
- **Solution:** Update `local.properties` with correct SDK path

**Problem:** "Gradle sync failed"
- **Solution:** File > Invalidate Caches > Invalidate and Restart

**Problem:** "Compilation errors"
- **Solution:** Build > Clean Project, then Build > Rebuild Project

### Runtime Errors

**Problem:** App crashes on launch
- **Solution:** Check logcat for errors, ensure API 26+

**Problem:** Database errors
- **Solution:** Uninstall app and reinstall (clears DB)

## Testing Checklist

Test these features to ensure everything works:

- [ ] Create a new game (Ghostbusters)
- [ ] Create a new game (Ghostbusters II with expansions)
- [ ] Select multiple characters
- [ ] View game details
- [ ] Open character sheet
- [ ] Add XP and watch level progress
- [ ] Toggle proton stream tokens
- [ ] Toggle action/slime tokens
- [ ] Add a ghost to trap (XP should increase)
- [ ] Remove a ghost from trap (XP should stay same)
- [ ] Navigate back to game list
- [ ] Delete a game
- [ ] Close and reopen app (data should persist)

## Development Tips

### Viewing Database

Use Android Studio's App Inspection tool:
```
View > Tool Windows > App Inspection > Database Inspector
```

### Debugging

Set breakpoints in ViewModels to track state changes:
- Right-click line number → Toggle Breakpoint
- Run in Debug mode (Shift+F9)

### Live Preview

Compose Preview is available for UI components:
- Add `@Preview` annotation to composables
- View in Split or Design mode

### Hot Reload

Jetpack Compose supports hot reload:
- Make UI changes
- Press Ctrl+Shift+F10 to apply changes without full rebuild

## Key Files to Customize

| File | Purpose | Priority |
|------|---------|----------|
| `CharacterAbility.kt` | Define character-specific abilities | High |
| `Color.kt` | Customize app colors | Medium |
| `CharacterSheetScreen.kt` | Add character portraits | Medium |
| `strings.xml` | Localization | Low |

## What's Next?

After verifying the app works:

1. **Add Character Abilities** → Define specific abilities per character
2. **Add Portraits** → Place character images in drawable
3. **Campaign System** → Add campaign/scenario selection
4. **Polish UI** → Add animations and refinements

## Need Help?

Check these files for documentation:
- `README.md` → Full project documentation
- `PROJECT_SETUP_COMPLETE.md` → Complete feature list
- Code comments → Inline documentation

---

**Happy Ghostbusting! 👻⚡**

