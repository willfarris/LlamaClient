package dev.willfarris.llmchat.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object OllamaPreferencesManager {
    private const val PREF_NAME = "SETTINGS"
    private const val KEY_ENDPOINT = "ENDPOINT_URL"
    private const val KEY_CONTEXT_SIZE = "CONTEXT_SIZE"
    private const val KEY_SHOULD_OVERRIDE_SYSTEM_PROMPT = "OVERRIDE_SYSTEM_PROMPT"
    private const val KEY_SYSTEM_PROMPT = "SYSTEM_PROMPT"

    private lateinit var sharedPreferences: SharedPreferences

    const val DEFAULT_ENDPOINT: String = "http://localhost:11434"
    var endpointUrl: String = DEFAULT_ENDPOINT
        set(newEndpointUrl) {
            if(endpointUrl == newEndpointUrl) return
            sharedPreferences.edit().putString(KEY_ENDPOINT, newEndpointUrl).apply()
            field = newEndpointUrl
        }

    const val DEFAULT_CONTEXT_SIZE: Int = 2048
    var contextSize: Int = DEFAULT_CONTEXT_SIZE
        set(newContextSize) {
            if(newContextSize == field) return
            sharedPreferences.edit().putInt(KEY_CONTEXT_SIZE, newContextSize).apply()
            field = newContextSize
        }

    var overrideSystemPrompt: Boolean = false
        set(shouldOverrideSystemPrompt) {
            if(shouldOverrideSystemPrompt == field) return
            sharedPreferences.edit().putBoolean(KEY_SHOULD_OVERRIDE_SYSTEM_PROMPT, shouldOverrideSystemPrompt).apply()
            field = shouldOverrideSystemPrompt
        }

    const val DEFAULT_SYSTEM_PROMPT: String = "You are a helpful assistant whose primary goal is to assist the user as best you can."
    var systemPrompt: String = DEFAULT_SYSTEM_PROMPT
        set(newSystemPrompt) {
            if(newSystemPrompt == field) return
            if(newSystemPrompt.trim() != "") {
                sharedPreferences.edit().putString(KEY_SYSTEM_PROMPT, newSystemPrompt).apply()
                field = newSystemPrompt
            } else systemPrompt = DEFAULT_SYSTEM_PROMPT
        }

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        endpointUrl = sharedPreferences.getString(KEY_ENDPOINT, DEFAULT_ENDPOINT)!!
        contextSize = sharedPreferences.getInt(KEY_CONTEXT_SIZE, DEFAULT_CONTEXT_SIZE)
        overrideSystemPrompt = sharedPreferences.getBoolean(
            KEY_SHOULD_OVERRIDE_SYSTEM_PROMPT, false)
        systemPrompt = sharedPreferences.getString(KEY_SYSTEM_PROMPT, DEFAULT_SYSTEM_PROMPT)!!
    }

}