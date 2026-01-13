# Maneuver Feature Implementation Complete

## Summary
Added a Maneuver system to the character sheet that allows each Ghostbuster to have 1 Maneuver in addition to their Actions. The Maneuver cannot be removed due to slime and is tracked independently.

## Changes Made

### 1. Database Schema Update
**File**: `CharacterEntity.kt`
- Added `maneuverUsed: Boolean = false` field to track whether the maneuver has been used
- Database version incremented from 2 to 3 in `AppDatabase.kt`
- Uses `.fallbackToDestructiveMigration()` so existing databases will be recreated

### 2. ViewModel Updates
**File**: `CharacterSheetViewModel.kt`
- Added `toggleManeuver()` function to toggle the maneuver state
- Added `isManeuverUsed()` function to check if the maneuver has been used
- These functions follow the same pattern as action toggling

### 3. UI Components
**File**: `TokenComponents.kt`
- Updated `ActionSlimeTokens` composable to include maneuver UI
- Added two new optional parameters:
  - `maneuverUsed: Boolean = false`
  - `onManeuverToggle: () -> Unit = {}`
- Added a new "Maneuver" section in the UI that displays:
  - Label: "Maneuver" 
  - Icon: Arrow emoji (➡️) when unused, checkmark in character color when used
  - Tappable to toggle between used/unused states
- Positioned between the Actions section and Slime controls

### 4. Screen Integration
**File**: `CharacterSheetScreen.kt`
- Updated `ActionSlimeTokens` call to pass:
  - `maneuverUsed = char.maneuverUsed`
  - `onManeuverToggle = { viewModel.toggleManeuver() }`

## Behavior

### Maneuver Token
- Each character has exactly 1 Maneuver
- Shows as ➡️ when unused (arrow emoji)
- Shows as colored checkmark when used (in character's proton stream color)
- Tappable to toggle between states
- **Cannot be affected by slime** (slime only affects Actions)
- Independent from Actions system

### Visual Layout
```
Actions
⚡ ⚡ [⚡]  (Lightning bolts / checkmarks)

Maneuver
➡️ or ✓   (Arrow or checkmark)

[🧪 Slime] [De-Slime]
```

## Technical Details

### State Management
- Maneuver state stored as boolean in `CharacterEntity`
- Reactive updates through Flow/StateFlow
- Updates persist to database via Room

### Code Quality
- Used dependency injection pattern for state composition
- Helper functions maintain encapsulation
- No code duplication
- Follows existing patterns in the codebase

## Testing Notes
When testing, verify:
1. ✅ Maneuver toggles correctly when tapped
2. ✅ Maneuver state persists across app restarts
3. ✅ Maneuver is NOT affected by slime
4. ✅ Visual appearance shows character color when used
5. ✅ Database migration handles the new field (existing games will be reset)

## Future Enhancements
- Character abilities that interact with Maneuvers can use `isManeuverUsed()` 
- End of turn reset functionality can include maneuver reset
- Statistics tracking for maneuver usage

---
**Implementation Date**: January 13, 2026
**Database Version**: 3
**Status**: ✅ Complete and Ready for Testing

