package com.nora.tunnel.data.secure

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(
    context: Context
) {

    private val masterKey =
        MasterKey.Builder(context)
            .setKeyScheme(
                MasterKey.KeyScheme.AES256_GCM
            )
            .build()

    private val prefs =
        EncryptedSharedPreferences.create(
            context,
            "secure_creds",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveSecret(
        profileId: String,
        password: String
    ) {
        prefs.edit()
            .putString(profileId, password)
            .apply()
    }

    fun getSecret(
        profileId: String
    ): String? {
        return prefs.getString(profileId, null)
    }

    fun deleteSecret(
        profileId: String
    ) {
        prefs.edit()
            .remove(profileId)
            .apply()
    }

    fun redact(
        log: String
    ): String {
        return log.replace(
            Regex(
                "(password|privateKey|token|secret)=[^\\s]+",
                RegexOption.IGNORE_CASE
            ),
            "$1=******"
        )
    }
}
