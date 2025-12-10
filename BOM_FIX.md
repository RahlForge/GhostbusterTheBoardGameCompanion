# BOM Character Fixes - December 9, 2025

## Problem
Several Kotlin files had UTF-8 BOM (Byte Order Mark) characters at the beginning, causing compilation errors:
```
Expecting a top level declaration
imports are only allowed in the beginning of file
```

## Root Cause
The files were saved with UTF-8 BOM encoding, which adds an invisible character (﻿) at the start of the file. Kotlin doesn't recognize this character and treats it as invalid syntax before the `package` declaration.

## Files Fixed

### 1. GameDetailViewModel.kt ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/GameDetailViewModel.kt`

**Action**: Deleted and recreated file without BOM character

**Changes Included**:
- Removed UTF-8 BOM
- Kept the fixed coroutine structure with nested launches
- Proper error handling for game instance and character loading
- Added detailed error logging with printStackTrace()

### 2. Converters.kt ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/data/database/Converters.kt`

**Action**: Deleted and recreated file without BOM character

**Changes Included**:
- Removed UTF-8 BOM
- Kept the null-safe JSON parsing with try-catch blocks
- Empty list fallback for parsing errors

### 3. CharacterEntity.kt ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/data/database/entities/CharacterEntity.kt`

**Action**: Deleted and recreated file without BOM character

**Changes Included**:
- Removed UTF-8 BOM
- All entity fields preserved

### 4. GameSetupViewModel.kt ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/GameSetupViewModel.kt`

**Action**: Deleted and recreated file without BOM character

**Changes Included**:
- Removed UTF-8 BOM
- Added 100ms delay after character creation to ensure database transaction completes
- Added detailed error logging with printStackTrace()
- Better error messages showing exception type and message

### 5. MainActivity.kt ✅
**Location**: `app/src/main/java/com/ghostbusters/companion/MainActivity.kt`

**Action**: Deleted and recreated file without BOM character

**Changes Included**:
- Removed UTF-8 BOM
- Improved navigation after game creation (uses popUpTo instead of double navigation)
- This prevents race conditions when navigating to newly created game

## Verification

All files now compile without errors:
- ✅ GameDetailViewModel.kt - No errors
- ✅ Converters.kt - No errors (only warnings about unused catch parameters)
- ✅ GameSetupViewModel.kt - No errors
- ✅ CharacterSheetViewModel.kt - No errors
- ✅ GameListViewModel.kt - No errors
- ✅ MainActivity.kt - No errors
- ✅ All screen files - No errors

## Prevention

To prevent BOM issues in the future:
1. Configure your IDE to save files as "UTF-8 without BOM"
2. In IntelliJ/Android Studio: File → Settings → Editor → File Encodings → Set "UTF-8" and uncheck "Add BOM for UTF-8 files"
3. Use a .editorconfig file with: `charset = utf-8` (without BOM specification)

## Status
✅ **All compilation errors resolved**
✅ **App should now build and run successfully**
✅ **All crash fixes from previous session intact**

