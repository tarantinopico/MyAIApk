package com.aimodelaggregator.di

import android.content.Context
import androidx.room.Room
import com.aimodelaggregator.data.database.AppDatabase
import com.aimodelaggregator.data.network.OpenAIApi
import com.aimodelaggregator.data.repository.ChatRepositoryImpl
import com.aimodelaggregator.data.repository.ConversationRepositoryImpl
import com.aimodelaggregator.data.repository.ModelRepositoryImpl
import com.aimodelaggregator.data.repository.ProviderSettingsRepositoryImpl
import com.aimodelaggregator.data.storage.SecureApiKeyStoreImpl
import com.aimodelaggregator.domain.repository.ChatRepository
import com.aimodelaggregator.domain.repository.ConversationRepository
import com.aimodelaggregator.domain.repository.ModelRepository
import com.aimodelaggregator.domain.repository.ProviderSettingsRepository
import com.aimodelaggregator.domain.storage.SecureApiKeyStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer {
    val secureApiKeyStore: SecureApiKeyStore
    val appDatabase: AppDatabase
    val providerSettingsRepository: ProviderSettingsRepository
    val modelRepository: ModelRepository
    val conversationRepository: ConversationRepository
    val chatRepository: ChatRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val groqApi: OpenAIApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenAIApi::class.java)
    }

    private val cerebrasApi: OpenAIApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cerebras.ai/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenAIApi::class.java)
    }

    override val secureApiKeyStore: SecureApiKeyStore by lazy {
        SecureApiKeyStoreImpl(context)
    }

    override val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "aimodel_aggregator_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    override val providerSettingsRepository: ProviderSettingsRepository by lazy {
        ProviderSettingsRepositoryImpl(secureApiKeyStore)
    }

    override val modelRepository: ModelRepository by lazy {
        ModelRepositoryImpl(appDatabase.providerModelDao())
    }

    override val conversationRepository: ConversationRepository by lazy {
        ConversationRepositoryImpl(
            appDatabase.conversationDao(),
            appDatabase.messageDao()
        )
    }

    override val chatRepository: ChatRepository by lazy {
        ChatRepositoryImpl(
            groqApi = groqApi,
            cerebrasApi = cerebrasApi,
            providerSettingsRepository = providerSettingsRepository,
            conversationRepository = conversationRepository,
            moshi = moshi
        )
    }
}
