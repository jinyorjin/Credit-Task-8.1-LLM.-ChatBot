package com.deakin.llmchatbot

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class BotResponse(val text: String, val aiSource: String)

class GeminiHelper {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun getResponse(prompt: String): BotResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text
                if (responseText != null) {
                    BotResponse(text = responseText, aiSource = "Gemini AI")
                } else {
                    BotResponse(text = "I am sorry, I could not generate a response.", aiSource = "Fake AI")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                BotResponse(text = "Error connecting to AI. This is a fallback fake AI response.", aiSource = "Fake AI")
            }
        }
    }
}
