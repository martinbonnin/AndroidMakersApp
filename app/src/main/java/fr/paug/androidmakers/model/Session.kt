package fr.paug.androidmakers.model

import androidx.annotation.StringRes
import android.text.TextUtils

import fr.paug.androidmakers.R

class Session(val title: String,
              val description: String?,
              val language: String,
              val speakers: List<String>,
              val subtype: String,
              val type: String,
              val experience: String,
              val videoURL: String?) {
    val presentation = ""

    val languageName: Int
        @StringRes
        get() = Session.getLanguageFullName(this.language)

    companion object {
        @StringRes
        fun getLanguageFullName(abbreviatedVersion: String): Int {
            if (!TextUtils.isEmpty(abbreviatedVersion)) {
                if ("en".equals(abbreviatedVersion, ignoreCase = true)) {
                    return R.string.english
                } else if ("fr".equals(abbreviatedVersion, ignoreCase = true)) {
                    return R.string.french
                }
            }
            return R.string.no_language
        }
    }

}
