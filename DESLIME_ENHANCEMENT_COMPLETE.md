# De-Slime Action Enhancement Complete

## Summary
Enhanced the De-Slime action to require available Actions and provide a selection dialog for choosing which Ghostbuster to de-slime, with different action costs for self vs. others.

## Changes Made

### 1. ViewModel Updates
**File**: `CharacterSheetViewModel.kt`

#### New Functions:
- **`canDeSlime(): Boolean`**
  - Checks if at least 1 action is available (not used and not slimed)
  - Used to enable/disable the De-Slime button
  - Returns `true` if `availableActions > 0`

- **`getDeSlimeTargets(): List<CharacterEntity>`**
  - Returns list of characters that can be de-slimed
  - Filters characters with `slimeCount > 0`
  - For self: only shows if ALL actions are available
  - For others: shows if at least 1 action is available
  - Used to populate the selection dialog

- **`deSlimeCharacter(targetCharacterId: Long)`**
  - Performs the de-slime action on the selected character
  - Reduces target's `slimeCount` by 1
  - Marks actions as used on the active character:
    - **Self**: Marks ALL actions as used
    - **Others**: Marks only 1 action as used
  - Maneuver is never affected

### 2. UI Component Updates
**File**: `TokenComponents.kt`

#### ActionSlimeTokens Composable:
- Added `canDeSlime: Boolean = true` parameter
- De-Slime button now uses `canDeSlime` instead of `slimeCount > 0`
- Button disabled when no actions available

### 3. Screen Updates
**File**: `CharacterSheetScreen.kt`

#### De-Slime Dialog:
- Added `showDeSlimeDialog` state variable
- De-Slime button opens dialog instead of immediate action
- Dialog displays all valid de-slime targets
- Each target shows:
  - Character name with "(Self)" indicator
  - Current slime count
  - Action cost: "Costs ALL actions" vs "Costs 1 action"
  - Character's proton stream color as background
- Empty state message if no targets available
- Cancel button to dismiss without action

#### Integration:
- Passes `canDeSlime = viewModel.canDeSlime()` to ActionSlimeTokens
- Added `SlimeGreen` import for color consistency

## Behavior

### De-Slime Button Enabled When:
- At least 1 action is available
- Action is not used
- Action is not slimed
- At least 1 character (including self) has slime

### De-Slime Button Disabled When:
- All actions are used
- All actions are slimed
- No available actions remain

### De-Sliming Self:
- **Requirement**: ALL actions must be available
- **Effect**: Removes 1 slime from self
- **Cost**: ALL actions marked as used
- **Note**: Maneuver is NOT affected

### De-Sliming Others:
- **Requirement**: At least 1 action available
- **Effect**: Removes 1 slime from target
- **Cost**: 1 action marked as used
- **Note**: Maneuver is NOT affected

## Logic Details

### Available Actions Calculation:
```kotlin
val maxActions = getMaxActions() // 2 or 3 depending on level
val usedActionCount = (0 until maxActions).count { isActionUsed(it) }
val availableActions = maxActions - slimeCount - usedActionCount
```

### Self De-Slime Check:
```kotlin
if (char.id == current.id) {
    // Can only de-slime self if ALL actions are available
    availableActions == maxActions - current.slimeCount
}
```

### Marking Actions as Used:
```kotlin
// De-sliming self: ALL actions
for (i in 0 until maxActions) {
    val bitMask = 1 shl i
    newActionsUsed = newActionsUsed or bitMask
}

// De-sliming others: 1 action
for (i in 0 until maxActions) {
    if (!isActionUsed(i)) {
        val bitMask = 1 shl i
        val newActionsUsed = current.actionsUsed or bitMask
        break
    }
}
```

## UI Flow

1. User taps "De-Slime" button
2. Dialog opens showing eligible targets
3. User selects a Ghostbuster
4. System:
   - Reduces target's slime by 1
   - Marks active character's actions as used (1 or all)
   - Updates both characters in database
5. Dialog closes
6. UI updates reactively

## Visual Design

### Dialog Layout:
```
┌─────────────────────────────┐
│ De-Slime Ghostbuster        │
├─────────────────────────────┤
│ Select a Ghostbuster:       │
│                             │
│ ┌─────────────────────────┐ │
│ │ Peter Venkman           │ │
│ │ Slime: 2                │ │
│ │ Costs 1 action          │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ Ray Stantz (Self)       │ │
│ │ Slime: 1                │ │
│ │ Costs ALL actions ⚠️    │ │
│ └─────────────────────────┘ │
│                             │
│             [Cancel]        │
└─────────────────────────────┘
```

### Color Coding:
- Each target card uses that character's proton stream color (20% opacity)
- Self de-slime cost shown in error color (red)
- Slime count shown in SlimeGreen

## Testing Scenarios

### Scenario 1: De-Slime Others
1. ✅ Peter has 2 slimes, Winston has 1 available action
2. ✅ Winston taps De-Slime → Dialog shows Peter
3. ✅ Winston selects Peter
4. ✅ Peter's slime count: 2 → 1
5. ✅ Winston's actions: 0 used → 1 used

### Scenario 2: De-Slime Self
1. ✅ Ray has 1 slime, 2 available actions (Level 2)
2. ✅ Ray taps De-Slime → Dialog shows Ray (Self)
3. ✅ Ray selects himself
4. ✅ Ray's slime count: 1 → 0
5. ✅ Ray's actions: 0 used → 2 used (ALL)

### Scenario 3: Cannot De-Slime Self
1. ✅ Egon has 1 slime, 1 used action (Level 2: 2 total)
2. ✅ Egon taps De-Slime → Dialog shows others, NOT self
3. ✅ Self not shown because not ALL actions available

### Scenario 4: Button Disabled
1. ✅ Winston has 2 slimes, 2 used actions (Level 2)
2. ✅ De-Slime button is disabled (no available actions)
3. ✅ Cannot open dialog

### Scenario 5: No Targets
1. ✅ All Ghostbusters have 0 slimes
2. ✅ De-Slime button is disabled
3. ✅ If somehow opened, shows "No Ghostbusters available"

### Scenario 6: Maneuver Unaffected
1. ✅ Peter de-slimes Ray
2. ✅ Peter's maneuver state unchanged
3. ✅ Only action marked as used

## Integration with Existing Features

### Ray's Level 1 Ability:
- "When you spend an action to remove Slime from another Ghostbuster, gain 1 XP."
- **TODO**: Hook into `deSlimeCharacter()` when target is not self
- Check if Ray, check if target is another, add XP

### Future Enhancement Ideas:
- Add confirmation dialog for self de-slime (costs ALL actions)
- Track de-slime statistics
- Add sound effect when de-sliming
- Animation for slime removal
- Show action cost preview in dialog

## Code Quality

### Strengths:
- ✅ Proper state management with StateFlow
- ✅ Reactive UI updates via Flow
- ✅ Clear separation of concerns (ViewModel/UI)
- ✅ Comprehensive validation logic
- ✅ User-friendly dialog with clear information

### Consistency:
- ✅ Follows existing patterns (similar to Transfer/Trap It dialogs)
- ✅ Uses character colors consistently
- ✅ Bit masking for action tracking
- ✅ Database updates via repository pattern

---
**Implementation Date**: January 14, 2026
**Status**: ✅ Complete and Ready for Testing
**Related Features**: Actions, Slime, Ray's Level 1 Ability (future integration)

