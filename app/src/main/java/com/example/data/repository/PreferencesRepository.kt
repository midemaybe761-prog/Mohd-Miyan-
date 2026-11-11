package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AiProviderType
import com.example.data.model.ApiConfig
import com.example.data.model.StudioAppMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "studio_settings")

class PreferencesRepository(private val context: Context) {
    private val KEY_MODE = stringPreferencesKey("app_mode")
    private val KEY_PROVIDER = stringPreferencesKey("selected_provider")
    private val KEY_BACKEND_URL = stringPreferencesKey("backend_url")
    private val KEY_API_KEY = stringPreferencesKey("api_key")
    private val KEY_TIER = stringPreferencesKey("subscription_tier")
    private val KEY_IS_CONNECTED = booleanPreferencesKey("is_connected")

    val apiConfigFlow: Flow<ApiConfig> = context.dataStore.data.map { prefs ->
        val modeStr = prefs[KEY_MODE] ?: StudioAppMode.MODE_A_LOCAL.name
        val providerStr = prefs[KEY_PROVIDER] ?: AiProviderType.NODE_BACKEND.name
        val backendUrl = prefs[KEY_BACKEND_URL] ?: "http://10.0.2.2:5000"
        val apiKey = prefs[KEY_API_KEY] ?: ""
        val tier = prefs[KEY_TIER] ?: "Pro Studio (Unlimited Local + AI Ready)"
        val isConnected = prefs[KEY_IS_CONNECTED] ?: false

        val activeMode = runCatching { StudioAppMode.valueOf(modeStr) }.getOrDefault(StudioAppMode.MODE_A_LOCAL)
        val selectedProvider = runCatching { AiProviderType.valueOf(providerStr) }.getOrDefault(AiProviderType.NODE_BACKEND)

        ApiConfig(
            activeMode = activeMode,
            selectedProvider = selectedProvider,
            backendBaseUrl = backendUrl,
            apiKey = apiKey,
            isConnected = isConnected,
            subscriptionTier = tier
        )
    }

    suspend fun setAppMode(mode: StudioAppMode) {
        context.dataStore.edit { it[KEY_MODE] = mode.name }
    }

    suspend fun setProvider(provider: AiProviderType) {
        context.dataStore.edit { it[KEY_PROVIDER] = provider.name }
    }

    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { it[KEY_BACKEND_URL] = url }
    }

    suspend fun setApiKey(key: String) {
        context.dataStore.edit { it[KEY_API_KEY] = key }
    }

    suspend fun setConnectionStatus(connected: Boolean) {
        context.dataStore.edit { it[KEY_IS_CONNECTED] = connected }
    }
}
