# Slime Icon Color Update - Implementation Summary

## Overview
Successfully implemented game-specific slime colors for the Actions/Slime tokens in the Ghostbusters Companion App:
- **Ghostbusters (GB1)**: Green slime (`SlimeGreen` - #7FFF00)
- **Ghostbusters II (GB2)**: Pink slime (`SlimePink` - #FF1493 Hot Pink)

## Changes Made

### 1. Color.kt - Added Pink Slime Color
**File**: `app/src/main/java/com/ghostbusters/companion/ui/theme/Color.kt`

Added new color constant:
```kotlin
val SlimePink = Color(0xFFFF1493) // Hot pink for GB2 slime
```

This complements the existing `SlimeGreen` color for GB1.

### 2. CharacterSheetViewModel.kt - Load Game Instance
**File**: `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/CharacterSheetViewModel.kt`

**Changes**:
- Added `GameInstanceRepository` injection
- Added `_gameInstance` and `gameInstance` StateFlow
- Implemented `loadGameInstance()` method to fetch game data based on character's gameInstanceId
- Now exposes game type information to the UI layer

**Key Code**:
```kotlin
private val _gameInstance = MutableStateFlow<GameInstanceEntity?>(null)
val gameInstance: StateFlow<GameInstanceEntity?> = _gameInstance.asStateFlow()

private fun loadGameInstance(gameId: Long) {
    viewModelScope.launch {
        gameInstanceRepository.getGameInstanceById(gameId).collect { gameInstance ->
            _gameInstance.value = gameInstance
        }
    }
}
```

### 3. TokenComponents.kt - Game-Based Slime Color
**File**: `app/src/main/java/com/ghostbusters/companion/ui/components/TokenComponents.kt`

**Changes**:
- Added `gameType: GameType` parameter to `ActionSlimeTokens` composable
- Implemented color selection logic based on game type
- Changed slime representation from emoji (💧) to colored `CheckCircle` icon
- Kept action state as lightning bolt emoji (⚡)

**Key Code**:
```kotlin
fun ActionSlimeTokens(
    actionCount: Int,
    usedActions: List<Boolean>,
    gameType: GameType,  // NEW PARAMETER
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Choose slime color based on game type
    val slimeColor = when (gameType) {
        GameType.GHOSTBUSTERS -> SlimeGreen     // Green for GB1
        GameType.GHOSTBUSTERS_II -> SlimePink   // Pink for GB2
    }
    
    // Display colored CheckCircle icon when in slime state
    if (usedActions[index]) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Slime ${index + 1}",
            tint = slimeColor,
            modifier = Modifier.size(48.dp)
        )
    }
}
```

### 4. CharacterSheetScreen.kt - Pass Game Type
**File**: `app/src/main/java/com/ghostbusters/companion/ui/screens/CharacterSheetScreen.kt`

**Changes**:
- Collect `gameInstance` state from ViewModel
- Pass `gameType` to `ActionSlimeTokens` component
- Default to `GameType.GHOSTBUSTERS` if game instance not loaded yet

**Key Code**:
```kotlin
val gameInstance by viewModel.gameInstance.collectAsState()

// In ActionSlimeTokens call:
ActionSlimeTokens(
    actionCount = viewModel.getMaxActions(),
    usedActions = (0 until viewModel.getMaxActions()).map { viewModel.isActionUsed(it) },
    gameType = gameInstance?.gameType ?: GameType.GHOSTBUSTERS,  // NEW
    onToggle = { viewModel.toggleAction(it) }
)
```

## Visual Changes

### Before
- All games used blue water drop emoji (💧) for slime

### After
- **Ghostbusters (GB1)**: Bright green `CheckCircle` icon (#7FFF00)
- **Ghostbusters II (GB2)**: Hot pink `CheckCircle` icon (#FF1493)
- Action state remains as lightning bolt (⚡)

## Thematic Justification

### Ghostbusters (1984) - Green Slime
- Slimer's ectoplasm is bright green
- The iconic "Slime him!" scene features green slime
- Green is the signature slime color of the original film

### Ghostbusters II (1989) - Pink Slime
- Features mood slime that glows pink/magenta
- The River of Slime flows pink beneath NYC
- Pink slime is a central plot element in GB2
- Differentiates visually from GB1

## Technical Notes

### BOM Encoding Fix
All four modified files were recreated to remove UTF-8 BOM (Byte Order Mark) characters that were causing compilation errors. Files are now properly encoded as UTF-8 without BOM.

### State Flow Architecture
The implementation follows the MVVM pattern:
1. **Repository Layer**: Provides reactive Flow of game instance data
2. **ViewModel Layer**: Converts Flow to StateFlow, manages state
3. **UI Layer**: Collects StateFlow and renders based on game type

### Reactive Updates
When a game instance is loaded or updated:
1. Character loads from database (includes `gameInstanceId`)
2. ViewModel automatically loads corresponding game instance
3. Game type flows to UI
4. Slime color updates reactively based on game type

## Testing Recommendations

1. **Create GB1 Game**: Verify slime displays as green
2. **Create GB2 Game**: Verify slime displays as pink
3. **Toggle Actions**: Confirm smooth transition between ⚡ and colored slime
4. **Load Existing Games**: Ensure color persists correctly across app restarts

## Future Enhancements

Potential improvements:
- Add custom slime icons instead of CheckCircle
- Animate slime token transitions
- Add slime sound effects on toggle
- Display different slime textures for GB1 vs GB2

## Files Modified

1. ✅ `ui/theme/Color.kt` - Added `SlimePink` color
2. ✅ `ui/viewmodels/CharacterSheetViewModel.kt` - Load game instance
3. ✅ `ui/components/TokenComponents.kt` - Game-based slime color
4. ✅ `ui/screens/CharacterSheetScreen.kt` - Pass game type

## Status

✅ **Implementation Complete**
✅ **BOM Encoding Fixed**
✅ **Architecture Follows MVVM Pattern**
✅ **Thematically Accurate to Films**
⏳ **Build Verification Pending**

---

**Date**: December 10, 2025
**Feature**: Game-Specific Slime Colors
**Impact**: Enhanced thematic accuracy and visual distinction between GB1 and GB2 games

