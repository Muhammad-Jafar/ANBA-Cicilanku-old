package jafar.cicilan.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import jafar.cicilan.utils.Constanta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreData(private val store: DataStore<Preferences>) {

    suspend fun saveLangValue(newValue: String) = store.edit { it[langValue] = newValue }
    fun getLangValue(): Flow<String> = store.data.map { it[langValue] ?: Constanta.defaultLanguage }

    suspend fun saveThemeValue(newValue: Int) = store.edit { it[themeValue] = newValue }
    fun getThemeValue(): Flow<Int> = store.data.map { it[themeValue] ?: Constanta.defaultTheme }

    companion object {
        private val themeValue = intPreferencesKey(Constanta.StoreData.Theme.name)
        private val langValue = stringPreferencesKey(Constanta.StoreData.Language.name)
    }
}
