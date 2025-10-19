# 🎨 Diagram Interaksi File ShopList

## 1. Alur Data dan Kontrol

```
┌─────────────────────────────────────────────────────────────────────┐
│                        ANDROID SYSTEM                                │
│                    (Lifecycle Management)                            │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│  MainActivity.kt                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │  onCreate() {                                                  │  │
│  │      setContent {                                              │  │
│  │          ShoppingListTheme { ◄──────────────────┐            │  │
│  │              MainAppContent()                    │            │  │
│  │          }                                       │            │  │
│  │      }                                           │            │  │
│  │  }                                               │            │  │
│  └───────────────────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────────────────┘
                                 │ memanggil
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│  ui/theme/Theme.kt                                                   │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │  ShoppingListTheme(                                            │  │
│  │      colorScheme = LightColorScheme / DarkColorScheme          │  │
│  │      typography = Typography  ◄─── Type.kt                    │  │
│  │      shapes = Shapes          ◄─── Shape.kt                   │  │
│  │  )                            ◄─── Color.kt                   │  │
│  └───────────────────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────────────────┘
                                 │ membungkus
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│  App.kt - MainAppContent()                                           │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │  val vm: MainViewModel = viewModel() ◄──┐                     │  │
│  │  val shoppingItems = vm.shoppingItems   │                     │  │
│  │                                          │                     │  │
│  │  var currentRoute = "home"              │                     │  │
│  │  var currentDetailItem = null           │                     │  │
│  │                                          │                     │  │
│  │  ModalNavigationDrawer(                 │                     │  │
│  │      drawerContent = { AppDrawer() }    │                     │  │
│  │  ) {                                     │                     │  │
│  │      Scaffold(                           │                     │  │
│  │          topBar = { TopAppBar() }        │                     │  │
│  │          bottomBar = { BottomNavBar() }  │                     │  │
│  │          floatingActionButton = { FAB() }│                     │  │
│  │          snackbarHost = { Snackbar() }   │                     │  │
│  │      ) {                                 │                     │  │
│  │          AnimatedContent(currentRoute) {  │                    │  │
│  │              when (route) {               │                    │  │
│  │                  "home" → HomeScreen()    │                    │  │
│  │                  "profile" → ProfileScreen()                  │  │
│  │                  "setting" → SettingScreen()                  │  │
│  │                  "detail" → DetailScreen()                    │  │
│  │              }                            │                    │  │
│  │          }                                │                    │  │
│  │      }                                    │                    │  │
│  │  }                                        │                    │  │
│  └───────────────────────────────────────────────────────────────┘  │
└───┬─────────────┬──────────────┬─────────────┬──────────────────────┘
    │             │              │             │
    │             │              │             └───────┐
    │             │              │                     │
    │             │              │                     ▼
    │             │              │      ┌──────────────────────────────┐
    │             │              │      │  MainViewModel.kt            │
    │             │              │      │  ┌────────────────────────┐  │
    │             │              │      │  │ shoppingItems          │  │
    │             │              │      │  │   = mutableStateListOf │  │
    │             │              │      │  │                         │  │
    │             │              │      │  │ addNewItem()           │  │
    │             │              │      │  │ addItem(name)          │  │
    │             │              │      │  └────────────────────────┘  │
    │             │              │      └──────────────────────────────┘
    │             │              │
    ▼             ▼              ▼
┌─────────┐  ┌──────────┐  ┌───────────┐
│ Home.kt │  │Profile.kt│  │Setting.kt │
└────┬────┘  └──────────┘  └───────────┘
     │
     │ menggunakan
     │
     ▼
┌─────────────────────────────────────────────────────────────────┐
│  components/                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ShoppingList  │  │ItemInput     │  │SearchInput          │  │
│  │  .kt         │  │  .kt         │  │  .kt                │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │BottomNavBar  │  │AppDrawer     │  │Title.kt             │  │
│  │  .kt         │  │  .kt         │  │                      │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Flow Navigasi User

```
APP START
    │
    ▼
┌─────────────────────────────────────────┐
│           HOME SCREEN                    │
│  ┌─────────────────────────────────┐   │
│  │ [☰ Menu]   Shopping List    [≡]│   │  ← TopAppBar
│  ├─────────────────────────────────┤   │
│  │ [Input Field]        [+ Add]   │   │  ← ItemInput
│  │ [🔍 Search Field]               │   │  ← SearchInput
│  │                                  │   │
│  │ ┌────────────────────────────┐ │   │
│  │ │ [S] Susu Segar        [+] │ │   │  ← ShoppingListItem
│  │ └────────────────────────────┘ │   │
│  │ ┌────────────────────────────┐ │   │     LazyColumn
│  │ │ [R] Roti Tawar        [+] │ │   │  ← ShoppingListItem
│  │ └────────────────────────────┘ │   │
│  │ ┌────────────────────────────┐ │   │
│  │ │ [T] Telur Ayam        [+] │ │   │  ← ShoppingListItem
│  │ └────────────────────────────┘ │   │
│  ├─────────────────────────────────┤   │
│  │    [Home]     [Profile]         │   │  ← BottomNavigationBar
│  └─────────────────────────────────┘   │
│                    [+] FAB              │  ← FloatingActionButton
└─────────────────────────────────────────┘
    │
    ├──────────────┬──────────────┬────────────┬──────────────┐
    │              │              │            │              │
    ▼              ▼              ▼            ▼              ▼
[Klik Card]  [Klik Icon]   [Bottom Nav]  [FAB Click]   [☰ Menu]
    │              │              │            │              │
    ▼              ▼              ▼            ▼              ▼
DETAIL       Toggle       PROFILE     Add Item      DRAWER
SCREEN       Select       SCREEN      Snackbar      MENU
    │                                                  │
    │                                                  ▼
    │                                            SETTING SCREEN
    │                                                  │
    └──────────── [Back Button] ─────────────────────┘
                        │
                        ▼
                   HOME SCREEN
```

---

## 3. State Management Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                     MainViewModel                                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  private val _items = mutableStateListOf(...)             │  │
│  │                                                             │  │
│  │  val shoppingItems: MutableList<String> get() = _items    │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────┬───────────────────────────────┬────────────────┘
                 │                               │
        ┌────────┴────────┐             ┌────────┴────────┐
        │ Read Access     │             │ Write Access    │
        ▼                 │             ▼                 │
┌────────────────┐        │     ┌────────────────┐       │
│  App.kt        │        │     │  App.kt        │       │
│  val items =   │        │     │  FAB.onClick { │       │
│  vm.shoppingItems       │     │    vm.addNewItem()     │
└───────┬────────┘        │     └────────────────┘       │
        │                 │                               │
        │ pass down       │                               │
        ▼                 │                               │
┌────────────────┐        │                               │
│  HomeScreen    │        │                               │
│  (shoppingItems)        │                               │
└───────┬────────┘        │                               │
        │                 │                               │
        │ pass down       │                               │
        ▼                 │                               │
┌────────────────┐        │                               │
│  ShoppingList  │        │                               │
│  (items)       │        │                               │
└───────┬────────┘        │                               │
        │                 │                               │
        │ render          │                               │
        ▼                 │                               │
┌────────────────┐        │                               │
│ShoppingListItem│        │                               │
│  (item)        │        │                               │
└────────────────┘        │                               │
                          │                               │
                ┌─────────┴───────────────────────────────┘
                │
                │ State update triggers recomposition
                │
                ▼
        ┌───────────────────┐
        │  UI Recomposition │
        │  (Only affected   │
        │   composables)    │
        └───────────────────┘
```

---

## 4. Animasi dan Transisi

```
┌─────────────────────────────────────────────────────────────────┐
│                    ANIMASI DALAM APLIKASI                        │
└─────────────────────────────────────────────────────────────────┘

1. TRANSISI ANTAR LAYAR (App.kt - AnimatedContent)
   
   Home Screen                    Detail Screen
   ┌──────────┐                  ┌──────────┐
   │          │  slideIn(→)      │          │
   │  [Item]  │  ───────────►    │  Detail  │
   │  [Item]  │  fadeIn()        │  Content │
   │          │                  │          │
   └──────────┘                  └──────────┘
                ◄───────────
                slideOut(←)
                fadeOut()

   Kode:
   slideInHorizontally { full -> full } + fadeIn()
       with
   slideOutHorizontally { full -> -full } + fadeOut()

─────────────────────────────────────────────────────────────────

2. ANIMASI ITEM (ShoppingList.kt)

   A. Warna Background (animateColorAsState)
      
      Not Selected                Selected
      ┌──────────────┐          ┌──────────────┐
      │   Surface    │  ──────► │ PrimaryContainer
      │   Color      │ (200ms)  │   Color      │
      └──────────────┘          └──────────────┘

   B. Icon Scale (animateFloatAsState)
      
      Normal                    Selected
        [+]        ──────►        [✓]
       size 1.0    spring       size 1.15
                   (400 stiffness)

   C. Content Size (animateContentSize)
      
      ┌─────────┐              ┌──────────────┐
      │ Normal  │  ──────►     │   Expanded   │
      │  Size   │  smooth      │     Size     │
      └─────────┘              └──────────────┘

─────────────────────────────────────────────────────────────────

3. DETAIL SCREEN (Detail.kt - AnimatedVisibility)

   Enter Animation:
      ▼ slideInVertically (dari bawah 1/4 layar)
      ▼ fadeIn
      
   Exit Animation:
      ▼ slideOutVertically (ke bawah 1/4 layar)
      ▼ fadeOut

─────────────────────────────────────────────────────────────────

4. TIMING SPECIFICATIONS

   │ Animation         │ Duration │ Easing       │
   ├───────────────────┼──────────┼──────────────┤
   │ Screen transition │ 300ms    │ tween        │
   │ Color change      │ 200ms    │ tween        │
   │ Icon scale        │ dynamic  │ spring(400)  │
   │ Content size      │ dynamic  │ spring       │
```

---

## 5. Data Flow untuk Navigation

```
┌─────────────────────────────────────────────────────────────────┐
│           NAVIGATION & DATA PASSING FLOW                         │
└─────────────────────────────────────────────────────────────────┘

SCENARIO 1: Bottom Navigation (Home ↔ Profile)

   User taps "Profile"
        │
        ▼
   BottomNavigationBar.kt
        │
        │ onItemSelected(BottomNavItem)
        ▼
   App.kt - MainAppContent()
        │
        │ currentRoute = selected.screen.route
        │              ("profile")
        ▼
   AnimatedContent recompose with "profile"
        │
        ▼
   ProfileScreen() rendered

─────────────────────────────────────────────────────────────────

SCENARIO 2: Drawer Navigation (Any → Setting)

   User opens drawer and taps "Setting"
        │
        ▼
   AppDrawer.kt
        │
        │ onDestinationClick(Screen.Setting)
        ▼
   App.kt - MainAppContent()
        │
        │ currentRoute = destination.route
        │              ("setting")
        │ drawerState.close()
        ▼
   AnimatedContent recompose with "setting"
        │
        ▼
   SettingScreen() rendered

─────────────────────────────────────────────────────────────────

SCENARIO 3: Item Click with Data Passing (Home → Detail)

   User clicks card in ShoppingList
        │
        ▼
   ShoppingListItem.kt
        │
        │ onCardClick()
        ▼
   ShoppingList.kt
        │
        │ onItemClick(item: "Susu Segar")
        ▼
   HomeScreen.kt
        │
        │ onItemClick("Susu Segar")
        ▼
   App.kt - MainAppContent()
        │
        │ currentDetailItem = "Susu Segar"  ◄── DATA STORED
        │ currentRoute = "detail"
        ▼
   AnimatedContent recompose with "detail"
        │
        ▼
   DetailScreen(
       item = currentDetailItem ?: "",     ◄── DATA PASSED
       onBack = {
           currentDetailItem = null
           currentRoute = Screen.Home.route
       }
   )

─────────────────────────────────────────────────────────────────

SCENARIO 4: FAB Click (Add Item)

   User clicks FAB
        │
        ▼
   FloatingActionButton.onClick
        │
        ▼
   App.kt - MainAppContent()
        │
        │ val newItem = vm.addNewItem()
        ▼
   MainViewModel
        │
        │ _items.add("Item ${size + 1}")
        │ return "Item X"
        ▼
   App.kt
        │
        │ snackbarHostState.showSnackbar("Menambahkan: Item X")
        ▼
   Snackbar displayed
        │
        ▼
   HomeScreen automatically recomposes
   (because shoppingItems is observed state)
        │
        ▼
   ShoppingList shows new item
```

---

## 6. Material Design Component Mapping

```
┌─────────────────────────────────────────────────────────────────┐
│                    MATERIAL DESIGN 3 USAGE                       │
└─────────────────────────────────────────────────────────────────┘

MainActivity
    └─ ShoppingListTheme ◄───────── ui/theme/
           │
           └─ MainAppContent (App.kt)
                  │
                  └─ ModalNavigationDrawer ◄── components/AppDrawer.kt
                         │
                         └─ Scaffold (M3 Layout Container)
                                │
                                ├─ topBar: TopAppBar
                                │         └─ IconButton + Icon
                                │
                                ├─ bottomBar: NavigationBar
                                │            └─ NavigationBarItem
                                │
                                ├─ floatingActionButton: FAB
                                │                        └─ Icon
                                │
                                ├─ snackbarHost: SnackbarHost
                                │
                                └─ content: AnimatedContent
                                           │
                                           ├─ HomeScreen
                                           │    ├─ LazyColumn
                                           │    └─ Card items
                                           │
                                           ├─ ProfileScreen
                                           │    └─ LazyColumn + Cards
                                           │
                                           ├─ SettingScreen
                                           │    └─ Card + Switch
                                           │
                                           └─ DetailScreen
                                                └─ AnimatedVisibility

┌─────────────────────────────────────────────────────────────────┐
│  MATERIAL DESIGN PRINCIPLES APPLIED:                            │
│                                                                  │
│  ✓ Elevation System: Card elevations (2dp, 4dp, 6dp, 8dp)     │
│  ✓ Color Roles: primary, onPrimary, surface, background, etc.  │
│  ✓ Typography Scale: headlineLarge, titleMedium, bodyMedium    │
│  ✓ Shape System: RoundedCornerShape(12dp, 16dp)                │
│  ✓ Spacing: 8dp grid (8, 16, 24, 32)                           │
│  ✓ State Layers: Selected, hover, pressed states               │
│  ✓ Motion: Enter/exit animations with spring/tween             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 7. Kesimpulan Interaksi

### Flow Utama Aplikasi:
1. **MainActivity** → Entry point, memanggil `MainAppContent()`
2. **App.kt** → Central hub untuk navigasi dan state
3. **MainViewModel** → Data persistence dan business logic
4. **Navigation.kt** → Route definitions
5. **Screen files** → UI untuk setiap halaman
6. **Components** → Reusable UI pieces
7. **Theme** → Consistent styling

### Prinsip Desain:
- **Unidirectional Data Flow**: State turun, events naik
- **Single Source of Truth**: ViewModel holds data
- **Composition over Inheritance**: Small composables
- **Separation of Concerns**: UI, logic, navigation terpisah
