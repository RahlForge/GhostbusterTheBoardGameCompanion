# Building the Ghostbusters Companion App

## Issue: Missing Gradle Wrapper

The project is currently missing the `gradlew` executable and `gradle-wrapper.jar` file. Here's how to fix and build:

## Solution 1: Build from Android Studio (Recommended)

1. **Open Android Studio**
2. **Sync Gradle**: Click "Sync Project with Gradle Files" button (elephant icon with sync arrows)
3. **Build**: 
   - Menu: `Build > Make Project` (Ctrl+F9)
   - Or: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
4. **Run**: Click the green play button or `Run > Run 'app'`

## Solution 2: Regenerate Gradle Wrapper

From terminal in project root:

```powershell
# If you have gradle installed globally
gradle wrapper --gradle-version 8.13

# This will create:
# - gradlew (Unix/Mac)
# - gradlew.bat (Windows) ✅ Created
# - gradle/wrapper/gradle-wrapper.jar (needed)
```

## Solution 3: Use Android Studio Terminal

1. Open Android Studio
2. Open Terminal within Android Studio (Alt+F12)
3. Run: `gradle wrapper`
4. Then: `.\gradlew.bat assembleDebug`

## Verify the Slime Color Feature

After building, test the feature:

### Test Steps:
1. **Create a Ghostbusters (1984) Game**
   - Select "Ghostbusters" as game type
   - Add characters
   - Go to any character sheet
   - Toggle Action tokens to Slime
   - **Expected**: Slime appears as **green** 🟢

2. **Create a Ghostbusters II (1989) Game**
   - Select "Ghostbusters II" as game type
   - Add characters
   - Go to any character sheet
   - Toggle Action tokens to Slime
   - **Expected**: Slime appears as **pink/hot pink** 💗

### What to Look For:
- Lightning bolt (⚡) icon = Action available
- Filled colored circle = Slime token
- Color should match game type (green for GB1, pink for GB2)

## Files Modified

All slime color changes are complete in these files:
- ✅ `app/src/main/java/com/ghostbusters/companion/ui/theme/Color.kt`
- ✅ `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/CharacterSheetViewModel.kt`
- ✅ `app/src/main/java/com/ghostbusters/companion/ui/components/TokenComponents.kt`
- ✅ `app/src/main/java/com/ghostbusters/companion/ui/screens/CharacterSheetScreen.kt`

## Current Status

✅ **Code Changes**: Complete
✅ **UTF-8 Encoding**: Fixed (BOM removed)
✅ **gradlew.bat**: Created
⏳ **gradle-wrapper.jar**: Needs regeneration (use Android Studio or gradle wrapper command)
⏳ **Build Verification**: Pending (build from IDE)

## Quick Start

**Easiest method**: Just open Android Studio and click the green "Run" button! The IDE will handle the build automatically.

---

**Note**: The slime color feature is fully implemented and ready. The gradle wrapper issue is a build system configuration issue, not related to the feature code.

