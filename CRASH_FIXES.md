# Crash Fixes Applied - December 9, 2025

## Issues Fixed

### 1. **Critical: GameDetailViewModel Race Condition** ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/GameDetailViewModel.kt`

**Problem**: The `loadGameData()` function had two separate `viewModelScope.launch` blocks collecting from Flows. The first launch block would collect from `getGameInstanceById()` forever (since `.collect{}` is a suspending function that never completes), and the second launch would collect from `getCharactersByGameInstance()` forever. This caused:
- Race conditions in state updates
- Memory leaks from uncancelled coroutines
- Potential crashes when the ViewModel is destroyed while collecting

**Solution**: Wrapped both collect operations inside a single parent coroutine scope with nested `launch` blocks. This ensures proper structured concurrency and both collectors can run simultaneously without blocking each other.

**Before**:
```kotlin
private fun loadGameData() {
    viewModelScope.launch {
        try {
            gameInstanceRepository.getGameInstanceById(gameId).collect { gameInstance ->
                _state.value = _state.value.copy(
                    gameInstance = gameInstance,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = "Failed to load game: ${e.message}"
            )
        }
    }

    viewModelScope.launch {
        characterRepository.getCharactersByGameInstance(gameId).collect { characters ->
            _state.value = _state.value.copy(characters = characters)
        }
    }
}
```

**After**:
```kotlin
private fun loadGameData() {
    viewModelScope.launch {
        try {
            // Collect game instance updates
            launch {
                gameInstanceRepository.getGameInstanceById(gameId).collect { gameInstance ->
                    _state.value = _state.value.copy(
                        gameInstance = gameInstance,
                        isLoading = false
                    )
                }
            }
            
            // Collect character updates
            launch {
                characterRepository.getCharactersByGameInstance(gameId).collect { characters ->
                    _state.value = _state.value.copy(characters = characters)
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = "Failed to load game: ${e.message}"
            )
        }
    }
}
```

### 2. **Type Converters Crash Prevention** ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/data/database/Converters.kt`

**Problem**: The JSON type converters (`toExpansionTypeList` and `toTrappedGhostList`) could crash if:
- The database contained empty strings
- The database contained null values (stored as "null" string)
- JSON parsing failed due to corrupted data
- `gson.fromJson()` returned null

**Solution**: Added defensive null checks and try-catch blocks to return empty lists on any parsing failure.

**Before**:
```kotlin
@TypeConverter
fun toExpansionTypeList(value: String): List<ExpansionType> {
    val listType = object : TypeToken<List<ExpansionType>>() {}.type
    return gson.fromJson(value, listType)
}

@TypeConverter
fun toTrappedGhostList(value: String): List<TrappedGhostData> {
    val listType = object : TypeToken<List<TrappedGhostData>>() {}.type
    return gson.fromJson(value, listType)
}
```

**After**:
```kotlin
@TypeConverter
fun toExpansionTypeList(value: String): List<ExpansionType> {
    if (value.isEmpty() || value == "null") return emptyList()
    return try {
        val listType = object : TypeToken<List<ExpansionType>>() {}.type
        gson.fromJson(value, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

@TypeConverter
fun toTrappedGhostList(value: String): List<TrappedGhostData> {
    if (value.isEmpty() || value == "null") return emptyList()
    return try {
        val listType = object : TypeToken<List<TrappedGhostData>>() {}.type
        gson.fromJson(value, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}
```

### 3. **Build Error Fixed** ✅
**Location**: `app/ic_launcher-playstore.png`

**Problem**: The `ic_launcher-playstore.png` file was in `app/src/main/` which is invalid. Android resource files must be in specific subdirectories.

**Solution**: Moved the file from `app/src/main/ic_launcher-playstore.png` to `app/ic_launcher-playstore.png`.

### 4. **Code Quality Improvements** ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/ui/screens/GameSetupScreen.kt`

**Problem**: Using deprecated Kotlin 1.9+ enum methods and unused imports.

**Solution**:
- Replaced `GameType.values()` with `GameType.entries`
- Replaced `ExpansionType.values()` with `ExpansionType.entries`
- Removed unused imports (`clickable`, `LazyColumn`, `items`)

## Testing Recommendations

After these fixes, you should test:

1. **Create Game Flow**:
   - Create a new game with 1-4 characters
   - Verify navigation to game detail screen works
   - Verify characters are displayed correctly

2. **Load Game Flow**:
   - Navigate back to game list
   - Click on existing game
   - Verify game details load without crash
   - Verify characters list populates correctly

3. **Character Sheet Flow**:
   - Click on a character from game detail screen
   - Verify character data loads
   - Verify XP tracker, proton streams, and ghost trap work

4. **Edge Cases**:
   - Create game with GB2 + expansions
   - Create game with all 4 base characters
   - Test adding/removing ghosts from trap
   - Test XP changes and level progression

## Root Cause Analysis

The primary crash was likely caused by the race condition in `GameDetailViewModel`. When navigating to a game detail screen after creating or selecting a game, the ViewModel would:

1. Start collecting game instance data in one coroutine
2. Start collecting character data in another coroutine (before the first completed setup)
3. Both coroutines would try to update the same `_state` value simultaneously
4. If the ViewModel was destroyed during collection (e.g., user navigated away quickly), the error handling wouldn't catch it properly
5. This could cause crashes, especially on slower devices or with database delays

The secondary issue with type converters could cause crashes when:
- First time loading a game (empty lists stored as "[]")
- Database migration issues
- Corrupted data from previous app versions

## Architecture Notes

The fixes maintain the reactive architecture:
- ✅ Flows continue to emit updates as database changes
- ✅ UI automatically updates when data changes
- ✅ ViewModels properly scope coroutines to their lifecycle
- ✅ Error handling gracefully degrades to empty lists rather than crashing

## Additional Recommendations

Consider these future improvements:
1. Add logging to track when database operations occur
2. Add crash analytics (like Firebase Crashlytics) to catch production issues
3. Add loading states to UI during database operations
4. Consider using `stateIn()` for the game instance and characters flows for better performance
5. Add unit tests for the type converters with edge cases

