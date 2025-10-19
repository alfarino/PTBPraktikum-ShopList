# 📱 Penjelasan Kode Utama - ShopList App

## 📂 Struktur Proyek

```
app/src/main/java/com/example/shopinglist/
│
├── MainActivity.kt              ← Entry point aplikasi
├── App.kt                       ← UI Host & Navigasi (MainAppContent)
├── Navigation.kt                ← Data model navigasi
├── MainViewModel.kt             ← State management (Data)
│
├── Home.kt                      ← Halaman Home
├── Profile.kt                   ← Halaman Profile
├── Setting.kt                   ← Halaman Setting
├── Detail.kt                    ← Halaman Detail
│
├── components/
│   ├── ShoppingList.kt          ← LazyColumn list
│   ├── ItemInput.kt             ← Input field + button
│   ├── SearchInput.kt           ← Search field
│   ├── BottomNavigationBar.kt   ← Bottom nav
│   ├── AppDrawer.kt             ← Drawer menu
│   └── Title.kt                 ← Header title
│
└── ui/theme/
    ├── Theme.kt                 ← Material theme
    ├── Color.kt                 ← Color palette
    ├── Type.kt                  ← Typography
    └── Shape.kt                 ← Shapes
```

---

## 🔥 File Inti dan Fungsi-Fungsinya

### 1️⃣ **MainActivity.kt** - Entry Point

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                MainAppContent()
            }
        }
    }
}
```

#### 📌 Fungsi Utama:
- **`onCreate()`**: Fungsi lifecycle Android yang dipanggil saat Activity dibuat
  - `setContent { }`: Mengganti XML layout dengan Jetpack Compose
  - `ShoppingListTheme { }`: Membungkus app dengan tema orange kustom
  - `MainAppContent()`: Memanggil fungsi composable utama dari `App.kt`

#### 🎯 Tanggung Jawab:
- **Entry point** aplikasi
- Setup tema
- Delegasi ke `App.kt`

---

### 2️⃣ **MainViewModel.kt** - State Management

```kotlin
class MainViewModel : ViewModel() {
    private val _items = mutableStateListOf(
        "Susu Segar",
        "Roti Tawar",
        "Telur Ayam",
        "Apel Fuji",
        "Daging Sapi"
    )
    val shoppingItems: MutableList<String>
        get() = _items

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

#### 📌 Fungsi-Fungsi:

1. **`_items`** (private):
   - `mutableStateListOf()`: List yang observable (Compose otomatis recompose saat berubah)
   - Private untuk enkapsulasi data

2. **`shoppingItems`** (public):
   - Getter untuk akses read-only dari luar
   - Return `_items` yang private

3. **`addNewItem()`**:
   - Membuat item baru dengan format "Item X"
   - Menambahkan ke list
   - Return nama item untuk ditampilkan di Snackbar

4. **`addItem(name: String)`**:
   - Menambah item dengan nama custom
   - Validasi: hanya tambah jika `name` tidak blank

#### 🎯 Tanggung Jawab:
- **Menyimpan data** shopping list
- **Survive configuration changes** (rotation, dark mode, dll)
- **Business logic** untuk manipulasi data

#### 💡 Keuntungan ViewModel:
- Data tidak hilang saat rotate
- Memindahkan logic dari UI
- Lifecycle-aware (auto cleanup)

---

### 3️⃣ **App.kt** - UI Host & Navigation Logic

```kotlin
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainAppContent() {
    // State declarations
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var currentRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
    var currentDetailItem by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = androidx.compose.material3.SnackbarHostState()
    val vm: MainViewModel = viewModel()
    val shoppingItems = vm.shoppingItems
    
    // ... UI Layout
}
```

#### 📌 State Variables:

1. **`drawerState`**:
   - `rememberDrawerState()`: State untuk drawer (open/closed)
   - `initialValue = DrawerValue.Closed`: Drawer tertutup saat start

2. **`coroutineScope`**:
   - `rememberCoroutineScope()`: Scope untuk coroutine (async operations)
   - Digunakan untuk `drawerState.open()`, `drawerState.close()`, `showSnackbar()`

3. **`currentRoute`**:
   - `rememberSaveable`: State yang survive rotation
   - Menyimpan route aktif ("home", "profile", "setting", "detail")

4. **`currentDetailItem`**:
   - Menyimpan nama item yang diklik untuk dikirim ke DetailScreen
   - `String?` = nullable (bisa null)

5. **`snackbarHostState`**:
   - State untuk menampilkan Snackbar notification

6. **`vm: MainViewModel`**:
   - `viewModel()`: Mendapatkan instance ViewModel
   - Otomatis survive configuration changes

7. **`shoppingItems`**:
   - Reference ke `vm.shoppingItems`
   - Observed: UI auto update saat data berubah

---

#### 📌 UI Components:

```kotlin
ModalNavigationDrawer(
    drawerState = drawerState,
    scrimColor = Color.Black.copy(alpha = 0.5f),
    drawerContent = {
        AppDrawer(
            selectedRoute = currentRoute,
            onDestinationClick = { destination ->
                currentRoute = destination.route
                coroutineScope.launch { drawerState.close() }
            }
        )
    }
) { /* Scaffold content */ }
```

##### **A. ModalNavigationDrawer**
- **Fungsi**: Container untuk drawer yang bisa di-swipe dari kiri
- **Parameters**:
  - `drawerState`: State untuk control buka/tutup
  - `scrimColor`: Warna overlay saat drawer terbuka (hitam 50% transparent)
  - `drawerContent`: UI yang muncul di drawer → `AppDrawer` component
  - `onDestinationClick`: Callback saat item drawer diklik
    - Update `currentRoute`
    - Tutup drawer dengan coroutine

---

```kotlin
Scaffold(
    topBar = { TopAppBar(...) },
    bottomBar = { BottomNavigationBar(...) },
    floatingActionButton = { FloatingActionButton(...) },
    snackbarHost = { SnackbarHost(...) }
) { innerPadding -> /* Content */ }
```

##### **B. Scaffold**
- **Fungsi**: Layout structure Material Design dengan slot untuk topBar, bottomBar, FAB, dll
- **Parameters**:

**1. TopAppBar:**
```kotlin
TopAppBar(
    title = { Text(text = currentScreen.title) },
    navigationIcon = {
        IconButton(onClick = {
            coroutineScope.launch {
                if (drawerState.isOpen) {
                    drawerState.close()
                } else {
                    drawerState.open()
                }
            }
        }) {
            Icon(
                imageVector = if (drawerState.isOpen) {
                    Icons.Filled.Settings
                } else {
                    Icons.Filled.Menu
                },
                contentDescription = "Buka menu"
            )
        }
    }
)
```
- **`title`**: Menampilkan judul screen aktif ("Home", "Profile", "Setting")
- **`navigationIcon`**: Tombol hamburger menu (☰)
  - Klik → toggle drawer (buka/tutup)
  - Icon berubah: Menu (☰) ↔ Settings (⚙️)

**2. FloatingActionButton:**
```kotlin
FloatingActionButton(onClick = {
    val newItem = vm.addNewItem()  // Panggil ViewModel
    coroutineScope.launch {
        snackbarHostState.showSnackbar("Menambahkan: $newItem")
    }
}) {
    Icon(imageVector = Icons.Filled.Add, contentDescription = "Tambah")
}
```
- **Fungsi**: Tombol floating (+) untuk tambah item cepat
- **onClick**:
  1. `vm.addNewItem()`: Buat item baru via ViewModel
  2. Tampilkan Snackbar dengan nama item

**3. SnackbarHost:**
```kotlin
snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
```
- **Fungsi**: Container untuk menampilkan Snackbar notification

**4. BottomNavigationBar:**
```kotlin
BottomNavigationBar(
    items = bottomNavItems,  // List dari Navigation.kt
    selectedRoute = currentRoute,
    onItemSelected = { selected ->
        currentRoute = selected.screen.route
    }
)
```
- **Fungsi**: Bottom navigation untuk Home & Profile
- **onItemSelected**: Callback saat tab diklik → update `currentRoute`

---

#### 📌 Navigation Logic (AnimatedContent):

```kotlin
AnimatedContent(targetState = currentRoute, transitionSpec = {
    if (initialState == Screen.Home.route && targetState == "detail") {
        slideInHorizontally(animationSpec = tween(300)) { full -> full } + 
            fadeIn() with
            slideOutHorizontally(animationSpec = tween(300)) { full -> -full } + 
            fadeOut()
    } else if (initialState == "detail" && targetState == Screen.Home.route) {
        slideInHorizontally(animationSpec = tween(300)) { full -> -full } + 
            fadeIn() with
            slideOutHorizontally(animationSpec = tween(300)) { full -> full } + 
            fadeOut()
    } else {
        fadeIn(animationSpec = tween(200)) with fadeOut(animationSpec = tween(200))
    }
}) { route ->
    when (route) {
        Screen.Profile.route -> ProfileScreen()
        Screen.Setting.route -> SettingScreen()
        "detail" -> {
            val item = currentDetailItem ?: ""
            DetailScreen(item = item, onBack = {
                currentDetailItem = null
                currentRoute = Screen.Home.route
            })
        }
        else -> HomeScreen(shoppingItems = shoppingItems, onItemClick = { item ->
            currentDetailItem = item
            currentRoute = "detail"
        })
    }
}
```

##### **Fungsi AnimatedContent:**
- **Menampilkan screen berdasarkan** `currentRoute`
- **Animasi transisi** antar screen

##### **Transition Specs:**

**1. Home → Detail:**
```kotlin
slideInHorizontally { full -> full } + fadeIn()    // Masuk dari kanan
with
slideOutHorizontally { full -> -full } + fadeOut()  // Keluar ke kiri
```
- Duration: 300ms (tween)
- Detail slide masuk dari kanan + fade in
- Home slide keluar ke kiri + fade out

**2. Detail → Home:**
```kotlin
slideInHorizontally { full -> -full } + fadeIn()    // Masuk dari kiri
with
slideOutHorizontally { full -> full } + fadeOut()   // Keluar ke kanan
```
- Duration: 300ms (tween)
- Home slide masuk dari kiri + fade in
- Detail slide keluar ke kanan + fade out

**3. Route lainnya:**
```kotlin
fadeIn() with fadeOut()  // Hanya fade (200ms)
```
- Profile, Setting: fade in/out saja

##### **Route Handling:**

**1. Profile:**
```kotlin
Screen.Profile.route -> ProfileScreen()
```
- Tampilkan halaman Profile dengan biodata

**2. Setting:**
```kotlin
Screen.Setting.route -> SettingScreen()
```
- Tampilkan halaman Setting dengan toggle

**3. Detail:**
```kotlin
"detail" -> {
    val item = currentDetailItem ?: ""
    DetailScreen(item = item, onBack = {
        currentDetailItem = null
        currentRoute = Screen.Home.route
    })
}
```
- **Data passing**: `currentDetailItem` dikirim ke `DetailScreen`
- **onBack callback**: Saat tombol Back diklik
  - Reset `currentDetailItem` ke `null`
  - Navigasi kembali ke Home (`currentRoute = Screen.Home.route`)

**4. Home (default):**
```kotlin
else -> HomeScreen(
    shoppingItems = shoppingItems,      // Data dari ViewModel
    onItemClick = { item ->             // Callback saat item diklik
        currentDetailItem = item        // Simpan item yang diklik
        currentRoute = "detail"         // Navigate ke detail
    }
)
```
- **Data passing**: `shoppingItems` dari ViewModel
- **onItemClick**: Callback untuk navigate ke detail dengan data

---

### 4️⃣ **Navigation.kt** - Navigation Data Model

```kotlin
sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Profile : Screen("profile", "Profile")
    data object Setting : Screen("setting", "Setting")
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, "Home"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, "Profile")
)
```

#### 📌 Fungsi:

1. **`Screen` sealed class**:
   - **Type-safe navigation** (tidak ada typo string)
   - `route`: String untuk routing ("home", "profile", "setting")
   - `title`: Judul untuk TopAppBar
   - `data object`: Singleton (hanya 1 instance)

2. **`BottomNavItem` data class**:
   - Model untuk item bottom navigation
   - `screen`: Reference ke Screen object
   - `icon`: Icon Material (Home, Person)
   - `label`: Label text

3. **`bottomNavItems` list**:
   - List item untuk bottom navigation
   - Hanya Home & Profile (Setting di drawer)

---

### 5️⃣ **Home.kt** - Halaman Utama

```kotlin
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    shoppingItems: MutableList<String>,
    onItemClick: (String) -> Unit = {}
) {
    var newItemText by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredItems by remember(searchQuery, shoppingItems) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                shoppingItems
            } else {
                shoppingItems.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Title()
        ItemInput(
            text = newItemText,
            onTextChange = { newItemText = it },
            onAddItem = {
                if (newItemText.isNotBlank()) {
                    shoppingItems.add(newItemText)
                    newItemText = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchInput(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShoppingList(items = filteredItems, onItemClick = onItemClick)
    }
}
```

#### 📌 Parameters:

1. **`shoppingItems: MutableList<String>`**:
   - Data list dari ViewModel (via `App.kt`)
   - Mutable = bisa diubah dari HomeScreen

2. **`onItemClick: (String) -> Unit`**:
   - Callback function untuk handle klik item
   - Parameter: nama item yang diklik
   - Diteruskan ke `ShoppingList` component

#### 📌 Local State:

1. **`newItemText`**:
   - `rememberSaveable`: State untuk text input
   - Survive rotation

2. **`searchQuery`**:
   - State untuk search field
   - Survive rotation

3. **`filteredItems`**:
   - `derivedStateOf`: Computed state
   - **Hanya recompute** saat `searchQuery` atau `shoppingItems` berubah
   - Logic:
     - Jika `searchQuery` kosong → tampilkan semua
     - Jika ada query → filter dengan `contains(query, ignoreCase = true)`

#### 📌 UI Components:

1. **`Title()`**:
   - Component untuk header "Shopping List"

2. **`ItemInput`**:
   - TextField + Button Add
   - **Props**:
     - `text`: Value dari `newItemText`
     - `onTextChange`: Update `newItemText`
     - `onAddItem`: Callback saat button Add diklik
       - Validasi: hanya tambah jika tidak blank
       - `shoppingItems.add(newItemText)`: Tambah ke list
       - `newItemText = ""`: Clear input

3. **`SearchInput`**:
   - TextField dengan icon search
   - **Props**:
     - `query`: Value dari `searchQuery`
     - `onQueryChange`: Update `searchQuery`

4. **`ShoppingList`**:
   - LazyColumn component
   - **Props**:
     - `items`: `filteredItems` (hasil filter)
     - `onItemClick`: Callback diteruskan dari parent

---

### 6️⃣ **Profile.kt** - Halaman Profile

```kotlin
private data class ProfileInfo(
	val label: String,
	val value: String
)

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
	val profileItems = listOf(
		ProfileInfo(stringResource(id = R.string.profile_label_name), "Alfa Rino Svedrilio"),
		ProfileInfo(stringResource(id = R.string.profile_label_nim), "2311522005"),
		ProfileInfo(stringResource(id = R.string.profile_label_hobby), "Membaca"),
		ProfileInfo(stringResource(id = R.string.profile_label_dob), "Payakumbuh, 13 Agustus 2005"),
		ProfileInfo(stringResource(id = R.string.profile_label_major), "Mobile Programming")
	)

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		contentPadding = PaddingValues(bottom = 32.dp)
	) {
		item {
			Card(
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					AsyncImage(
						model = R.drawable.profile_picture,
						contentDescription = stringResource(id = R.string.profile_photo_desc),
						modifier = Modifier
							.fillMaxWidth()
							.height(180.dp)
							.background(MaterialTheme.colorScheme.surfaceVariant),
						contentScale = ContentScale.Crop
					)
					Text(
						text = stringResource(id = R.string.profile_title_greeting),
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = stringResource(id = R.string.profile_description),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
		}

		items(profileItems) { info ->
			Card(
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(modifier = Modifier.padding(16.dp)) {
					Text(
						text = info.label,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold
					)
					Spacer(modifier = Modifier.height(4.dp))
					Text(
						text = info.value,
						style = MaterialTheme.typography.bodyLarge
					)
				}
			}
		}
	}
}
```

#### 📌 Data Class:

**`ProfileInfo`**:
- Model untuk data profil
- `label`: Label field (Nama, NIM, Hobi, dll)
- `value`: Nilai field

#### 📌 Local Data:

**`profileItems`**:
- List of `ProfileInfo`
- Menggunakan `stringResource()` untuk label (i18n-ready)

#### 📌 UI Components:

1. **LazyColumn**:
   - Scroll container (seperti RecyclerView)
   - **Parameters**:
     - `verticalArrangement = Arrangement.spacedBy(16.dp)`: Jarak antar item
     - `contentPadding`: Padding bottom untuk scroll sampai bawah

2. **First Card (Header)**:
   ```kotlin
   item { Card { ... } }
   ```
   - **AsyncImage**: Load gambar dari `R.drawable.profile_picture`
     - Menggunakan **Coil library** (efficient image loading)
     - `contentScale = ContentScale.Crop`: Crop gambar untuk fill width
   - **Text (Greeting)**: "Halo! 👋"
   - **Text (Description)**: Bio singkat

3. **Profile Items Cards**:
   ```kotlin
   items(profileItems) { info -> Card { ... } }
   ```
   - Loop melalui `profileItems`
   - Setiap item tampilkan Card dengan:
     - **Label** (bold, titleMedium)
     - **Value** (bodyLarge)

---

### 7️⃣ **Setting.kt** - Halaman Setting

```kotlin
@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
	var notificationsEnabled by remember { mutableStateOf(true) }
	var darkThemeEnabled by remember { mutableStateOf(false) }

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Ini halaman Pengaturan",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold
		)

		Card(...) {
			Column {
				SettingToggleRow(
					title = "Notifikasi",
					description = "Aktifkan untuk menerima pengingat belanja",
					checked = notificationsEnabled,
					onCheckedChange = { notificationsEnabled = it }
				)
				SettingToggleRow(
					title = "Mode Gelap",
					description = "Sesuaikan tampilan dengan preferensi gelap",
					checked = darkThemeEnabled,
					onCheckedChange = { darkThemeEnabled = it }
				)
				Button(onClick = { /* TODO */ }) {
					Text(text = "Simpan Pengaturan")
				}
			}
		}
	}
}

@Composable
private fun SettingToggleRow(
	title: String,
	description: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit
) {
	Card(...) {
		Column {
			Text(text = title, style = MaterialTheme.typography.titleMedium)
			Text(text = description, style = MaterialTheme.typography.bodyMedium)
			Switch(
				checked = checked,
				onCheckedChange = onCheckedChange
			)
		}
	}
}
```

#### 📌 Local State:

1. **`notificationsEnabled`**:
   - State untuk toggle notifikasi
   - Default: `true`

2. **`darkThemeEnabled`**:
   - State untuk toggle dark mode
   - Default: `false`

#### 📌 Components:

**`SettingToggleRow`**:
- Reusable component untuk setting row
- **Props**:
  - `title`: Judul setting
  - `description`: Deskripsi
  - `checked`: State boolean
  - `onCheckedChange`: Callback saat switch toggle

---

### 8️⃣ **Detail.kt** - Halaman Detail

```kotlin
@Composable
fun DetailScreen(item: String, onBack: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { full -> full / 4 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { full -> full / 4 }) + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Detail Item", style = MaterialTheme.typography.titleLarge)
            Text(text = item, style = MaterialTheme.typography.headlineMedium, 
                 modifier = Modifier.padding(top = 8.dp))

            Button(onClick = { onBack() }, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = stringResource(R.string.kembali2))
            }
        }
    }
}
```

#### 📌 Parameters:

1. **`item: String`**:
   - Nama item yang diterima dari HomeScreen
   - Diteruskan via `currentDetailItem` di `App.kt`

2. **`onBack: () -> Unit`**:
   - Callback untuk tombol Back
   - Di `App.kt` implemented sebagai:
     ```kotlin
     onBack = {
         currentDetailItem = null
         currentRoute = Screen.Home.route
     }
     ```

#### 📌 Animation:

**`AnimatedVisibility`**:
- **Enter animation**:
  - `slideInVertically`: Slide masuk dari bawah (1/4 layar)
  - `fadeIn()`: Fade in bersamaan
- **Exit animation**:
  - `slideOutVertically`: Slide keluar ke bawah
  - `fadeOut()`: Fade out bersamaan

---

## 🎨 Komponen UI Reusable

### 9️⃣ **ShoppingList.kt** - LazyColumn Component

```kotlin
@Composable
fun ShoppingList(items: List<String>, onItemClick: (String) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
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

#### 📌 Fungsi LazyColumn:
- **Efisien**: Hanya render item yang visible (seperti RecyclerView)
- **Parameters**:
  - `contentPadding`: Padding top/bottom
  - `verticalArrangement.spacedBy(8.dp)`: Jarak antar item
  - `items(items, key = { it })`: Loop items dengan key unique

---

```kotlin
@Composable
fun ShoppingListItem(item: String, onCardClick: () -> Unit = {}) {
    var isSelected by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(200)
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) 
            MaterialTheme.colorScheme.onPrimaryContainer 
        else 
            MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(200)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar Card
            val avatarColor by animateColorAsState(
                targetValue = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondaryContainer,
                animationSpec = tween(200)
            )

            Card(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(containerColor = avatarColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = item.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Item Text
            Text(
                text = item,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            // Icon with Scale Animation
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(stiffness = 400f)
            )
            
            val iconBgColor by animateColorAsState(
                targetValue = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                animationSpec = tween(200)
            )

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { isSelected = !isSelected },
                shape = RoundedCornerShape(10.dp),
                color = iconBgColor
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isSelected) "Selected" else "Add to cart",
                    tint = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(6.dp)
                        .scale(iconScale)
                )
            }
        }
    }
}
```

#### 📌 Local State:

**`isSelected`**:
- State untuk track apakah item dipilih
- Toggle saat icon diklik

#### 📌 Animasi (3 Jenis):

**1. Color Animation (`animateColorAsState`)**:
- **backgroundColor**: Surface → PrimaryContainer saat selected
- **contentColor**: OnSurface → OnPrimaryContainer saat selected
- **avatarColor**: SecondaryContainer → Primary saat selected
- **iconBgColor**: Outline → Primary saat selected
- **Duration**: 200ms (tween)

**2. Scale Animation (`animateFloatAsState`)**:
- **iconScale**: 1.0 → 1.15 saat selected
- **Animation**: Spring dengan stiffness 400 (bouncy effect)

**3. Size Animation (`animateContentSize`)**:
- **Card**: Otomatis animate saat content size berubah
- Smooth expansion/contraction

#### 📌 Click Behaviors:

**1. Click Card**:
```kotlin
.clickable { onCardClick() }
```
- Trigger navigasi ke DetailScreen
- Diteruskan ke parent via callback

**2. Click Icon**:
```kotlin
.clickable { isSelected = !isSelected }
```
- Toggle `isSelected` state
- **Tidak** trigger navigasi
- Untuk "add to cart" functionality

---

### 🔟 **ItemInput.kt** - Input Component

```kotlin
@Composable
fun ItemInput(text: String, onTextChange: (String) -> Unit, onAddItem: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Add new item") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onAddItem) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add")
        }
    }
}
```

#### 📌 Parameters:
- **`text`**: Value input field (dari parent state)
- **`onTextChange`**: Callback saat text berubah
- **`onAddItem`**: Callback saat button Add diklik

#### 📌 Components:
- **OutlinedTextField**: Material Design text field
  - `shape = RoundedCornerShape(16.dp)`: Rounded corners
  - `modifier.weight(1f)`: Expand untuk ambil sisa space
- **Button**: Action button dengan icon + text

---

### 1️⃣1️⃣ **SearchInput.kt** - Search Component

```kotlin
@Composable
fun SearchInput(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.search_items)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        }
    )
}
```

#### 📌 Features:
- **leadingIcon**: Search icon (🔍) di kiri
- **stringResource**: Label dari resources (i18n)

---

### 1️⃣2️⃣ **BottomNavigationBar.kt** - Bottom Nav Component

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
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}
```

#### 📌 Fungsi:
- Loop melalui `items` (Home & Profile)
- Highlight item yang selected
- Callback `onItemSelected` saat diklik

---

### 1️⃣3️⃣ **AppDrawer.kt** - Drawer Component

```kotlin
@Composable
fun AppDrawer(
    selectedRoute: String,
    onDestinationClick: (Screen) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxHeight()) {
        val drawerWidth: Dp = maxWidth * 0.66f  // 2/3 layar

        Surface(
            modifier = Modifier
                .width(drawerWidth)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text(text = Screen.Setting.title) },
                    selected = selectedRoute == Screen.Setting.route,
                    onClick = { onDestinationClick(Screen.Setting) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Geser untuk kembali",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

#### 📌 Features:
- **BoxWithConstraints**: Responsive width (2/3 layar)
- **NavigationDrawerItem**: Item untuk Setting
- **Rounded corners**: Top-right & bottom-right
- **tonalElevation**: Material Design elevation

---

## 🎨 Theme System

### 1️⃣4️⃣ **Theme.kt** - Material Theme

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = OrangeLight,
    onPrimary = OrangeOn,
    primaryContainer = OrangePrimaryContainer,
    onPrimaryContainer = OrangeOnPrimaryContainer,
    secondary = OrangeSecondary,
    secondaryContainer = OrangeSecondaryContainer,
    onSecondaryContainer = OrangeOnSecondaryContainer,
    background = OrangeBackground,
    onBackground = OrangeOnBackground,
    surface = OrangeSurface,
    onSurface = OrangeOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,
    onPrimary = OrangeOn,
    primaryContainer = OrangeDarkPrimaryContainer,
    onPrimaryContainer = OrangeDarkOnPrimaryContainer,
    secondary = OrangeSecondary,
    secondaryContainer = OrangeDarkSecondaryContainer,
    onSecondaryContainer = OrangeDarkOnSecondaryContainer,
    background = OrangeDarkBackground,
    onBackground = OrangeDarkOnBackground,
    surface = OrangeSurfaceDark,
    onSurface = OrangeOnSurfaceDark
)

@Composable
fun ShoppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

#### 📌 Fungsi:
- **LightColorScheme**: Warna untuk light mode
- **DarkColorScheme**: Warna untuk dark mode
- **ShoppingListTheme**: Composable untuk apply theme
  - Auto-detect dark mode
  - Support Material You (Android 12+)
  - Fallback ke custom orange theme

---

## 📊 Alur Data Lengkap

```
USER ACTION                     STATE UPDATE                    UI UPDATE
───────────────────────────────────────────────────────────────────────────

1. Klik FAB (+)
   └─> App.kt: vm.addNewItem()
       └─> MainViewModel: _items.add("Item X")
           └─> Compose: Detect state change
               └─> HomeScreen: Recompose
                   └─> ShoppingList: Show new item
                   └─> Snackbar: "Menambahkan: Item X"

2. Ketik di SearchInput
   └─> HomeScreen: searchQuery = "susu"
       └─> derivedStateOf: Filter items
           └─> ShoppingList: Recompose dengan filtered items

3. Klik Item Card
   └─> ShoppingList: onCardClick()
       └─> HomeScreen: onItemClick(item)
           └─> App.kt: 
               ├─> currentDetailItem = item
               └─> currentRoute = "detail"
                   └─> AnimatedContent: Slide animation
                       └─> DetailScreen: Show item detail

4. Klik Icon dalam Item
   └─> ShoppingListItem: isSelected = !isSelected
       └─> Animate colors & scale
           └─> Icon change: Add (+) ↔ Check (✓)

5. Klik Bottom Nav (Profile)
   └─> BottomNavigationBar: onItemSelected(Profile)
       └─> App.kt: currentRoute = "profile"
           └─> AnimatedContent: Fade animation
               └─> ProfileScreen: Show profile

6. Buka Drawer (☰)
   └─> TopAppBar: IconButton.onClick
       └─> App.kt: drawerState.open()
           └─> ModalNavigationDrawer: Slide drawer from left

7. Klik Setting di Drawer
   └─> AppDrawer: onDestinationClick(Setting)
       └─> App.kt: 
           ├─> currentRoute = "setting"
           └─> drawerState.close()
               └─> AnimatedContent: Fade animation
                   └─> SettingScreen: Show settings
```

---

## 🎯 Kesimpulan

### Prinsip Desain:
1. **Separation of Concerns**: Setiap file punya tanggung jawab jelas
2. **Unidirectional Data Flow**: State turun, events naik
3. **Single Source of Truth**: ViewModel holds data
4. **Component Composition**: UI dari komponen kecil reusable
5. **Declarative UI**: UI = function of state

### Performance Optimizations:
1. **LazyColumn**: Efficient list rendering
2. **derivedStateOf**: Smart recomposition
3. **rememberSaveable**: Survive rotation
4. **ViewModel**: Lifecycle-aware state
5. **Card elevation**: Lighter than shadow

### Material Design 3:
1. **Color roles**: primary, surface, background, etc.
2. **Typography scale**: headlineLarge, titleMedium, bodyLarge
3. **Shape system**: RoundedCornerShape(12dp, 16dp)
4. **Elevation**: 2dp, 4dp, 6dp, 8dp
5. **Spacing**: 8dp grid (8, 16, 24, 32)

---

**File ini menjelaskan SEMUA fungsi dan bagaimana mereka terhubung! 🚀**
