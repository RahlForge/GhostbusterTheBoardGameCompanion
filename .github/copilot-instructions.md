# GitHub Copilot Instructions for Ghostbusters Companion App

## Project Overview

This is an Android companion application for Cryptozoic's "Ghostbusters: The Board Game" and "Ghostbusters II: The Board Game". The app helps players manage game sessions, track character progression, and handle game mechanics digitally.

## Technology Stack & Architecture

### Core Technologies
- **Language**: Kotlin (100%)
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room (SQLite) with reactive Flow
- **DI**: Hilt (Dagger)
- **Navigation**: Navigation Compose with type-safe arguments
- **Async**: Kotlin Coroutines & StateFlow
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34 (Android 14)

### Project Structure
```
com.ghostbusters.companion/
├── data/                    # Data layer
│   ├── database/
│   │   ├── entities/       # Room entities (data models)
│   │   ├── dao/           # Data Access Objects
│   │   ├── AppDatabase.kt
│   │   └── Converters.kt  # Type converters for Room
│   └── repository/        # Repository pattern
├── domain/                 # Domain layer
│   ├── model/            # Domain models, enums, data classes
│   └── usecase/          # Business logic (future)
├── ui/                    # Presentation layer
│   ├── components/       # Reusable Composables
│   ├── screens/          # Screen Composables
│   ├── viewmodels/       # ViewModels with StateFlow
│   ├── theme/            # Material 3 theme
│   └── Navigation.kt     # Navigation routes
├── di/                   # Hilt modules
└── util/                 # Utilities
```

## Coding Standards & Patterns

### Naming Conventions
- **Files**: PascalCase (e.g., `GameListViewModel.kt`)
- **Classes/Objects**: PascalCase (e.g., `class CharacterRepository`)
- **Functions**: camelCase (e.g., `fun loadGameData()`)
- **Variables**: camelCase (e.g., `val currentXp`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g., `const val MAX_XP = 30`)
- **Composables**: PascalCase (e.g., `@Composable fun XpTracker()`)

### Architecture Patterns

#### 1. Data Layer
```kotlin
// Entities: Room database models
@Entity(tableName = "table_name")
data class EntityName(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val field: Type
)

// DAOs: Database operations with Flow
@Dao
interface EntityDao {
    @Query("SELECT * FROM table_name")
    fun getAll(): Flow<List<EntityName>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EntityName): Long
}

// Repositories: Single source of truth
@Singleton
class EntityRepository @Inject constructor(
    private val dao: EntityDao
) {
    fun getAll(): Flow<List<EntityName>> = dao.getAll()
    suspend fun create(entity: EntityName): Long = dao.insert(entity)
}
```

#### 2. Domain Layer
```kotlin
// Enums: Game types, character names, levels
enum class EnumName {
    VALUE_ONE,
    VALUE_TWO;
    
    fun getDisplayName(): String = when (this) {
        VALUE_ONE -> "Display One"
        VALUE_TWO -> "Display Two"
    }
}

// Domain models: Business logic objects
data class DomainModel(
    val id: String,
    val property: Type
)
```

#### 3. Presentation Layer (ViewModels)
```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle // For navigation args
) : ViewModel() {
    
    // State as StateFlow, exposed as immutable
    private val _state = MutableStateFlow(InitialState())
    val state: StateFlow<State> = _state.asStateFlow()
    
    // Or collect from repository directly
    val data: StateFlow<List<Entity>> = repository.getData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Actions as public functions
    fun performAction(param: Type) {
        viewModelScope.launch {
            // Update state or call repository
            _state.value = _state.value.copy(field = newValue)
            repository.update(entity)
        }
    }
}
```

#### 4. UI Layer (Compose)
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenName(
    onNavigateBack: () -> Unit,
    onNavigateTo: (Long) -> Unit,
    viewModel: ScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Title") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Content
        }
    }
}
```

### State Management Rules
1. **ViewModels own state**: Use `StateFlow` or `Flow` from repositories
2. **Immutable state**: Use data class `.copy()` for updates
3. **Single source of truth**: Repository → ViewModel → UI
4. **Reactive updates**: Use `collectAsState()` in Composables
5. **Screen state**: Group related state in data classes

### Database Patterns
```kotlin
// Always use Flow for reactive queries
@Query("SELECT * FROM table WHERE id = :id")
fun getById(id: Long): Flow<Entity?>

// Suspend functions for write operations
@Insert
suspend fun insert(entity: Entity): Long

@Update
suspend fun update(entity: Entity)

@Delete
suspend fun delete(entity: Entity)

// Use cascade delete for parent-child relationships
@ForeignKey(
    entity = ParentEntity::class,
    parentColumns = ["id"],
    childColumns = ["parentId"],
    onDelete = ForeignKey.CASCADE
)
```

### Compose UI Guidelines

#### Component Organization
```kotlin
// Main screen composable
@Composable
fun MainScreen() { /* ... */ }

// Private helper composables in same file
@Composable
private fun SubComponent() { /* ... */ }

// Reusable components in ui/components/
@Composable
fun ReusableComponent(
    param: Type,
    onAction: () -> Unit,
    modifier: Modifier = Modifier // Always provide modifier param
) { /* ... */ }
```

#### Modifier Patterns
```kotlin
// Always accept and apply modifier
@Composable
fun Component(modifier: Modifier = Modifier) {
    Box(modifier = modifier) { // Apply at root
        // Content
    }
}

// Chain modifiers: size → padding → behavior
Modifier
    .fillMaxWidth()
    .height(48.dp)
    .padding(16.dp)
    .clickable { /* action */ }
```

#### State Hoisting
```kotlin
// Hoist state to parent, pass down actions
@Composable
fun Parent() {
    var value by remember { mutableStateOf("") }
    Child(
        value = value,
        onValueChange = { value = it }
    )
}

@Composable
fun Child(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(value = value, onValueChange = onValueChange)
}
```

## Domain-Specific Rules

### Character System
- 6 characters total: Venkman, Spengler, Zeddemore, Stantz, Tully, Slimer
- Each has a unique color for proton streams (defined in `CharacterColor` enum)
- Tully requires Plazm Phenomenon expansion (GB2)
- Slimer requires Sea Fright expansion (GB2)

### XP & Level System
- XP range: 0-30 (enforced with `.coerceIn(0, 30)`)
- 5 levels with specific XP ranges:
  - Level 1: 1-4 XP
  - Level 2: 5-10 XP
  - Level 3: 11-18 XP (unlocks 3rd Action)
  - Level 4: 19-29 XP
  - Level 5: 30 XP
- Use `Level.fromXp(xp)` for level calculation
- XP gained when trapping ghosts (equals ghost rating)
- XP never lost when removing ghosts

### Token Management
- **Proton Streams**: 5 per character, use bit masking for storage
- **Actions/Slime**: 2 at start, 3 from Level 3+, toggle between states
- Use bit operations: `1 shl index` for masking
- Store as Int in database (5 bits for 5 tokens)

### Ghost Trap Rules
- Ghosts have ratings: 1, 2, or 3
- Store as `List<TrappedGhostData>` (converted to JSON by Room)
- Gain XP = ghost rating when trapping
- No XP loss when removing ghosts
- Each ghost has unique ID (UUID)

### Game Type Logic
```kotlin
when (gameType) {
    GameType.GHOSTBUSTERS -> {
        // Base 4 characters only
    }
    GameType.GHOSTBUSTERS_II -> {
        // Base 4 + expansion characters if enabled
        if (expansions.contains(ExpansionType.PLAZM_PHENOMENON)) {
            // Louis Tully available
        }
        if (expansions.contains(ExpansionType.SEA_FRIGHT)) {
            // Slimer available
        }
    }
}
```

## Code Generation Guidelines

### When Creating New Features

1. **Start with domain models** (enums, data classes)
2. **Add database entities and DAOs** if persistence needed
3. **Create/update repository** for data access
4. **Build ViewModel** with StateFlow
5. **Create UI composables** (screen → components)
6. **Add to navigation** if new screen
7. **Update DI modules** if needed

### When Modifying Existing Features

1. **Check database schema** - might need migration
2. **Update entities** if data structure changes
3. **Modify repository** if new operations needed
4. **Update ViewModel state** and actions
5. **Adjust UI** to reflect changes
6. **Test error handling** and edge cases

### Code Review Checklist

- [ ] Follows Kotlin coding conventions
- [ ] Uses Jetpack Compose best practices
- [ ] StateFlow for state management
- [ ] Proper error handling (try-catch in ViewModels)
- [ ] Null safety handled (safe calls, elvis operator)
- [ ] Resources not hardcoded (use strings.xml)
- [ ] Modifiers always provided and applied
- [ ] Database operations use suspend/Flow
- [ ] No blocking operations on main thread
- [ ] Navigation uses sealed class routes
- [ ] Hilt injection properly configured

## Common Patterns & Examples

### Adding a New Screen

```kotlin
// 1. Add to Navigation.kt
sealed class Screen(val route: String) {
    // ...existing screens...
    object NewScreen : Screen("new_screen/{paramId}") {
        fun createRoute(paramId: Long) = "new_screen/$paramId"
    }
}

// 2. Create ViewModel
@HiltViewModel
class NewScreenViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val paramId: Long = savedStateHandle.get<Long>("paramId") ?: 0L
    // ...state and actions...
}

// 3. Create Screen Composable
@Composable
fun NewScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    // ...UI implementation...
}

// 4. Add to MainActivity navigation
composable(
    route = Screen.NewScreen.route,
    arguments = listOf(
        navArgument("paramId") { type = NavType.LongType }
    )
) {
    NewScreen(onNavigateBack = { navController.popBackStack() })
}
```

### Adding Database Entities

```kotlin
// 1. Create entity
@Entity(
    tableName = "new_table",
    foreignKeys = [ /* if needed */ ],
    indices = [Index("foreignKeyField")] // for FK fields
)
data class NewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val field: Type
)

// 2. Create DAO
@Dao
interface NewEntityDao {
    @Query("SELECT * FROM new_table")
    fun getAll(): Flow<List<NewEntity>>
    
    @Insert
    suspend fun insert(entity: NewEntity): Long
}

// 3. Add to AppDatabase
@Database(
    entities = [
        // ...existing entities...
        NewEntity::class
    ],
    version = 2, // Increment version!
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // ...existing DAOs...
    abstract fun newEntityDao(): NewEntityDao
}

// 4. Create repository
@Singleton
class NewEntityRepository @Inject constructor(
    private val dao: NewEntityDao
) {
    fun getAll(): Flow<List<NewEntity>> = dao.getAll()
    suspend fun create(entity: NewEntity): Long = dao.insert(entity)
}

// 5. Update DatabaseModule if needed
@Provides
fun provideNewEntityDao(database: AppDatabase): NewEntityDao {
    return database.newEntityDao()
}
```

## Testing Patterns

### ViewModel Testing
```kotlin
@Test
fun `action updates state correctly`() = runTest {
    val viewModel = TestViewModel(fakeRepository)
    viewModel.performAction(testData)
    
    val state = viewModel.state.value
    assertEquals(expectedValue, state.field)
}
```

### Repository Testing
```kotlin
@Test
fun `repository returns data from dao`() = runTest {
    val testData = listOf(TestEntity())
    whenever(dao.getAll()).thenReturn(flowOf(testData))
    
    val result = repository.getAll().first()
    assertEquals(testData, result)
}
```

## Color Palette

Use predefined colors from `ui/theme/Color.kt`:
- **GhostbustersYellow**: `0xFFFFD700` - Primary brand color
- **GhostbustersBlack**: `0xFF000000` - Text/UI
- **SlimeGreen**: `0xFF7FFF00` - Slime indicators
- **TrapYellow**: `0xFFFFD700` - Ghost trap UI
- **TrapBlack**: `0xFF1A1A1A` - Ghost trap background

Character colors (proton streams):
- Spengler: Orange `0xFFFF8C00`
- Venkman: Red `0xFFDC143C`
- Stantz: Yellow `0xFFFFD700`
- Zeddemore: Purple `0xFF9370DB`
- Tully: Khaki `0xFFF0E68C`
- Slimer: Green `0xFF32CD32`

## Dependencies (build.gradle.kts)

Current versions:
- Compose BOM: 2024.01.00
- Room: 2.6.1
- Hilt: 2.48
- Navigation: 2.7.6
- Kotlin: 1.9.20
- Gradle Plugin: 8.2.0

## Important Notes

1. **Always use `coerceIn(0, 30)` for XP values** to enforce limits
2. **Level 3 grants +1 Action** - check this in action count logic
3. **Ghost trap XP is additive only** - never subtract when removing ghosts
4. **Bit masking for tokens** - use `1 shl index` for 5-token systems
5. **GB2-specific features** should check game type before display
6. **Foreign key cascades** handle cleanup automatically
7. **Flow-based queries** ensure reactive UI updates
8. **Use `viewModelScope.launch`** for all coroutines in ViewModels
9. **Hoist state** to parent composables when multiple children need it
10. **Apply modifier parameter** at root of every reusable component

## Future Development Notes

- Multiplayer networking planned for Phase 3
- Campaign/Scenario system entities to be added
- Character portraits will be loaded from drawable resources
- Custom abilities per character to be defined
- Ghost trap token (GB2) UI pending implementation

## Quick Reference

### Get current level from XP
```kotlin
val level = Level.fromXp(character.xp)
```

### Check if proton stream is used
```kotlin
val isUsed = (character.protonStreamsUsed and (1 shl index)) != 0
```

### Toggle token bit
```kotlin
val newState = if (isUsed) {
    current and (1 shl index).inv() // Turn off
} else {
    current or (1 shl index) // Turn on
}
```

### Add ghost and gain XP
```kotlin
val newXp = (character.xp + ghost.rating).coerceIn(0, 30)
val updatedGhosts = character.trappedGhosts + ghost
character.copy(xp = newXp, trappedGhosts = updatedGhosts)
```

### Navigation with arguments
```kotlin
navController.navigate(Screen.Detail.createRoute(id))
```

---

**Last Updated**: December 9, 2025  
**Project Phase**: 1 - Core functionality complete  
**Next Focus**: Character abilities, portraits, and polish

