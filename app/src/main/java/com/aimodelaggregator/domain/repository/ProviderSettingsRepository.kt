package com.aimodelaggregator.domain.repository

import com.aimodelaggregator.domain.models.ProviderType

interface ProviderSettingsRepository {
    fun saveApiKey(provider: ProviderType, apiKey: String)
    fun getApiKey(provider: ProviderType): String?
    fun deleteApiKey(provider: ProviderType)
    fun isProviderConfigured(provider: ProviderType): Boolean
}
