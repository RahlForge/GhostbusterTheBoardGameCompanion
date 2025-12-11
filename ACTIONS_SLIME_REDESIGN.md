# Actions and Slime System Redesign

## Overview
Redesigned the Actions and Slime system to separate action checkboxes from slime management, providing clearer UI and more intuitive controls.

## Changes Made

### 1. UI Design Changes

#### Action Tokens (Lightning Bolt / Checkmark)
- **Unused Actions**: Display as lightning bolt emoji (⚡)
- **Used Actions**: Display as checkmark in the Ghostbuster's character color
- Actions can be toggled by tapping: lightning bolt ↔ colored checkmark
- Slimed actions show a green slime indicator overlay in the corner
- Slimed actions are disabled (cannot be toggled)
- Slimed actions appear as the rightmost actions

#### Separate Slime Controls
- **🧪 Slime Button**: Adds one slime to the rightmost available action
  - Disabled when all actions are slimed
  - Uses green color (SlimeGreen)
  
- **De-Slime Button**: Removes one slime at a time
  - Disabled when no slime present
  - Uses secondary color scheme
  
- **Slime Counter**: Shows "{n} slime(s)" below the buttons

### 2. Database Changes

#### CharacterEntity Schema Update
- Added `slimeCount: Int = 0` field to track slime separately
- Database version incremented from 1 to 2
- Using `fallbackToDestructiveMigration()` for schema update

### 3. ViewModel Updates

#### New Methods
- `addSlime()`: Adds one slime (max = actionCount)
- `removeSlime()`: Removes one slime
- `getSlimeCount()`: Returns current slime count
- `canAddSlime()`: Checks if more slime can be added

#### Modified Logic
- Slime now tracked independently from action toggle state
- Slimed actions calculated as rightmost `slimeCount` actions
- Actions disabled when slimed (index >= actionCount - slimeCount)

### 4. Peter Venkman's Abilities Updated

#### Level 1: "When a Ghost Slimes you on your turn, gain 1 XP"
- **OLD**: Toggled first available action to slime
- **NEW**: Automatically calls `addSlime()` to add one slime when tapped
- Grants 1 XP
- **Requirement**: Not all actions are slimed (ability disabled when all actions slimed)
- **Behavior**: Tapping this ability adds one slime to the rightmost available action AND grants 1 XP

#### Level 2: "Swap with adjacent Ghostbuster and get Slimed instead"
- **OLD**: Toggled first available action to slime  
- **NEW**: Automatically calls `addSlime()` to add one slime when tapped
- **Requirement**: Not all actions are slimed (ability disabled when all actions slimed)
- **Behavior**: Tapping this ability adds one slime to the rightmost available action

### 5. Component API Changes

#### ActionSlimeTokens (TokenComponents.kt)
**OLD API:**
```kotlin
ActionSlimeTokens(
    actionCount: Int,
    usedActions: List<Boolean>,
    gameType: GameType,
    onToggle: (Int) -> Unit
)
```

**NEW API:**
```kotlin
ActionSlimeTokens(
    actionCount: Int,
    slimeCount: Int,
    actionStates: List<Boolean>,
    characterColor: Color,
    onActionToggle: (Int) -> Unit,
    onAddSlime: () -> Unit,
    onRemoveSlime: () -> Unit
)
```

### 6. CharacterSheetScreen Updates

#### Ability Tappability Logic
- Venkman's Level 1 & 2 abilities check `canAddMoreSlime`
- Other abilities still check `hasActionsAvailable`
- Abilities disabled when all actions are slimed

#### Component Usage
```kotlin
ActionSlimeTokens(
    actionCount = viewModel.getMaxActions(),
    slimeCount = viewModel.getSlimeCount(),
    characterColor = Color(char.characterName.getProtonStreamColor().hex),
    onActionToggle = { viewModel.toggleAction(it) },
    onAddSlime = { viewModel.addSlime() },
    onRemoveSlime = { viewModel.removeSlime() }
)
```

## User Experience Improvements

### Clarity
- Clear visual distinction between actions and slime
- Separate controls make the game state more obvious
- Slime counter provides immediate feedback

### Intuitive Controls
- "🧪 Slime" button explicitly adds slime
- "De-Slime" button explicitly removes slime
- No ambiguous toggle behavior

### Game Accuracy
- Better reflects board game mechanics
- Slime disables actions without removing them
- One-at-a-time slime removal matches physical gameplay

## Technical Notes

### Database Migration
- Schema version 2 adds `slimeCount` column
- `fallbackToDestructiveMigration()` handles upgrade
- **WARNING**: This will clear existing game data

### Backward Compatibility
- Old `actionsUsed` field still present for action checkboxes
- New `slimeCount` field independent of action state
- Both systems coexist for different purposes

### BOM Handling
- Applied BOM removal to TokenComponents.kt
- Following established BOM_POLICY.md procedures

## Files Modified

### Database
- `app/src/main/java/com/ghostbusters/companion/data/database/entities/CharacterEntity.kt`
- `app/src/main/java/com/ghostbusters/companion/data/database/AppDatabase.kt`

### UI Components
- `app/src/main/java/com/ghostbusters/companion/ui/components/TokenComponents.kt`

### ViewModels
- `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/CharacterSheetViewModel.kt`

### Screens
- `app/src/main/java/com/ghostbusters/companion/ui/screens/CharacterSheetScreen.kt`

### Documentation
- `ACTIONS_SLIME_REDESIGN.md` (this file)

## Testing Checklist

- [ ] Unused actions display as lightning bolt (⚡)
- [ ] Tapping lightning bolt changes it to colored checkmark
- [ ] Tapping checkmark changes it back to lightning bolt
- [ ] Checkmarks display in character's color
- [ ] Actions can be toggled individually
- [ ] Slime button adds slime one at a time
- [ ] Slime button disabled when all actions slimed
- [ ] De-Slime button removes slime one at a time
- [ ] De-Slime button disabled when no slime
- [ ] Slimed actions show green slime indicator overlay
- [ ] Slimed actions are disabled (can't be toggled)
- [ ] Slime indicator appears in corner of slimed actions
- [ ] Slime counter shows correct count
- [ ] Venkman Level 1 ability automatically adds slime and grants 1 XP
- [ ] Venkman Level 2 ability automatically adds slime
- [ ] Venkman abilities disabled when all actions slimed
- [ ] Character colors display correctly

## December 10, 2025
Implementation complete. Database migration will occur on next app launch.

