package com.aimodelaggregator.data.repository

import com.aimodelaggregator.data.network.ChatCompletionRequest
import com.aimodelaggregator.data.network.ChatMessageDto
import com.aimodelaggregator.data.network.OpenAIApi
import com.aimodelaggregator.data.network.ChatCompletionResponse
import com.aimodelaggregator.domain.models.ChatMessage
import com.aimodelaggregator.domain.models.MessageRole
import com.aimodelaggregator.domain.models.ProviderType
import com.aimodelaggregator.domain.repository.ChatRepository
import com.aimodelaggregator.domain.repository.ConversationRepository
import com.aimodelaggregator.domain.repository.ProviderSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import com.squareup.moshi.Moshi

class ChatRepositoryImpl(
    private val groqApi: OpenAIApi,
    private val cerebrasApi: OpenAIApi,
    private val providerSettingsRepository: ProviderSettingsRepository,
    private val conversationRepository: ConversationRepository,
    private val moshi: Moshi
) : ChatRepository {

    override suspend fun sendMessage(
        conversationId: Long,
        message: String,
        provider: ProviderType,
        modelId: String
    ): ChatMessage {
        val userMessage = ChatMessage(
            conversationId = conversationId,
            role = MessageRole.USER,
            content = message,
            createdAt = System.currentTimeMillis()
        )
        conversationRepository.addMessage(userMessage)

        val history = conversationRepository.getConversationWithMessagesSync(conversationId)?.messages ?: listOf(userMessage)
        
        val api = getApi(provider)
        val authHeader = getAuthHeader(provider)

        val request = ChatCompletionRequest(
            model = modelId,
            messages = history.map { ChatMessageDto(it.role.name.lowercase(), it.content) }
        )

        val response = api.chatCompletion(authHeader, request)
        val assistantContent = response.choices.firstOrNull()?.message?.content ?: ""

        val assistantMessage = ChatMessage(
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = assistantContent,
            createdAt = System.currentTimeMillis()
        )
        conversationRepository.addMessage(assistantMessage)
        return assistantMessage
    }

    override fun sendMessageStream(
        conversationId: Long,
        message: String,
        provider: ProviderType,
        modelId: String
    ): Flow<String> = flow {
        val userMessage = ChatMessage(
            conversationId = conversationId,
            role = MessageRole.USER,
            content = message,
            createdAt = System.currentTimeMillis()
        )
        conversationRepository.addMessage(userMessage)

        val history = conversationRepository.getConversationWithMessagesSync(conversationId)?.messages ?: listOf(userMessage)
        
        val api = getApi(provider)
        val authHeader = getAuthHeader(provider)

        val request = ChatCompletionRequest(
            model = modelId,
            messages = history.map { ChatMessageDto(it.role.name.lowercase(), it.content) },
            stream = true
        )

        val responseBody = api.chatCompletionStream(authHeader, request)
        val adapter = moshi.adapter(ChatCompletionResponse::class.java)

        var fullAssistantContent = ""

        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
            var line: String? = reader.readLine()
            
            while (line != null) {
                if (line.startsWith("data: ")) {
                    val data = line.substring(6)
                    if (data == "[DONE]") break
                    
                    try {
                        val chunk = adapter.fromJson(data)
                        val deltaContent = chunk?.choices?.firstOrNull()?.delta?.content
                        if (deltaContent != null) {
                            fullAssistantContent += deltaContent
                            emit(fullAssistantContent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                line = reader.readLine()
            }
        }

        val assistantMessage = ChatMessage(
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = fullAssistantContent,
            createdAt = System.currentTimeMillis()
        )
        conversationRepository.addMessage(assistantMessage)
    }

    private fun getApi(provider: ProviderType): OpenAIApi {
        return when (provider) {
            ProviderType.GROQ -> groqApi
            ProviderType.CEREBRAS -> cerebrasApi
            ProviderType.GEMINI -> throw UnsupportedOperationException("Gemini use not implement yet")
        }
    }

    private suspend fun getAuthHeader(provider: ProviderType): String {
        val key = providerSettingsRepository.getApiKey(provider) ?: throw Exception("API key for \$provider is missing")
        return "Bearer \$key"
    }
}
