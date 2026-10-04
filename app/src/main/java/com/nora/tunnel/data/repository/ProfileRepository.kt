package com.nora.tunnel.data.repository

import com.nora.tunnel.core.model.TunnelProfile
import com.nora.tunnel.data.database.ProfileDao
import com.nora.tunnel.data.secure.SecureStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val dao: ProfileDao,
    private val secureStorage: SecureStorage
) {

    fun observeProfiles() =
        dao.observeAll()

    suspend fun getById(
        id: String
    ): TunnelProfile? =
        dao.getById(id)

    suspend fun save(
        profile: TunnelProfile,
        passwordOrKey: String?
    ) {
        dao.upsert(profile)

        if (!passwordOrKey.isNullOrBlank()) {
            secureStorage.saveSecret(
                profile.id,
                passwordOrKey
            )
        }
    }

    suspend fun delete(
        profile: TunnelProfile
    ) {
        dao.delete(profile)
        secureStorage.deleteSecret(profile.id)
    }
}
