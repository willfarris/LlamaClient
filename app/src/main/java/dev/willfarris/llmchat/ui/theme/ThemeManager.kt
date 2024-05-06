package dev.willfarris.llmchat.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}

object ThemeManager {
    private const val PREF_NAME = "SETTINGS"
    private const val KEY_THEME = "THEME"

    private lateinit var sharedPreferences: SharedPreferences

    var theme = mutableStateOf(Theme.SYSTEM)
        private set

    fun themeOptions(): List<String> {
        return Theme.values().map { theme -> theme.name}//.lowercase().replaceFirstChar { c -> c.uppercaseChar() } }
    }

    fun setThemeFromString(string: String) {
        val newTheme = when(string) {
            Theme.DARK.name -> Theme.DARK
            Theme.LIGHT.name -> Theme.LIGHT
            else -> Theme.SYSTEM
        }
        if (theme.value != newTheme) {
            theme.value = newTheme
            sharedPreferences.edit().putString(KEY_THEME, newTheme.name).apply()
        }
    }

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        theme.value = getSavedTheme()
    }

    private fun getSavedTheme(): Theme {
        val themeValue = sharedPreferences.getString(KEY_THEME, Theme.SYSTEM.name)
        return Theme.valueOf(themeValue ?: Theme.SYSTEM.name)
    }

}
