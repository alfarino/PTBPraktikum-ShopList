# 🎯 Cheat Sheet Presentasi - ShopList App

## 📋 Quick Reference untuk Presentasi 5-10 Menit

---

## 🗂️ 1. STRUKTUR PROYEK (30 detik)

```
MainActivity.kt          → Entry point (16 baris)
    ↓ memanggil
App.kt                   → UI Host + Navigasi
    ↓ menggunakan
MainViewModel.kt         → Data (shoppingItems)
    ↓ diteruskan ke
Home.kt                  → Halaman utama
    ↓ menggunakan
components/              → UI components reusable
    ├─ ShoppingList.kt   → LazyColumn (3 animasi!)
    ├─ ItemInput.kt      → TextField + Button
    ├─ SearchInput.kt    → Search field
    ├─ BottomNav.kt      → Bottom navigation
    └─ AppDrawer.kt      → Side drawer
```

**Script:**
> "Saya pakai clean architecture: MainActivity sebagai entry point, App.kt untuk navigasi, MainViewModel untuk data management, dan screens untuk setiap halaman. Semua UI components reusable dan terpisah."

---

## 🎯 2. FILE INTI (Harus Dijelaskan)

### A. MainActivity.kt (15 detik)
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {    // Tema orange kustom
                MainAppContent()   // UI dari App.kt
            }
        }
    }
}
```

**Highlight:**
- ✅ 16 baris saja (clean!)
- ✅ Single Responsibility: hanya entry point
- ✅ Delegasi ke App.kt

**Script:**
> "MainActivity sangat minimalis - cuma 16 baris. Tugasnya hanya setup tema orange dan memanggil MainAppContent. Ini mengikuti Single Responsibility Principle."

---

### B. MainViewModel.kt (30 detik)
```kotlin
class MainViewModel : ViewModel() {
    private val _items = mutableStateListOf(
        "Susu Segar", "Roti Tawar", ...
    )
    val shoppingItems: MutableList<String> get() = _items

    fun addNewItem(): String {
        val newItem = "Item ${_items.size + 1}"
        _items.add(newItem)
        return newItem
    }

    fun addItem(name: String) {
        if (name.isNotBlank()) _items.add(name)
    }
}
```

**Highlight:**
- ✅ `mutableStateListOf`: Observable list (auto recompose)
- ✅ Survive rotation
- ✅ Business logic di ViewModel (bukan di UI)

**Script:**
> "ViewModel menyimpan data shopping list. Saya pakai mutableStateListOf yang otomatis trigger recomposition. Keuntungannya: data survive rotation, dan business logic terpisah dari UI."

---

### C. App.kt - MainAppContent() (2 menit) ⭐ PENTING

**State Management:**
```kotlin
val vm: MainViewModel = viewModel()           // ViewModel
val shoppingItems = vm.shoppingItems           // Data
var currentRoute by rememberSaveable { ... }   // Navigasi
var currentDetailItem by rememberSaveable { ... } // Data passing
val drawerState = rememberDrawerState(...)     // Drawer state
val snackbarHostState = SnackbarHostState()    // Snackbar
```

**Script:**
> "Di App.kt, saya setup semua state: ViewModel untuk data, currentRoute untuk navigasi, drawerState untuk drawer, dan snackbarHostState untuk notification."

**Layout Structure:**
```
ModalNavigationDrawer              ← Drawer container
  └─ Scaffold                      ← Material Design layout
      ├─ TopAppBar                 ← Header dengan menu icon
      ├─ BottomNavigationBar       ← Home & Profile tabs
      ├─ FloatingActionButton      ← Tombol (+) tambah item
      ├─ SnackbarHost              ← Notification
      └─ AnimatedContent           ← Navigation dengan animasi
          ├─ HomeScreen
          ├─ ProfileScreen
          ├─ SettingScreen
          └─ DetailScreen
```

**Script:**
> "Untuk layout, saya pakai Scaffold dari Material Design 3. Ini punya slot untuk TopAppBar, BottomBar, FAB, dan Snackbar. Semuanya terorganisir rapi dalam satu struktur."

**Navigasi dengan Animasi:**
```kotlin
AnimatedContent(targetState = currentRoute, transitionSpec = {
    if (initialState == "home" && targetState == "detail") {
        // Home → Detail: slide kanan + fade
        slideInHorizontally { full -> full } + fadeIn() 
        with 
        slideOutHorizontally { full -> -full } + fadeOut()
    } else if (initialState == "detail" && targetState == "home") {
        // Detail → Home: slide kiri + fade  
        slideInHorizontally { full -> -full } + fadeIn()
        with
        slideOutHorizontally { full -> full } + fadeOut()
    } else {
        // Route lain: fade saja
        fadeIn() with fadeOut()
    }
})
```

**Script:**
> "Navigasi pakai AnimatedContent dengan 2 jenis animasi: untuk Home-Detail saya pakai slide horizontal + fade (300ms), untuk route lain cuma fade (200ms). Ini bikin transisi smooth dan menarik."

**Data Passing:**
```kotlin
else -> HomeScreen(
    shoppingItems = shoppingItems,      // Data dari ViewModel
    onItemClick = { item ->             // Callback
        currentDetailItem = item        // Simpan item
        currentRoute = "detail"         // Navigate
    }
)

"detail" -> DetailScreen(
    item = currentDetailItem ?: "",     // Pass data
    onBack = {
        currentDetailItem = null        // Clear
        currentRoute = Screen.Home.route // Back
    }
)
```

**Script:**
> "Untuk kirim data antar screen, saya simpan di currentDetailItem. Saat user klik item di Home, saya simpan item-nya, lalu navigate ke detail. DetailScreen terima data lewat parameter."

---

### D. Home.kt (1 menit)

**Filter Search:**
```kotlin
val filteredItems by remember(searchQuery, shoppingItems) {
    derivedStateOf {
        if (searchQuery.isBlank()) {
            shoppingItems
        } else {
            shoppingItems.filter { 
                it.contains(searchQuery, ignoreCase = true) 
            }
        }
    }
}
```

**Highlight:**
- ✅ `derivedStateOf`: Hanya recompute saat dependency berubah
- ✅ Performa optimal (tidak filter setiap recompose)

**Script:**
> "Untuk search, saya pakai derivedStateOf. Ini smart - hanya recompute saat searchQuery atau shoppingItems berubah. Jadi lebih efficient dari filter biasa."

**Composition:**
```
HomeScreen
├─ Title()           → Header "Shopping List"
├─ ItemInput()       → TextField + Button Add
├─ SearchInput()     → Search field
└─ ShoppingList()    → LazyColumn
```

**Script:**
> "HomeScreen adalah contoh component composition. Saya gabungkan komponen-komponen kecil: Title, ItemInput, SearchInput, dan ShoppingList. Semuanya reusable dan mudah di-maintain."

---

### E. ShoppingList.kt (1.5 menit) ⭐ HIGHLIGHT ANIMASI

**3 Jenis Animasi:**

**1. Color Animation (200ms tween):**
```kotlin
val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) 
        MaterialTheme.colorScheme.primaryContainer 
    else 
        MaterialTheme.colorScheme.surface,
    animationSpec = tween(200)
)
```
- Background, text, avatar berubah warna smooth

**2. Scale Animation (spring physics):**
```kotlin
val iconScale by animateFloatAsState(
    targetValue = if (isSelected) 1.15f else 1f,
    animationSpec = spring(stiffness = 400f)
)
```
- Icon scale dari 1.0 → 1.15 dengan bouncy effect

**3. Size Animation:**
```kotlin
Card(
    modifier = Modifier.animateContentSize()
)
```
- Card resize otomatis smooth

**Script:**
> "ShoppingList adalah komponen favorit saya karena ada 3 animasi. Pertama, color animation dengan tween 200ms untuk background dan text. Kedua, scale animation dengan spring physics untuk icon - ini yang bikin bouncy effect. Ketiga, animateContentSize untuk smooth resize."

**Pisah Click Behavior:**
```kotlin
Card(
    modifier = Modifier.clickable { onCardClick() }  // Navigate
) {
    Row {
        Text(...)  // Item name
        
        Surface(
            modifier = Modifier.clickable { 
                isSelected = !isSelected  // Toggle (tidak navigate)
            }
        ) {
            Icon(...)  // Check/Add icon
        }
    }
}
```

**Script:**
> "Yang unik: saya pisahkan click behavior. Klik card untuk navigate ke detail, klik icon untuk toggle selected. User punya 2 aksi berbeda - UX yang lebih baik."

---

### F. Profile.kt (45 detik)

**LazyColumn + Card:**
```kotlin
LazyColumn {
    item {
        Card {  // Header card
            AsyncImage(...)        // Foto profile (Coil)
            Text("Halo! 👋")      // Greeting
        }
    }
    
    items(profileItems) { info ->  // Loop biodata
        Card {
            Text(info.label)       // "Nama"
            Text(info.value)       // "Alfa Rino"
        }
    }
}
```

**Highlight:**
- ✅ LazyColumn (efficient scrolling)
- ✅ AsyncImage dari Coil (image loading library)
- ✅ Material Design Cards

**Script:**
> "Profile pakai LazyColumn untuk efficiency. Ada header card dengan foto (load pakai Coil library), terus loop biodata pakai items(). Semuanya wrapped dalam Card untuk Material Design look."

---

## 🎨 3. KOMPONEN UI (Sebutkan Cepat)

### ItemInput.kt
```kotlin
Row {
    OutlinedTextField(...)    // Input field
    Button(onClick = ...)     // Add button
}
```
- TextField + Button dalam Row
- Rounded corners (16dp)

### SearchInput.kt
```kotlin
OutlinedTextField(
    leadingIcon = { Icon(Icons.Default.Search) }
)
```
- Search icon di kiri
- String resources (i18n-ready)

### BottomNavigationBar.kt
```kotlin
NavigationBar {
    items.forEach { item ->
        NavigationBarItem(
            selected = selectedRoute == item.screen.route,
            onClick = { onItemSelected(item) },
            icon = { Icon(...) },
            label = { Text(...) }
        )
    }
}
```
- Loop bottomNavItems (Home & Profile)
- Auto highlight selected

### AppDrawer.kt
```kotlin
BoxWithConstraints {
    val drawerWidth = maxWidth * 0.66f  // 2/3 layar
    
    Surface(
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        NavigationDrawerItem(...)  // Setting menu
    }
}
```
- Responsive width (2/3 screen)
- Rounded corners kanan
- Setting menu item

---

## 🎯 4. MATERIAL DESIGN 3 (30 detik)

**Components Used:**
- ✅ Scaffold (layout structure)
- ✅ TopAppBar
- ✅ NavigationBar (bottom nav)
- ✅ ModalNavigationDrawer
- ✅ FloatingActionButton
- ✅ Snackbar
- ✅ Card (elevation, not shadow)
- ✅ OutlinedTextField
- ✅ Button

**Color System:**
```kotlin
MaterialTheme.colorScheme.primary           // Orange #FF9800
MaterialTheme.colorScheme.primaryContainer  // Light orange
MaterialTheme.colorScheme.surface           // White
MaterialTheme.colorScheme.background        // Cream
```

**Typography:**
```kotlin
MaterialTheme.typography.headlineLarge      // 32sp
MaterialTheme.typography.titleMedium        // 22sp
MaterialTheme.typography.bodyLarge          // 16sp
```

**Shapes:**
```kotlin
RoundedCornerShape(12.dp)   // Default
RoundedCornerShape(16.dp)   // TextField, Card
RoundedCornerShape(50)      // Circle avatar
```

**Script:**
> "Saya konsisten pakai Material Design 3: Scaffold untuk layout, NavigationBar untuk bottom nav, Card dengan elevation, dan color system orange. Typography dan shapes juga mengikuti Material Design guidelines."

---

## ⚡ 5. PERFORMANCE (30 detik)

**Optimizations:**
1. **LazyColumn** → Hanya render visible items
2. **derivedStateOf** → Smart recomputation
3. **rememberSaveable** → Survive rotation
4. **ViewModel** → Lifecycle-aware
5. **Card elevation** → Lighter than shadow (GPU friendly)

**Script:**
> "Untuk performa, saya pakai LazyColumn yang hanya render item visible. derivedStateOf untuk smart recomputation. ViewModel untuk lifecycle management. Dan Card elevation instead of shadow karena lebih ringan untuk GPU."

---

## 🎬 6. DEMO FLOW (1-2 menit)

1. **Home Screen**
   - Show list items
   - Ketik di search → filter real-time
   - Input item baru → klik Add
   - Klik icon → toggle selected (animasi color + scale)
   - Klik card → navigate ke Detail (animasi slide)

2. **Detail Screen**
   - Show item name
   - Animasi slide in from bottom
   - Klik Back → kembali ke Home

3. **Bottom Navigation**
   - Klik Profile tab
   - Show profile card + biodata list
   - Scroll smooth (LazyColumn)

4. **Drawer**
   - Buka drawer (swipe atau tap hamburger)
   - Klik Setting
   - Toggle switches (Notifikasi, Dark Mode)

5. **FAB**
   - Klik FAB (+)
   - Show Snackbar "Menambahkan: Item X"
   - List auto update

---

## 📊 7. ALUR DATA (Diagram)

```
USER CLICK FAB
    ↓
App.kt: vm.addNewItem()
    ↓
MainViewModel: _items.add("Item X")
    ↓
Compose: Detect state change
    ↓
HomeScreen: Recompose
    ↓
ShoppingList: Show new item
```

```
USER CLICK ITEM CARD
    ↓
ShoppingListItem: onCardClick()
    ↓
ShoppingList: onItemClick(item)
    ↓
HomeScreen: onItemClick(item)
    ↓
App.kt: currentDetailItem = item
        currentRoute = "detail"
    ↓
AnimatedContent: Slide animation (300ms)
    ↓
DetailScreen: Show with data
```

---

## 🎯 8. KEY TAKEAWAYS (Penutup 30 detik)

**Architecture:**
- ✅ Clean separation: MainActivity → App → ViewModel → Screens → Components
- ✅ Unidirectional data flow: State turun, events naik
- ✅ Single source of truth: ViewModel

**UI/UX:**
- ✅ Material Design 3 consistent
- ✅ 3 jenis animasi (color, scale, size)
- ✅ Smooth transitions (slide + fade)
- ✅ Responsive layout

**Performance:**
- ✅ LazyColumn (efficient)
- ✅ derivedStateOf (smart)
- ✅ ViewModel (lifecycle-aware)

**Script:**
> "Kesimpulannya, aplikasi ini demonstrasikan clean architecture, Material Design 3, animasi yang smooth, dan performance optimization. Semua component reusable dan mudah di-maintain."

---

## 💡 TIPS PRESENTASI

### DO's:
✅ Tunjukkan kode saat explain fungsi spesifik
✅ Demo animasi secara live
✅ Highlight design decisions (kenapa LazyColumn, kenapa ViewModel)
✅ Sebutkan performa benefits

### DON'Ts:
❌ Jangan baca kode baris per baris
❌ Jangan skip demo live
❌ Jangan lupa sebut Material Design 3
❌ Jangan abaikan animasi (ini highlight!)

### Time Management:
- **Intro + Struktur**: 1 min
- **File Inti (MainActivity, ViewModel, App)**: 3 min
- **Components (Home, ShoppingList, Profile)**: 2 min
- **Material Design + Performance**: 1 min
- **Demo Live**: 2 min
- **Q&A Buffer**: 1 min

**Total: 10 menit**

---

## 🚀 ONE-LINER per File (Backup)

| File | One-Liner |
|------|-----------|
| MainActivity.kt | "Entry point yang minimalis, hanya 16 baris" |
| MainViewModel.kt | "Data management dengan mutableStateListOf yang observable" |
| App.kt | "Central hub: Scaffold + navigasi + animasi" |
| Navigation.kt | "Type-safe navigation dengan sealed class" |
| Home.kt | "Component composition dengan derivedStateOf untuk filter" |
| Profile.kt | "LazyColumn + Card + AsyncImage untuk biodata" |
| Setting.kt | "Toggle switches untuk Notifikasi dan Dark Mode" |
| Detail.kt | "AnimatedVisibility dengan slide + fade" |
| ShoppingList.kt | "LazyColumn dengan 3 animasi dan pisah click behavior" |
| ItemInput.kt | "TextField + Button dalam Row" |
| SearchInput.kt | "OutlinedTextField dengan search icon" |
| BottomNav.kt | "NavigationBar untuk Home & Profile" |
| AppDrawer.kt | "ModalDrawer responsive dengan rounded corners" |
| Theme.kt | "Material 3 orange theme dengan Light/Dark mode" |

---

**Good luck dengan presentasinya! 🎉🚀**
