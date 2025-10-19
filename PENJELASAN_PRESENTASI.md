# 📱 Penjelasan Aplikasi ShopList - Presentasi

## 📋 Daftar Isi
1. [Gambaran Umum Aplikasi](#gambaran-umum-aplikasi)
2. [Struktur Proyek dan Arsitektur](#struktur-proyek-dan-arsitektur)
3. [Penjelasan File dan Interaksinya](#penjelasan-file-dan-interaksinya)
4. [Pemenuhan Ketentuan Aplikasi](#pemenuhan-ketentuan-aplikasi)
5. [Refleksi Penggunaan AI](#refleksi-penggunaan-ai)
6. [Demo Flow](#demo-flow)

---

## 🎯 Gambaran Umum Aplikasi

**ShopList** adalah aplikasi shopping list modern berbasis Jetpack Compose dengan fitur:
- ✅ Menampilkan daftar belanja dengan LazyColumn
- ✅ Navigasi antar layar (Home, Profile, Setting, Detail)
- ✅ Animasi transisi antar layar dan animasi komponen
- ✅ Material Design 3 dengan tema orange kustom
- ✅ Bottom Navigation + Navigation Drawer
- ✅ FloatingActionButton, Snackbar, Cards, dan komponen Material

---

## 🏗️ Struktur Proyek dan Arsitektur

### Diagram Alur Aplikasi
```
MainActivity.kt (Entry Point)
    ↓ memanggil
App.kt (MainAppContent - UI Host)
    ↓ menampilkan
┌─────────────────────────────────────┐
│  Scaffold (Material Design Layout)  │
│  ├─ TopAppBar                        │
│  ├─ BottomNavigationBar              │
│  ├─ FloatingActionButton             │
│  ├─ ModalNavigationDrawer            │
│  └─ Content Area (Navigasi)          │
└─────────────────────────────────────┘
    ↓ navigasi ke
┌────────────────┬────────────────┬────────────────┬────────────────┐
│   Home.kt      │  Profile.kt    │  Setting.kt    │   Detail.kt    │
│  (Halaman      │  (Halaman      │  (Halaman      │  (Halaman      │
│   Utama)       │   Profil)      │   Pengaturan)  │   Detail Item) │
└────────────────┴────────────────┴────────────────┴────────────────┘
    ↓ menggunakan komponen dari
┌──────────────────────────────────────────────────────────────────┐
│            components/ (Komponen UI Reusable)                     │
│  ├─ ShoppingList.kt (LazyColumn + ShoppingListItem)              │
│  ├─ ItemInput.kt (TextField + Button Add)                        │
│  ├─ SearchInput.kt (Search TextField)                            │
│  ├─ BottomNavigationBar.kt (Bottom Nav)                          │
│  ├─ AppDrawer.kt (Navigation Drawer)                             │
│  └─ Title.kt (Header Title)                                      │
└──────────────────────────────────────────────────────────────────┘
    ↓ menggunakan data dari
┌──────────────────────────────────────────────────────────────────┐
│  MainViewModel.kt (State Management)                              │
│  └─ mutableStateListOf<String> (Shopping Items Data)             │
└──────────────────────────────────────────────────────────────────┘
    ↓ menggunakan tema dari
┌──────────────────────────────────────────────────────────────────┐
│  ui/theme/ (Material Theme Configuration)                         │
│  ├─ Theme.kt (LightColorScheme + DarkColorScheme)                │
│  ├─ Color.kt (Orange Custom Colors)                              │
│  ├─ Type.kt (Typography)                                         │
│  └─ Shape.kt (Rounded Corners)                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📄 Penjelasan File dan Interaksinya

### 1️⃣ **MainActivity.kt** - Entry Point
**Peran:** Entry point aplikasi Android

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {  // Menerapkan tema custom
                MainAppContent()  // Memanggil UI utama
            }
        }
    }
}
```

**Fungsi:**
- Hanya bertugas memanggil `MainAppContent()` dari `App.kt`
- Membungkus aplikasi dengan tema `ShoppingListTheme`
- Mengikuti prinsip **Single Responsibility** - hanya mengatur lifecycle Activity

---

### 2️⃣ **App.kt** - UI Host & Navigation Logic
**Peran:** Mengatur layout utama, navigasi, dan state management

**Komponen Utama:**

#### A. State Management
```kotlin
// ViewModel untuk data persistence
val vm: MainViewModel = viewModel()
val shoppingItems = vm.shoppingItems

// State navigasi (disimpan saat rotation)
var currentRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
var currentDetailItem by rememberSaveable { mutableStateOf<String?>(null) }

// State UI
val drawerState = rememberDrawerState(DrawerValue.Closed)
val snackbarHostState = SnackbarHostState()
```

#### B. Layout Structure (Scaffold)
```kotlin
Scaffold(
    topBar = { TopAppBar(...) },           // Header dengan tombol menu
    bottomBar = { BottomNavigationBar(...) }, // Bottom nav (Home/Profile)
    floatingActionButton = { FAB(...) },    // Tombol tambah item
    snackbarHost = { SnackbarHost(...) }    // Notifikasi
) { ... }
```

#### C. Navigation Logic (AnimatedContent)
```kotlin
AnimatedContent(targetState = currentRoute, transitionSpec = {
    // Animasi slide+fade untuk detail
    if (initialState == Screen.Home.route && targetState == "detail") {
        slideInHorizontally(...) + fadeIn() with 
            slideOutHorizontally(...) + fadeOut()
    } else {
        fadeIn() with fadeOut()  // Animasi fade untuk layar lain
    }
}) { route ->
    when (route) {
        Screen.Home.route -> HomeScreen(...)
        Screen.Profile.route -> ProfileScreen()
        Screen.Setting.route -> SettingScreen()
        "detail" -> DetailScreen(...)
    }
}
```

**Interaksi dengan File Lain:**
- ✅ Memanggil `MainViewModel` untuk data
- ✅ Memanggil komponen `BottomNavigationBar` dan `AppDrawer`
- ✅ Merender halaman `HomeScreen`, `ProfileScreen`, `SettingScreen`, `DetailScreen`

---

### 3️⃣ **Navigation.kt** - Navigation Data Model
**Peran:** Mendefinisikan struktur navigasi

```kotlin
// Sealed class untuk route yang type-safe
sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Profile : Screen("profile", "Profile")
    data object Setting : Screen("setting", "Setting")
}

// Data class untuk bottom navigation items
data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

// List bottom nav items
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, "Home"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, "Profile")
)
```

**Keuntungan:**
- Type-safe navigation (tidak ada typo string)
- Single source of truth untuk route dan metadata
- Mudah menambah halaman baru

---

### 4️⃣ **MainViewModel.kt** - State Management
**Peran:** Mengelola data shopping list dengan lifecycle-aware

```kotlin
class MainViewModel : ViewModel() {
    private val _items = mutableStateListOf(
        "Susu Segar",
        "Roti Tawar",
        "Telur Ayam",
        "Apel Fuji",
        "Daging Sapi"
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

**Keuntungan:**
- ✅ Data tidak hilang saat rotation
- ✅ Mengurangi recomposition
- ✅ Memindahkan business logic dari UI

---

### 5️⃣ **Home.kt** - Halaman Utama
**Peran:** Tampilan utama untuk mengelola shopping list

**Komponen:**
```kotlin
@Composable
fun HomeScreen(
    shoppingItems: MutableList<String>,
    onItemClick: (String) -> Unit
) {
    Column {
        Title()                    // Judul "Shopping List"
        ItemInput(...)            // Input + tombol Add
        SearchInput(...)          // Search bar dengan filter
        ShoppingList(...)         // LazyColumn daftar item
    }
}
```

**Fitur:**
1. **Input Item** - `ItemInput` untuk menambah item baru
2. **Search** - `SearchInput` dengan filter real-time menggunakan `derivedStateOf`
3. **List** - `ShoppingList` menampilkan hasil filter
4. **Navigation** - Klik item → navigasi ke `DetailScreen`

**Interaksi:**
- Menerima `shoppingItems` dari ViewModel (via `App.kt`)
- Memanggil komponen `ItemInput`, `SearchInput`, `ShoppingList`, `Title`
- Mengirim callback `onItemClick` ke parent untuk navigasi

---

### 6️⃣ **Profile.kt** - Halaman Profil
**Peran:** Menampilkan biodata dengan Card dan LazyColumn

**Struktur:**
```kotlin
@Composable
fun ProfileScreen() {
    val profileItems = listOf(
        ProfileInfo("Nama", "Alfa Rino Svedrilio"),
        ProfileInfo("NIM", "2311522005"),
        ...
    )
    
    LazyColumn {
        // Card pertama: Foto + Greeting
        item {
            Card {
                AsyncImage(model = R.drawable.profile_picture, ...)
                Text("Halo! 👋")
            }
        }
        
        // Cards untuk setiap info
        items(profileItems) { info ->
            Card {
                Text(info.label)
                Text(info.value)
            }
        }
    }
}
```

**Pemenuhan Ketentuan:**
- ✅ LazyColumn untuk list biodata
- ✅ Card Material Design
- ✅ AsyncImage dari Coil (loading gambar efisien)
- ✅ String resources (`stringResource()`)

---

### 7️⃣ **Setting.kt** - Halaman Pengaturan
**Peran:** Halaman pengaturan dengan toggle switches

**Fitur:**
```kotlin
@Composable
fun SettingScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkThemeEnabled by remember { mutableStateOf(false) }
    
    Column {
        Text("Ini halaman Pengaturan")
        Card {
            SettingToggleRow(
                title = "Notifikasi",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            SettingToggleRow(
                title = "Mode Gelap",
                checked = darkThemeEnabled,
                onCheckedChange = { darkThemeEnabled = it }
            )
            Button("Simpan Pengaturan")
        }
    }
}
```

---

### 8️⃣ **Detail.kt** - Halaman Detail Item
**Peran:** Menampilkan detail item dengan animasi

**Fitur Animasi:**
```kotlin
@Composable
fun DetailScreen(item: String, onBack: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),  // Masuk dari bawah + fade
        exit = slideOutVertically() + fadeOut()   // Keluar ke bawah + fade
    ) {
        Column {
            Text("Detail Item")
            Text(item)
            Button("Kembali", onClick = onBack)
        }
    }
}
```

**Interaksi:**
- Dipanggil dari `App.kt` saat `currentRoute == "detail"`
- Menerima `item` yang diklik dari `HomeScreen`
- Callback `onBack()` untuk kembali ke Home

---

### 9️⃣ **components/ShoppingList.kt** - List Component
**Peran:** Komponen LazyColumn dengan item yang interaktif

#### A. ShoppingList - LazyColumn Container
```kotlin
@Composable
fun ShoppingList(items: List<String>, onItemClick: (String) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it }) { item ->
            ShoppingListItem(
                item = item,
                onCardClick = { onItemClick(item) }
            )
        }
    }
}
```

#### B. ShoppingListItem - Item dengan Animasi
```kotlin
@Composable
fun ShoppingListItem(item: String, onCardClick: () -> Unit) {
    var isSelected by remember { mutableStateOf(false) }
    
    // Animasi warna background
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) primaryContainer else surface,
        animationSpec = tween(200)
    )
    
    // Animasi warna konten
    val contentColor by animateColorAsState(...)
    
    Card(
        modifier = Modifier.clickable { onCardClick() }  // Klik card → detail
            .animateContentSize(),  // Animasi ukuran smooth
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row {
            // Avatar dengan huruf pertama
            Card { Text(item.firstOrNull()?.uppercase()) }
            
            // Nama item
            Text(item)
            
            // Tombol check/add (klik terpisah dari card)
            Surface(
                modifier = Modifier.clickable { isSelected = !isSelected }
            ) {
                Icon(if (isSelected) Check else Add)
            }
        }
    }
}
```

**Fitur Animasi:**
1. **animateColorAsState** - Warna background dan text berubah smooth
2. **animateFloatAsState** - Icon scale saat selected
3. **animateContentSize** - Card resize smooth
4. **Pisah Click** - Klik card (navigasi) vs klik icon (toggle select)

---

### 🔟 **components/ItemInput.kt** - Input Component
```kotlin
@Composable
fun ItemInput(text: String, onTextChange: (String) -> Unit, onAddItem: () -> Unit) {
    Row {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Add new item") },
            shape = RoundedCornerShape(16.dp)  // Material Design rounded
        )
        Button(onClick = onAddItem) {
            Icon(Icons.Default.Add)
            Text("Add")
        }
    }
}
```

---

### 1️⃣1️⃣ **components/SearchInput.kt** - Search Component
```kotlin
@Composable
fun SearchInput(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.search_items)) },
        leadingIcon = { Icon(Icons.Default.Search) },
        shape = RoundedCornerShape(16.dp)
    )
}
```

---

### 1️⃣2️⃣ **components/BottomNavigationBar.kt** - Bottom Nav
```kotlin
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.screen.route,
                onClick = { onItemSelected(item) },
                icon = { Icon(item.icon) },
                label = { Text(item.label) }
            )
        }
    }
}
```

---

### 1️⃣3️⃣ **components/AppDrawer.kt** - Navigation Drawer
```kotlin
@Composable
fun AppDrawer(selectedRoute: String, onDestinationClick: (Screen) -> Unit) {
    BoxWithConstraints {
        val drawerWidth: Dp = maxWidth * 0.66f  // 2/3 layar
        
        Surface(
            modifier = Modifier.width(drawerWidth),
            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
        ) {
            Column {
                Text("Menu")
                NavigationDrawerItem(
                    label = { Text(Screen.Setting.title) },
                    selected = selectedRoute == Screen.Setting.route,
                    onClick = { onDestinationClick(Screen.Setting) }
                )
            }
        }
    }
}
```

---

### 1️⃣4️⃣ **ui/theme/** - Material Theme
**Peran:** Konfigurasi tema orange kustom

#### Theme.kt
```kotlin
private val LightColorScheme = lightColorScheme(
    primary = OrangeLight,           // #FF9800
    onPrimary = OrangeOn,            // White
    primaryContainer = OrangePrimaryContainer,  // #FFCC80
    background = OrangeBackground,   // #FFF8F5
    ...
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,            // #F57C00
    surface = OrangeSurfaceDark,     // #2B2930
    ...
)

@Composable
fun ShoppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Shapes
    )
}
```

---

## ✅ Pemenuhan Ketentuan Aplikasi

### 1. ✅ LazyColumn
**Implementasi:**
- `Profile.kt` → LazyColumn untuk daftar biodata
- `ShoppingList.kt` → LazyColumn untuk daftar belanja
- Menggunakan `items()` dengan `key` untuk performa optimal

### 2. ✅ Material Design Components
| Komponen | Lokasi | Fungsi |
|----------|---------|--------|
| **Scaffold** | `App.kt` | Layout utama dengan TopBar, BottomBar, FAB |
| **Card** | Semua halaman | Container untuk konten |
| **FloatingActionButton** | `App.kt` | Tombol tambah item cepat |
| **Snackbar** | `App.kt` | Notifikasi "Menambahkan: Item X" |
| **Button** | `ItemInput.kt`, `Setting.kt` | Aksi user |
| **NavigationBar** | `BottomNavigationBar.kt` | Bottom navigation |
| **ModalNavigationDrawer** | `App.kt` | Side drawer untuk Setting |
| **TopAppBar** | `App.kt` | Header dengan menu icon |

### 3. ✅ Navigasi Antar Layar + Pengiriman Data
**Flow Navigasi:**
```
Home → (klik item) → Detail (menerima item name)
        ↓
   Bottom Nav
        ↓
   Home ↔ Profile

Drawer → Setting
```

**Implementasi:**
```kotlin
// Di App.kt
var currentRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
var currentDetailItem by rememberSaveable { mutableStateOf<String?>(null) }

// Navigasi ke detail dengan data
HomeScreen(onItemClick = { item ->
    currentDetailItem = item        // Simpan data item
    currentRoute = "detail"         // Navigate ke detail
})

// Detail menerima data
DetailScreen(item = currentDetailItem ?: "", onBack = {
    currentDetailItem = null
    currentRoute = Screen.Home.route
})
```

### 4. ✅ Animasi
| Jenis Animasi | Lokasi | Implementasi |
|---------------|--------|--------------|
| **Transisi layar** | `App.kt` | `slideInHorizontally + fadeIn` untuk Home→Detail |
| **Animasi warna** | `ShoppingList.kt` | `animateColorAsState` untuk background card |
| **Animasi ukuran** | `ShoppingList.kt` | `animateContentSize` pada card |
| **Animasi scale** | `ShoppingList.kt` | `animateFloatAsState` untuk icon |
| **Fade in/out** | `Detail.kt` | `AnimatedVisibility` dengan slide+fade |

### 5. ✅ Tema Kustom
**Orange Theme dengan:**
- Light mode: Orange #FF9800
- Dark mode: Orange #F57C00
- Custom shapes: RoundedCornerShape(12.dp)
- Custom typography

---

## 🤖 Refleksi Penggunaan AI

### Bantuan yang Diterima dari AI (GitHub Copilot)

#### 1. **Arsitektur dan Struktur**
**Yang dibantu AI:**
- ✅ Pemisahan concerns: `MainActivity` → `App.kt` → `Navigation.kt`
- ✅ Implementasi MVVM dengan `MainViewModel`
- ✅ State hoisting patterns

**Pemahaman Saya:**
- Memahami bahwa `MainActivity` sebaiknya minimalis (hanya entry point)
- `App.kt` sebagai UI host mengatur layout dan routing
- `ViewModel` untuk data yang survive configuration changes
- Struktur modular memudahkan maintenance dan testing

#### 2. **Navigasi dengan AnimatedContent**
**Yang dibantu AI:**
- ✅ Setup `AnimatedContent` dengan transitionSpec custom
- ✅ Kombinasi animasi (slide + fade)

**Penyesuaian Saya:**
- Mengubah animasi agar hanya detail yang slide, sisanya fade
- Menambahkan timing 300ms untuk transisi smooth
- Menggunakan `rememberSaveable` agar state tidak hilang saat rotation

**Kode yang saya pahami:**
```kotlin
AnimatedContent(targetState = currentRoute, transitionSpec = {
    // Jika dari Home ke Detail → slide kanan + fade
    if (initialState == Screen.Home.route && targetState == "detail") {
        slideInHorizontally(animationSpec = tween(300)) { full -> full } + 
            fadeIn() 
        with  // Operator infix untuk menggabungkan enter+exit
            slideOutHorizontally(animationSpec = tween(300)) { full -> -full } + 
            fadeOut()
    } else {
        // Route lain hanya fade
        fadeIn() with fadeOut()
    }
})
```

#### 3. **Optimasi Performa**
**Yang dibantu AI:**
- ✅ Mengganti `Box` + `shadow` dengan `Card` + `elevation` (mengurangi GPU overhead)
- ✅ Menghapus `animateItemPlacement()` yang tidak kompatibel dengan versi Compose
- ✅ Menggunakan `derivedStateOf` untuk filter search (mengurangi recomposition)

**Pemahaman Saya:**
- Shadow mahal untuk GPU, elevation lebih ringan
- `derivedStateOf` hanya recompute saat dependency berubah
- `key` parameter di `items()` penting untuk performa LazyColumn

#### 4. **Material Design 3**
**Yang dibantu AI:**
- ✅ Setup tema dengan `lightColorScheme` dan `darkColorScheme`
- ✅ Implementasi komponen Material 3 (NavigationBar, Card, Scaffold)

**Penyesuaian Saya:**
- Memilih palet warna orange yang konsisten (light/dark)
- Menambahkan `tonalElevation` pada drawer untuk depth
- Menggunakan `containerColor` dan `contentColor` yang tepat

#### 5. **Error Handling dan Debugging**
**Yang dibantu AI:**
- ✅ Fix unresolved reference `animateItemPlacement`
- ✅ Fix duplicate declarations `Screen` dan `BottomNavItem`
- ✅ Fix frame skips dengan memindahkan logic ke ViewModel

**Pembelajaran Saya:**
- Selalu cek kompatibilitas API dengan versi Compose yang digunakan
- Centralize definitions untuk menghindari konflik
- Main thread harus ringan, heavy work di background

#### 6. **String Resources**
**Yang dibantu AI:**
- ✅ Externalize hardcoded strings ke `strings.xml`
- ✅ Penggunaan `stringResource()` di Composable

**Pemahaman Saya:**
```kotlin
// Sebelum (hardcoded)
Text("Nama")

// Sesudah (resource)
Text(stringResource(R.string.profile_label_name))

// Keuntungan: mudah lokalisasi, konsisten, maintainable
```

---

### Bagian yang Saya Kembangkan Sendiri

#### 1. **Logika Bisnis**
- Filter search dengan `contains(query, ignoreCase = true)`
- Validasi input `if (name.isNotBlank())`
- Auto-generate item name "Item ${size + 1}"

#### 2. **UI/UX Design**
- Pemilihan warna orange yang harmonis
- Spacing dan padding yang konsisten (8dp, 16dp, 24dp)
- Layout responsive dengan `BoxWithConstraints` untuk drawer

#### 3. **Data dan Content**
- Biodata di Profile.kt (nama, NIM, hobi, dll)
- Sample shopping items
- Text dan label Indonesian

---

## 🎥 Demo Flow (untuk Video)

### Timeline 5-10 Menit

**Menit 1-2: Intro dan Demo Fitur**
1. Jalankan aplikasi
2. Tunjukkan Home screen:
   - Daftar belanja (LazyColumn)
   - Tambah item via input
   - Search/filter item
   - Klik icon (toggle select) vs klik card (ke detail)
3. Navigasi ke Profile via bottom nav
4. Buka Setting via drawer
5. Kembali ke Home → klik item → Detail screen (tunjukkan animasi slide)

**Menit 3-5: Penjelasan Kode**
1. **Struktur proyek:**
   ```
   MainActivity → App.kt → [Home, Profile, Setting, Detail]
                         → [Components]
                         → [ViewModel]
                         → [Theme]
   ```

2. **Navigasi (App.kt):**
   - Tunjukkan `AnimatedContent` dan transitionSpec
   - Jelaskan `currentRoute` state management
   - Demo pengiriman data ke DetailScreen

3. **Komponen UI (ShoppingList.kt):**
   - Tunjukkan LazyColumn implementation
   - Jelaskan 3 animasi: `animateColorAsState`, `animateContentSize`, `animateFloatAsState`
   - Demo pisah click (card vs icon)

4. **Material Design:**
   - Show Scaffold structure di kode
   - Point out Card, FAB, Snackbar usage
   - Tunjukkan theme/Color.kt orange palette

**Menit 6-8: Refleksi AI**
1. "Saya menggunakan GitHub Copilot untuk membantu dengan:"
   - Arsitektur MVVM dan state management
   - Implementasi animasi complex (slide + fade)
   - Debugging performance issues (shadow → elevation)
   - Setup Material Design 3 theme

2. "Yang saya pahami dan sesuaikan:"
   - Kenapa perlu ViewModel (survive rotation, off main thread)
   - Cara kerja `AnimatedContent` (enter with exit animations)
   - Trade-off performa (shadow vs elevation, derivedStateOf)
   - Material Design principles (color roles, elevation, spacing)

3. "Yang saya develop sendiri:"
   - Logika bisnis (filter, validation)
   - Content (biodata, item names)
   - UI decisions (spacing, colors, layout)

**Menit 9-10: Penutup**
- Recap fitur yang memenuhi ketentuan (checklist)
- Lessons learned: "Compose itu declarative, state-driven, dan animations built-in"
- Q&A preparation

---

## 📝 Checklist Ketentuan

### Aplikasi
- [x] LazyColumn (`ShoppingList.kt`, `Profile.kt`)
- [x] Material Design (Card, Scaffold, Button, FAB, Snackbar)
- [x] Navigasi antar layar (Home → Detail, Bottom Nav, Drawer)
- [x] Pengiriman data (`currentDetailItem` ke `DetailScreen`)
- [x] Animasi (transisi slide+fade, color, size, scale)
- [x] Tema kustom (Orange theme light+dark)

### Video
- [x] Demo lengkap semua fitur
- [x] Penjelasan struktur proyek
- [x] Penjelasan navigasi
- [x] Penjelasan komponen UI
- [x] Penjelasan animasi
- [x] Refleksi penggunaan AI
- [x] Bagian yang dibantu vs dikembangkan sendiri

---

## 🎓 Key Takeaways

1. **Compose is Declarative**: UI adalah fungsi dari state
2. **State Hoisting**: Move state up untuk reusability
3. **ViewModel for Data**: Survive config changes, off main thread
4. **Animations are Easy**: Built-in APIs seperti `animate*AsState`
5. **Material Design 3**: Theme system powerful untuk consistency
6. **Performance Matters**: Measure, optimize (shadow→elevation)
7. **AI as Assistant**: Helps with boilerplate, you understand the "why"

---

## 📚 Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Compose Animation](https://developer.android.com/jetpack/compose/animation)
- [State Management](https://developer.android.com/jetpack/compose/state)
- [ViewModel Guide](https://developer.android.com/topic/libraries/architecture/viewmodel)

---

**Good luck dengan presentasinya! 🚀**
