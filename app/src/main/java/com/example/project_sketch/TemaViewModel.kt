package com.example.project_sketch

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context

val Context.dataStore by preferencesDataStore(name = "preferencias")

class TemaViewModel(private val app: Application) : AndroidViewModel(app) {

    private val temaKey = stringPreferencesKey("tema")

    val tema = app.dataStore.data
        .map { it[temaKey] ?: "sistema" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "sistema")

    fun setTema(nuevo: String) {
        viewModelScope.launch {
            app.dataStore.edit { it[temaKey] = nuevo }
        }
    }
}