# Database Migration Required

## Important: App Data Will Be Reset

The Actions and Slime System redesign requires a database schema change. When you next run the app, all existing game data will be cleared due to the `fallbackToDestructiveMigration()` strategy.

## What Changed
- Added `slimeCount: Int = 0` column to `CharacterEntity`
- Database version incremented from 1 to 2

## Steps to Apply Changes

### Option 1: Uninstall and Reinstall (Recommended)
1. Uninstall the app from your device/emulator
2. Clean the project: `.\gradlew.bat clean`
3. Rebuild and install: `.\gradlew.bat :app:installDebug`

### Option 2: Just Rebuild
1. The app will automatically recreate the database on first launch
2. Any existing game data will be lost
3. Simply run the app - it will handle the migration

## Verification
After rebuilding and running the app:
1. Create a new game
2. Select Peter Venkman as a character
3. Gain enough XP to reach Level 1 (should start at Level 1)
4. Tap his Level 1 ability
5. Verify that:
   - A slime is added (green indicator appears on rightmost action)
   - 1 XP is granted
   - The slime counter shows "1 slime"
   - The action with slime is disabled (grayed out)

## Troubleshooting

### Slime Not Being Added
- Make sure the app was fully rebuilt (not just hot-reloaded)
- Check that database version is 2 in AppDatabase.kt
- Try uninstalling the app completely and reinstalling

### App Crashes on Startup
- Clear app data: Settings > Apps > Ghostbusters Companion > Clear Data
- Or uninstall and reinstall the app

### Build Errors
- Run: `.\gradlew.bat clean`
- Delete the `.gradle` folder in project root
- Rebuild: `.\gradlew.bat :app:assembleDebug`

## Bug Fix Applied

### Issue: Slime Count Not Updating in UI
**Problem**: `slimeCount` was being read via `viewModel.getSlimeCount()` which is not reactive.

**Solution**: Changed to read `char.slimeCount` directly from the reactive character state that's collected via `collectAsState()`.

**Files Fixed**:
- `CharacterSheetScreen.kt`: Now uses `char.slimeCount` instead of `viewModel.getSlimeCount()`
- Added debug logging to ViewModel to help diagnose issues

This ensures the UI automatically updates when slimeCount changes in the database.

## December 10, 2025
Database schema version 2 ready for deployment.
Bug fix applied - slimeCount now reactive.

