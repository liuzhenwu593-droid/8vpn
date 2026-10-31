package app.vpn8

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    private val supported = setOf("en", "fa", "zh")

    /** A stored en/fa/zh wins. Anything else (missing, "system", unknown) follows the system language. */
    fun effectiveLanguage(stored: String?, systemLanguage: String?): String {
        if (stored != null && stored in supported) return stored
        return when (systemLanguage) {
            "zh" -> "zh"
            "fa" -> "fa"
            else -> "en"
        }
    }

    fun wrap(base: Context): Context {
        val stored = base.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("language", null)
        // Read the system language from the unwrapped base context, before setDefault() below
        // would overwrite it with the app's effective language.
        val cfg0 = base.resources.configuration
        val systemLanguage = if (Build.VERSION.SDK_INT >= 24) {
            cfg0.locales.get(0)?.language
        } else {
            @Suppress("DEPRECATION")
            cfg0.locale?.language
        }
        val lang = effectiveLanguage(stored, systemLanguage)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val cfg = Configuration(cfg0)
        cfg.setLocale(locale)
        cfg.setLayoutDirection(locale)
        return base.createConfigurationContext(cfg)
    }
}
