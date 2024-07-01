package dev.willfarris.llmchat.data.preferences

import android.content.Context
import android.content.SharedPreferences

object OllamaPreferencesManager {
    private const val PREF_NAME = "SETTINGS"
    private const val KEY_ENDPOINT = "ENDPOINT_URL"
    private const val KEY_CONTEXT_SIZE = "CONTEXT_SIZE"
    private const val KEY_SYSTEM_PROMPT = "SYSTEM_PROMPT"
    private const val KEY_LAST_CHAT_INDEX = "LAST_CHAT_INDEX"

    private lateinit var sharedPreferences: SharedPreferences

    const val DEFAULT_ENDPOINT: String = "http://localhost:11434"
    var endpointUrl: String = DEFAULT_ENDPOINT
        set(newEndpointUrl) {
            if(endpointUrl == newEndpointUrl) return
            sharedPreferences.edit().putString(KEY_ENDPOINT, newEndpointUrl).apply()
            field = newEndpointUrl
        }

    const val DEFAULT_CONTEXT_SIZE: Int = 32000
    var contextSize: Int = DEFAULT_CONTEXT_SIZE
        set(newContextSize) {
            if(newContextSize == field) return
            sharedPreferences.edit().putInt(KEY_CONTEXT_SIZE, newContextSize).apply()
            field = newContextSize
        }

    const val DEFAULT_SYSTEM_PROMPT: String = "You are a helpful assistant whose primary goal is to assist the user as best you can."
    var systemPrompt: String = DEFAULT_SYSTEM_PROMPT
        set(newSystemPrompt) {
            if(newSystemPrompt == field) return
            field = if(newSystemPrompt.trim() != "") {
                sharedPreferences.edit().putString(KEY_SYSTEM_PROMPT, newSystemPrompt).apply()
                newSystemPrompt
            } else DEFAULT_SYSTEM_PROMPT
        }

    var lastChatIndex: Int = 0
        set(newLastChatIndex) {
            if(lastChatIndex == newLastChatIndex) return
            sharedPreferences.edit().putInt(KEY_LAST_CHAT_INDEX, newLastChatIndex).apply()
            field = newLastChatIndex
        }


    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        endpointUrl = sharedPreferences.getString(KEY_ENDPOINT, DEFAULT_ENDPOINT)!!
        contextSize = sharedPreferences.getInt(KEY_CONTEXT_SIZE, DEFAULT_CONTEXT_SIZE)
        systemPrompt = sharedPreferences.getString(KEY_SYSTEM_PROMPT, DEFAULT_SYSTEM_PROMPT)!!
        lastChatIndex = sharedPreferences.getInt(KEY_LAST_CHAT_INDEX, 0)
    }

}