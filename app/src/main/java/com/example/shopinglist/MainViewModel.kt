package com.example.shopinglist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

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
