# TODO List - Future Enhancements

## Recently Completed ✅

### Character Abilities - Base Ghostbusters Game (December 10, 2025)
- [x] Add character stats display (Move/Drive, Line of Sight)
- [x] Create ability type system (Passive, Tappable, Dice-Triggered, Team)
- [x] Peter Venkman abilities (all 5 levels) with tappable L1 & L2
- [x] Egon Spengler abilities (all 5 levels) with dice mechanics
- [x] Ray Stantz abilities (all 5 levels) with tappable L1 & L2
- [x] Winston Zeddemore abilities (all 5 levels) with deposit system
- [x] Tappable ability system with visual feedback
- [x] Ghost deposit feature for Winston with XP calculation
- [x] Ability type badges (TAP, DICE, TEAM)
- [x] Dynamic stat modifications (Venkman LoS +1, Winston Move +1)
- [x] Documentation (CHARACTER_ABILITIES_COMPLETE.md)

### Game-Specific Slime Colors (December 10, 2025)
- [x] Add pink slime color for Ghostbusters II
- [x] Update CharacterSheetViewModel to load game instance
- [x] Update ActionSlimeTokens to accept game type parameter
- [x] Dynamically color slime based on game type (green for GB1, pink for GB2)
- [x] Update CharacterSheetScreen to pass game type to component
- [x] Documentation created (SLIME_COLOR_QUICK_REFERENCE.md, SLIME_COLOR_UPDATE.md)

## Priority: HIGH (Core Functionality)

### Character Abilities - Remaining
- [ ] Louis Tully abilities (Levels 1, 2, 4, 5) - GB2 expansion
- [ ] Slimer abilities (Levels 1, 2, 4, 5) - GB2 expansion
- [ ] Ghostbusters II abilities for base 4 characters
- [ ] Dice rolling system integration
- [ ] Turn tracking for once-per-turn abilities
- [ ] Team-wide stat application (cross-character bonuses)

### Character Portraits
- [ ] Create or source character portrait images
- [ ] Add images to `res/drawable/`
- [ ] Update `CharacterSheetScreen` to load and display portraits
- [ ] Add portrait selection to character cards in `GameDetailScreen`

### Launcher Icons
- [ ] Design Ghostbusters-themed app icon
- [ ] Generate icons for all densities (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- [ ] Add ic_launcher.png and ic_launcher_round.png to mipmap folders

## Priority: MEDIUM (Enhanced Features)

### Campaign System
- [ ] Create Campaign entity
- [ ] Add Campaign selection to GameSetupScreen
- [ ] Define campaigns for Ghostbusters:
  - [ ] Campaign 1: [Name TBD]
  - [ ] Campaign 2: [Name TBD]
  - [ ] Campaign 3: [Name TBD]
- [ ] Define campaigns for Ghostbusters II:
  - [ ] Campaign 1: [Name TBD]
  - [ ] Campaign 2: [Name TBD]
  - [ ] Campaign 3: [Name TBD]

### Scenario System
- [ ] Create Scenario entity
- [ ] Link scenarios to campaigns
- [ ] Add scenario selection UI
- [ ] Add "Random Scenario" option
- [ ] Implement random scenario generator logic

### Ghostbusters II Specific Features
- [ ] Add Ghost Trap Token UI to CharacterSheetScreen
- [ ] Only show for GB2 games
- [ ] Track deployment status
- [ ] Add toggle functionality

### Ghost Management
- [ ] Allow naming custom ghosts
- [ ] Add ghost transfer between characters
- [ ] Show total ghosts trapped in game summary
- [ ] Add ghost type/category field

### XP Management
- [ ] Add +/- buttons to XP tracker for fine control
- [ ] Add bulk XP add dialog (for multiple ghosts)
- [ ] Show XP history/log
- [ ] Add XP undo functionality

## Priority: LOW (Polish & UX)

### UI/UX Improvements
- [ ] Add animations for level-up
- [ ] Add haptic feedback for token taps
- [ ] Add sound effects (toggle in settings)
- [ ] Improve color scheme for dark mode
- [ ] Add app intro/tutorial for first-time users
- [ ] Add swipe gestures for navigation
- [ ] Improve XP tracker interactivity (drag to set XP)

### Visual Polish
- [ ] Design custom proton stream icons
- [ ] Design custom action/slime icons
- [ ] Add Ghostbusters logo to splash screen
- [ ] Add themed background patterns
- [ ] Improve ghost trap visual (animated trap doors?)
- [ ] Add character-specific backgrounds/themes

### Statistics & Tracking
- [ ] Add game statistics screen
  - [ ] Total games played
  - [ ] Total ghosts trapped
  - [ ] Highest XP reached
  - [ ] Most used character
- [ ] Add per-character statistics
- [ ] Add session timer

### Settings & Preferences
- [ ] Create Settings screen
- [ ] Add theme selection (light/dark/auto)
- [ ] Add sound/haptic toggles
- [ ] Add data backup/restore
- [ ] Add data export (JSON/CSV)
- [ ] Add app version info

### Quality of Life
- [ ] Add game notes/memo field
- [ ] Add character notes field
- [ ] Add quick actions (long-press menus)
- [ ] Add search/filter for game list
- [ ] Add sort options for game list
- [ ] Duplicate game feature

## Priority: FUTURE (Advanced Features)

### Multiplayer/Networking
- [ ] Design networking architecture
- [ ] Choose backend (Firebase, custom server, P2P?)
- [ ] Implement host/join flow
- [ ] Implement game room system
- [ ] Sync character sheet updates in real-time
- [ ] Handle connection loss gracefully
- [ ] Add player chat/communication
- [ ] Add host controls (kick, pass host)

### Cloud Features
- [ ] Add user authentication (optional)
- [ ] Cloud save backup
- [ ] Sync across devices
- [ ] Share games via link
- [ ] Community features?

### Equipment/Items System
- [ ] Add equipment entities
- [ ] Track equipment per character
- [ ] Add equipment library
- [ ] Implement equipment effects

### Advanced Rules
- [ ] Add difficulty settings
- [ ] Add variant rules toggles
- [ ] Add house rules customization
- [ ] Custom scenario creator

### Accessibility
- [ ] Add content descriptions for screen readers
- [ ] Improve color contrast ratios
- [ ] Add text size options
- [ ] Add high contrast mode
- [ ] Test with TalkBack

### Localization
- [ ] Extract all hardcoded strings
- [ ] Add Spanish translation
- [ ] Add French translation
- [ ] Add German translation
- [ ] Add Japanese translation

## Testing & Quality

### Testing
- [ ] Write unit tests for ViewModels
- [ ] Write unit tests for repositories
- [ ] Write integration tests for database
- [ ] Write UI tests for main flows
- [ ] Test on various screen sizes
- [ ] Test on tablets
- [ ] Test on foldables

### Performance
- [ ] Profile app performance
- [ ] Optimize database queries
- [ ] Optimize image loading
- [ ] Reduce APK size
- [ ] Add ProGuard rules

### Bug Fixes
- [ ] Test edge cases (0 XP, 30 XP)
- [ ] Test with no characters
- [ ] Test rapid tapping of tokens
- [ ] Test database migration scenarios
- [ ] Test app backgrounding/foregrounding

## Documentation

### Code Documentation
- [ ] Add KDoc comments to public APIs
- [ ] Document complex algorithms
- [ ] Add architecture decision records (ADRs)

### User Documentation
- [ ] Create user manual
- [ ] Create video tutorials
- [ ] Create FAQ
- [ ] Add in-app help system

## Release Preparation

### Pre-Release
- [ ] Create app icon (final version)
- [ ] Write Play Store description
- [ ] Create screenshots for Play Store
- [ ] Create promotional graphics
- [ ] Test on physical devices
- [ ] Beta testing with friends

### Release
- [ ] Generate signed APK/AAB
- [ ] Set up Play Store listing
- [ ] Submit to Google Play (if desired)
- [ ] Create GitHub releases
- [ ] Write release notes

---

## Notes

This is a living document. Check off items as you complete them and add new items as you think of them!

**Current Phase:** Phase 1 Complete - Basic functionality working
**Next Focus:** Character abilities and portraits (HIGH priority items)

---

Last Updated: December 9, 2025
