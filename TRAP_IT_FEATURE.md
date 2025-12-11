# Trap It! Feature Implementation

## Overview
Implemented a new proton stream and ghost trapping system that better reflects the collaborative nature of ghostbusting in the board game.

## Changes Made

### 1. Proton Stream System Overhaul

#### "Trap It!" Button
- Added a prominent "Trap It!" button to the Proton Streams section
- Button is disabled when no proton streams are active
- Button shows active stream count below the tokens
- When pressed, asks if the ghost was successfully trapped

#### New XP Mechanics
- **OLD**: Gained XP = Ghost Rating when trapping
- **NEW**: Each Ghostbuster gains XP = Their Own Active Proton Streams
- XP is gained whether the ghost is trapped or not (reflects effort)
- All proton streams are automatically deactivated after using "Trap It!"
- Ghost rating = Total of all selected Ghostbusters' active proton streams

#### Multi-Ghostbuster Collaboration
- When "Trap It!" is pressed, dialog shows all OTHER Ghostbusters with active proton streams
- User selects which Ghostbusters were assisting with the capture
- Each selected assistant gains XP equal to their active stream count
- The trapping Ghostbuster gains XP equal to their active stream count
- Ghost rating = Sum of all selected Ghostbusters' streams + trapping Ghostbuster's streams
- All participating Ghostbusters' proton streams are reset to inactive

### 2. Ghost Transfer System

#### Transfer Button (All Characters)
- Every Ghostbuster now has a "Transfer" button in their Ghost Trap section
- Appears alongside the Deposit button when ghosts are present
- Allows selecting multiple ghosts to transfer

#### Character Selection Dialog
- Shows all other Ghostbusters in the game
- Displays each character's name with their proton stream color
- Shows current ghost count for each potential recipient
- Clicking a character completes the transfer instantly

#### Backend Implementation
- `transferGhostsToCharacter()` method handles the transfer atomically
- Uses repository pattern to update both source and target characters
- Properly removes ghosts from sender and adds to receiver

### 3. UI Components Updated

#### ProtonStreamTokens.kt
- Added `onTrapIt` callback parameter
- Added active stream counter display
- Button styling uses character's proton stream color
- Shows "{n} stream(s) active" when streams are selected

#### GhostTrapSection.kt
- Added `onTransferGhosts` parameter
- New `TransferGhostsDialog` composable
- Dialog uses same selection pattern as Deposit dialog
- Clean, consistent UI across both dialogs

#### CharacterSheetScreen.kt
- Added "Trap It!" confirmation dialog
- Added character selection dialog for transfers
- Integrated with ViewModel methods
- Proper state management for dialog visibility

### 4. ViewModel Methods

#### New Methods
- `getCharactersWithActiveStreams()`: Returns list of other characters with active streams
  - Excludes current character
  - Returns pairs of (CharacterEntity, streamCount)
  - Used to populate assistant selection dialog

- `handleTrapIt(trapped: Boolean, assistingCharacterIds: List<Long>)`: Main trapping logic
  - Counts active proton streams for current character
  - Awards XP to each assisting character based on their stream count
  - Awards XP to current character based on their stream count
  - Optionally adds ghost with rating = total streams from all participants
  - Deactivates all streams for all participating characters
  
- `transferGhostsToCharacter(ghostIds, targetId)`: Transfer ghosts
  - Removes from current character
  - Adds to target character
  - Uses repository for atomic updates
  
- `receiveTransferredGhosts(ghosts)`: Helper for receiving ghosts

#### Modified Methods
- `trapGhost(rating)`: No longer awards XP (manual add only)

## Example Scenario

**Setup:**
- Egon: 2 active proton streams
- Peter: 1 active proton stream
- Winston: 2 active proton streams
- Ray: 1 active proton stream

**Ray's Turn:**
1. Ray adds a 2nd proton stream (now has 2 active)
2. Ray presses "Trap It!"
3. Dialog shows: Peter (1 stream), Egon (2 streams), Winston (2 streams)
4. Ray selects only Peter as assistant
5. **Results:**
   - Peter gains 1 XP, his 1 stream is deactivated
   - Ray gains 2 XP and receives Ghost (Rating 3), his 2 streams are deactivated
   - Egon and Winston still have active streams

**Winston's Turn:**
1. Winston adds a 3rd proton stream (now has 3 active)
2. Winston presses "Trap It!"
3. Dialog shows: Only Egon (2 streams) - Peter and Ray have no active streams
4. Winston selects Egon as assistant
5. **Results:**
   - Egon gains 2 XP, his 2 streams are deactivated
   - Winston gains 3 XP and receives Ghost (Rating 5), his 3 streams are deactivated

## Game Mechanics Rationale

### Multiple Ghostbusters Can Engage Same Ghost
- In the board game, multiple players can place proton streams on the same ghost
- The new system reflects this collaborative gameplay
- Each Ghostbuster gains XP for their contribution (active streams)
- Only Ghostbusters with active streams can be selected as assistants

### Effort vs Success
- You gain XP whether the ghost is trapped or not
- This rewards players for attempting difficult captures
- Reflects the "you learn from every encounter" theme

### Transfer for Tactical Play
- Allows strategic ghost redistribution
- Useful when one player is close to a deposit point
- Enables Winston's Deposit ability to benefit the whole team
- Matches the cooperative nature of the game

## Files Modified

### Core Components
- `app/src/main/java/com/ghostbusters/companion/ui/components/TokenComponents.kt`
- `app/src/main/java/com/ghostbusters/companion/ui/components/GhostTrapSection.kt`

### ViewModels
- `app/src/main/java/com/ghostbusters/companion/ui/viewmodels/CharacterSheetViewModel.kt`

### Screens
- `app/src/main/java/com/ghostbusters/companion/ui/screens/CharacterSheetScreen.kt`

### Documentation
- `BOM_POLICY.md` (BOM removal performed on all files)
- `TRAP_IT_FEATURE.md` (this file)

## Testing Notes

### Manual Testing Checklist
- [X ] "Trap It!" button disabled when no streams active
- [X ] "Trap It!" button enabled with at least 1 stream active
- [X ] Dialog shows only other Ghostbusters with active streams
- [X ] Can select/deselect assistants by clicking cards
- [X ] Selected assistants show highlighted background
- [X ] Stream count displays correctly for each character
- [X ] Each Ghostbuster gains XP = their own active streams
- [X ] Ghost rating = sum of all participants' active streams
- [X ] All participating Ghostbusters' streams deactivate
- [X ] Non-selected Ghostbusters keep their active streams
- [X ] Ghost added to trap when "Yes - Trapped!" selected
- [X ] No ghost added when "No - Escaped" selected (but XP still awarded)
- [X] Transfer button visible for all characters
- [X] Transfer dialog shows all other characters
- [X] Ghosts successfully move between characters
- [X] Character colors display correctly in all dialogs
- [X] No duplicate ghosts created during transfer

## Known Issues / Future Improvements

1. **Network Sync**: Transfer only works locally, multiplayer sync needed
2. **Undo**: No undo for transfers (future enhancement)
3. **Animations**: Could add visual feedback for transfers
4. **Notifications**: Target player doesn't get notified of incoming ghosts (yet)

## December 10, 2025
Implementation complete and ready for testing.

