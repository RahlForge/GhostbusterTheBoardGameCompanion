# Slime Color Feature - Quick Reference

## What Changed?

The slime icon in the Actions/Slime tracker now changes color based on which Ghostbusters game you're playing:

- **Ghostbusters (1984)**: Bright green slime 🟢
- **Ghostbusters II (1989)**: Hot pink slime 💗

## How It Works

When you toggle an Action token to Slime:
- The lightning bolt (⚡) changes to a filled circle
- The circle color is **green** for GB1 games
- The circle color is **pink** for GB2 games

## Why These Colors?

### Green for Ghostbusters (1984)
- Slimer is bright green
- "Slime him!" scene features green ectoplasm
- Iconic signature color of the original film

### Pink for Ghostbusters II (1989)
- The mood slime is pink/magenta
- River of Slime beneath NYC is pink
- Central plot element in the sequel
- Visually distinguishes GB2 from GB1

## Visual Example

```
GHOSTBUSTERS (1984) - Actions/Slime Tokens:
⚡ ⚡ 🟢  (2 Actions available, 1 Slime)

GHOSTBUSTERS II (1989) - Actions/Slime Tokens:
⚡ 💗 💗  (1 Action available, 2 Slime)
```

## Technical Details

- Color values:
  - Green: `#7FFF00` (Chartreuse/Slime Green)
  - Pink: `#FF1493` (Deep Pink/Hot Pink)
- Icon: Material Icons `CheckCircle`
- Reactively updates based on game type from database

## Files Modified

1. `Color.kt` - Added pink slime color
2. `CharacterSheetViewModel.kt` - Loads game instance to get game type
3. `TokenComponents.kt` - Displays slime in game-appropriate color
4. `CharacterSheetScreen.kt` - Passes game type to token component

## Testing

To verify the feature:
1. Create a new GB1 game → Slime should be green
2. Create a new GB2 game → Slime should be pink
3. Load existing games → Color should match game type
4. Toggle tokens → Smooth transition between ⚡ and colored slime

---

**Feature Status**: ✅ Complete
**Date Implemented**: December 10, 2025

