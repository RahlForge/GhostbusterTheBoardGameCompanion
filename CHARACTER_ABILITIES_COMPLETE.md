# Character Abilities Implementation - Complete!

## Overview
Successfully implemented the complete character ability system for the base Ghostbusters board game, including:
- Character stats display (Move/Drive, Line of Sight)
- Game-specific abilities for all 4 base characters
- Tappable ability system with visual feedback
- Winston's ghost deposit feature with XP calculation
- Dynamic stat modifications based on level

## Features Implemented

### 1. Character Stats Display
Added character stats card showing:
- **Move/Drive**: Movement and driving values (base: 2/6)
- **Line of Sight**: Vision range (base: 3)
- Stats update dynamically based on level abilities

### 2. Ability System
Created `AbilityType` enum with four types:
- **PASSIVE**: Always active (e.g., stat bonuses)
- **TAPPABLE**: User can manually activate
- **TRIGGERED_BY_DICE**: Will activate with dice rolling (future feature)
- **TEAM_PASSIVE**: Affects all Ghostbusters

### 3. Character-Specific Abilities

#### Peter Venkman
- **Level 1** (TAPPABLE): "When a Ghost Slimes you on your turn, gain 1 XP"
  - Tap to use an Action and gain 1 XP
- **Level 2** (TAPPABLE): "If adjacent Ghostbuster would get Slimed, swap spaces and you get Slimed instead"
  - Tap to use an Action
- **Level 3** (PASSIVE): Gain 1 Extra Action
- **Level 4** (DICE): Free Combat Action when hitting with a 6
- **Level 5** (TEAM): All Ghostbusters gain +1 Line of Sight

#### Egon Spengler
- **Level 1** (DICE): Gain 1 XP when rolling 1 on Combat Roll
- **Level 2** (DICE): Re-roll a failed Proton Roll once per turn
- **Level 3** (PASSIVE): Gain 1 Extra Action
- **Level 4** (DICE): Roll two Proton Dice and choose one
- **Level 5** (TEAM): All Ghostbusters can reroll 1's on Proton Die

#### Ray Stantz
- **Level 1** (TAPPABLE): "When you remove Slime from another Ghostbuster, gain 1 XP"
  - Tap to gain 1 XP
- **Level 2** (TAPPABLE): "Remove a Slime from yourself for free"
  - Tap to toggle a Slime back to Action
- **Level 3** (PASSIVE): Gain 1 Extra Action
- **Level 4** (DICE): Remove Slime from self or nearby Ghostbuster when hitting Ghost
- **Level 5** (TEAM): All Ghostbusters can reroll Movement Die on failed Proton Roll

#### Winston Zeddemore
- **Level 1** (PASSIVE): Gain 1 XP per 3 Ghost rating points deposited
- **Level 2** (PASSIVE): Free Deposit Action within LoS once per turn
- **Level 3** (PASSIVE): Gain 1 Extra Action
- **Level 4** (DICE): Free Move Action when hitting a Ghost
- **Level 5** (TEAM): All Ghostbusters gain +1 Move (3/7 instead of 2/6)

### 4. Ghost Deposit System (Winston)
Added comprehensive deposit feature:
- **Deposit Button**: Appears in Ghost Trap for Winston at Level 1+
- **Ghost Selection Dialog**: Multi-select interface with checkboxes
- **XP Calculation**: Shows total rating and XP gain (total ÷ 3)
- **Visual Feedback**: Selected ghosts highlighted
- **Automatic XP Award**: Winston gains XP when depositing

### 5. Ability UI Enhancements
- **Tappable Indicators**: "TAP" badge on tappable abilities
- **Team Passive Badges**: "TEAM" badge for team-wide abilities
- **Dice Badges**: "DICE" indicator for future dice mechanics
- **Color Coding**: Different colors for different ability types
- **Tap Hint**: "👆 Tap to use" appears on available tappable abilities
- **Locked Abilities**: Grayed out with 🔒 icon until level reached

## Technical Implementation

### Files Modified

1. **Models.kt**
   - Added `CharacterStats` data class
   - Added `AbilityType` enum
   - Enhanced `CharacterAbility` with type, action requirements, XP gain
   - Implemented `getAbilitiesForCharacter()` for game-specific abilities
   - Added `getBaseStats()` for character base stats

2. **CharacterSheetViewModel.kt**
   - Added `getCharacterStats()` with level-based modifications
   - Added `hasActionsAvailable()` helper method
   - Added `useAbility()` for handling tappable abilities
   - Added `depositGhosts()` for Winston's deposit feature
   - Stat calculation applies Venkman Level 5 LoS bonus
   - Stat calculation applies Winston Level 5 Move bonus

3. **CharacterSheetScreen.kt**
   - Added Character Stats card display
   - Updated Abilities section with type badges
   - Added tappable ability handling
   - Added conditional deposit button for Winston
   - Enhanced visual hierarchy and feedback

4. **GhostTrapSection.kt**
   - Added `onDepositGhosts` callback parameter
   - Added Deposit button (conditional)
   - Created `DepositGhostsDialog` with multi-select
   - Shows XP calculation in dialog
   - Visual feedback for selected ghosts

## Stat Modifications

### Base Stats (All Characters)
- Move: 2
- Drive: 6
- Line of Sight: 3

### Level-Based Modifications
- **Venkman Level 5**: LoS becomes 4 (for all Ghostbusters)
- **Winston Level 5**: Move becomes 3, Drive becomes 7 (for all Ghostbusters)

## User Experience

### Ability Interaction
1. **Passive Abilities**: Always active, no user interaction
2. **Tappable Abilities**: 
   - Card changes to secondary color when tappable
   - Shows "👆 Tap to use" hint
   - Tap card to activate
   - Automatic action consumption and XP gain
3. **Dice Abilities**: Badge shown, will be functional with dice system
4. **Team Abilities**: Badge shown, stats auto-update

### Deposit Flow (Winston)
1. Reach Level 1
2. Trap some ghosts
3. Click "Deposit" button in Ghost Trap
4. Select ghosts to deposit (multi-select with checkboxes)
5. Dialog shows total rating and XP calculation
6. Click "Deposit (N)" button
7. XP automatically awarded, ghosts removed

## Future Enhancements

### Dice System Integration
When dice rolling is implemented:
- Egon's abilities will become fully functional
- Venkman's Level 4 will trigger on 6 rolls
- Ray's Level 4 and 5 will activate
- Winston's Level 4 will activate

### Ability Cooldowns
Consider adding:
- Once-per-turn tracking for Level 2 abilities
- Visual indicator for used abilities
- Reset button for turn management

### Team Stat Display
Consider adding:
- Team stats overview screen
- Show cumulative bonuses from all characters
- Highlight which character provides which bonus

## Testing Checklist

- [ ] Test Venkman Level 1: Tap ability uses action and gains XP
- [ ] Test Venkman Level 2: Tap ability uses action
- [ ] Test Venkman Level 5: LoS shows as 4
- [ ] Test Ray Level 1: Tap ability gains XP
- [ ] Test Ray Level 2: Tap ability removes slime
- [ ] Test Winston Level 1: Deposit calculates XP correctly (÷3)
- [ ] Test Winston Level 5: Move/Drive shows as 3/7
- [ ] Test ability badges appear correctly
- [ ] Test locked abilities show 🔒
- [ ] Test tappable abilities only work when actions available
- [ ] Test deposit dialog multi-select
- [ ] Test deposit XP calculation display

## Known Limitations

1. **Team Abilities**: Currently only apply to the character themselves
   - Venkman's LoS boost only shows on his sheet
   - Winston's Move boost only shows on his sheet
   - Future: Need game-wide state to apply to all characters

2. **Once-Per-Turn**: No tracking for abilities that are "once per turn"
   - Ray Level 2, Egon Level 2 can be used multiple times
   - Future: Add turn tracking system

3. **Dice Abilities**: Marked but not functional yet
   - Will be implemented when dice rolling system is added

## Documentation

- Updated TODO.md with completed abilities section
- All ability text matches board game rules
- Code comments explain mechanics

## Status

✅ **Character Stats**: Complete
✅ **Base Game Abilities**: Complete (all 4 characters)
✅ **Tappable System**: Complete
✅ **Winston Deposit**: Complete
✅ **Visual Indicators**: Complete
⏳ **Dice Integration**: Pending dice system
⏳ **Team Stat Application**: Needs game-wide state
⏳ **Turn Tracking**: Future enhancement

---

**Date**: December 10, 2025  
**Feature**: Character Abilities (Ghostbusters Base Game)  
**Status**: Complete and Ready for Testing!  
**Next**: Add dice rolling system or implement GB2 abilities

