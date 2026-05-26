package com.aimodelaggregator.data.repository

import com.aimodelaggregator.domain.models.ProviderType
import com.aimodelaggregator.domain.repository.ProviderSettingsRepository
import com.aimodelaggregator.domain.storage.SecureApiKeyStore

class ProviderSettingsRepositoryImpl(
    private val secureApiKeyStore: SecureApiKeyStore
) : ProviderSettingsRepository {

    override fun saveApiKey(provider: ProviderType, apiKey: String) {
        secureApiKeyStore.saveApiKey(provider, apiKey.trim())
    }

    override fun getApiKey(provider: ProviderType): String? {
        return secureApiKeyStore.getApiKey(provider)?.trim()
    }

    override fun deleteApiKey(provider: ProviderType) {
        secureApiKeyStore.deleteApiKey(provider)
    }

    override fun isProviderConfigured(provider: ProviderType): Boolean {
        return secureApiKeyStore.hasApiKey(provider)
    }
}
