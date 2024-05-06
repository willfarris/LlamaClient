package dev.willfarris.llmchat.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object OllamaPreferencesManager {
    private const val PREF_NAME = "SETTINGS"
    private const val KEY_ENDPOINT = "ENDPOINT_URL"
    private const val KEY_CONTEXT_SIZE = "CONTEXT_SIZE"

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
            if(newContextSize == contextSize) return
            sharedPreferences.edit().putInt(KEY_CONTEXT_SIZE, newContextSize).apply()
            field = newContextSize
        }

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        endpointUrl = sharedPreferences.getString(KEY_ENDPOINT, DEFAULT_ENDPOINT)!!
        contextSize = sharedPreferences.getInt(KEY_CONTEXT_SIZE, DEFAULT_CONTEXT_SIZE)
        Log.d("INIT Prefs", "$contextSize")
    }

}