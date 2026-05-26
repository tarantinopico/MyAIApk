package com.aimodelaggregator.domain.storage

import com.aimodelaggregator.domain.models.ProviderType

/**
 * Interface for securely storing API keys.
 */
interface SecureApiKeyStore {
    fun saveApiKey(provider: ProviderType, apiKey: String)
    fun getApiKey(provider: ProviderType): String?
    fun deleteApiKey(provider: ProviderType)
    fun hasApiKey(provider: ProviderType): Boolean
}
