package com.nora.tunnel.tunnel

import com.nora.tunnel.core.model.TunnelProfile
import kotlinx.coroutines.flow.Flow

interface TunnelAdapter {

    suspend fun validate(
        profile: TunnelProfile
    ): Result<Unit>

    suspend fun prepare(
        profile: TunnelProfile
    ): Result<Unit>

    suspend fun connect(
        profile: TunnelProfile,
        vpnService: NoraVpnService
    ): Result<ConnectionState>

    suspend fun disconnect()

    fun status(): Flow<ConnectionState>

    fun statistics(): Flow<TrafficStats>

    fun diagnostics(): DiagnosticsResult
}

enum class ConnectionState {
    IDLE,
    VALIDATING,
    PREPARING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    DISCONNECTING,
    DISCONNECTED,
    ERROR
}

data class TrafficStats(
    val rxBytes: Long,
    val txBytes: Long,
    val latencyMs: Long? = null,
    val uptimeSec: Long = 0
)

data class DiagnosticsResult(
    val success: Boolean,
    val message: String,
    val latencyMs: Long? = null
)
