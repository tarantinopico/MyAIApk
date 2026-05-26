package com.aimodelaggregator.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming
import okhttp3.ResponseBody

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<ChatMessageDto>,
    @Json(name = "stream") val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "id") val id: String?,
    @Json(name = "choices") val choices: List<ChatChoiceDto>
)

@JsonClass(generateAdapter = true)
data class ChatChoiceDto(
    @Json(name = "message") val message: ChatMessageDto?,
    @Json(name = "delta") val delta: ChatMessageDto?
)

interface OpenAIApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    @Streaming
    @POST("chat/completions")
    suspend fun chatCompletionStream(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): ResponseBody
}
