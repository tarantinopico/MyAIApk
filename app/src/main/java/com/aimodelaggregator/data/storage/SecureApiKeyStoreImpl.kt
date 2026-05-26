package com.aimodelaggregator.data.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.aimodelaggregator.domain.models.ProviderType
import com.aimodelaggregator.domain.storage.SecureApiKeyStore

class SecureApiKeyStoreImpl(context: Context) : SecureApiKeyStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "api_keys_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun getProviderKey(provider: ProviderType) = "api_key_${provider.name}"

    override fun saveApiKey(provider: ProviderType, apiKey: String) {
        sharedPreferences.edit().putString(getProviderKey(provider), apiKey).apply()
    }

    override fun getApiKey(provider: ProviderType): String? {
        return sharedPreferences.getString(getProviderKey(provider), null)
    }

    override fun deleteApiKey(provider: ProviderType) {
        sharedPreferences.edit().remove(getProviderKey(provider)).apply()
    }

    override fun hasApiKey(provider: ProviderType): Boolean {
        return sharedPreferences.contains(getProviderKey(provider))
    }
}
