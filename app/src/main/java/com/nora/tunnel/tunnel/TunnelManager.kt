package com.nora.tunnel.tunnel

import com.nora.tunnel.core.model.Core
import com.nora.tunnel.core.model.TunnelProfile
import dagger.hilt.android.scopes.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class TunnelManager @Inject constructor(
    private val adapters: Map<Core, @JvmSuppressWildcards TunnelAdapter>
) {

    private val _state = MutableStateFlow(ConnectionState.IDLE)
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    suspend fun connect(
        profile: TunnelProfile,
        service: NoraVpnService
    ) {
        val adapter = adapters[profile.core] ?: run {
            _state.value = ConnectionState.ERROR
            return
        }

        _state.value = ConnectionState.VALIDATING

        adapter.validate(profile)
            .onFailure {
                _state.value = ConnectionState.ERROR
                return
            }

        _state.value = ConnectionState.PREPARING

        adapter.prepare(profile)
            .onFailure {
                _state.value = ConnectionState.ERROR
                return
            }

        _state.value = ConnectionState.CONNECTING

        adapter.connect(profile, service)
            .onSuccess {
                _state.value = ConnectionState.CONNECTED
            }
            .onFailure {
                _state.value = ConnectionState.ERROR
            }
    }

    suspend fun disconnect() {
        _state.value = ConnectionState.DISCONNECTING

        adapters.values.forEach { adapter ->
            runCatching {
                adapter.disconnect()
            }
        }

        _state.value = ConnectionState.DISCONNECTED
    }
}
