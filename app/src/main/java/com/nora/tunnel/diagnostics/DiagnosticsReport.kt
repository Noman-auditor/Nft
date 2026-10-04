package com.nora.tunnel.diagnostics

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.nora.tunnel.BuildConfig
import com.nora.tunnel.log.LogRedactor
import com.nora.tunnel.tunnel.ConnectionState

object DiagnosticsReport {

    fun generate(
        context: Context,
        state: ConnectionState,
        protocol: String = "Unknown",
        core: String = "Unknown",
        transport: String = "Unknown",
        dns: String = "Unknown",
        recentErrors: List<String> = emptyList()
    ): String {

        val network = getNetworkType(context)
        val architecture = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"

        val safeErrors = if (recentErrors.isEmpty()) {
            "[none]"
        } else {
            recentErrors
                .map { LogRedactor.redact(it) }
                .joinToString("; ")
        }

        return buildString {
            appendLine("Nora Tunnel Diagnostics")
            appendLine("App version: ${BuildConfig.VERSION_NAME}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Arch: $architecture")
            appendLine("VPN state: ${vpnState(state)}")
            appendLine("Protocol: $protocol / Core: $core / Transport: $transport")
            appendLine("Connection state: ${state.name}")
            appendLine("DNS: ${LogRedactor.redact(dns)}")
            appendLine("Network: $network")
            appendLine("Recent errors: $safeErrors")
        }.trimEnd()
    }

    private fun vpnState(state: ConnectionState): String {
        return when (state) {
            ConnectionState.CONNECTED,
            ConnectionState.CONNECTING,
            ConnectionState.RECONNECTING,
            ConnectionState.PREPARING,
            ConnectionState.VALIDATING -> "ACTIVE"

            else -> "INACTIVE"
        }
    }

    private fun getNetworkType(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager
                ?: return "UNKNOWN"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
                ?: return "OFFLINE"

            val capabilities =
                connectivityManager.getNetworkCapabilities(network)
                    ?: return "UNKNOWN"

            when {
                capabilities.hasTransport(
                    android.net.NetworkCapabilities.TRANSPORT_WIFI
                ) -> "WIFI"

                capabilities.hasTransport(
                    android.net.NetworkCapabilities.TRANSPORT_CELLULAR
                ) -> "CELLULAR"

                capabilities.hasTransport(
                    android.net.NetworkCapabilities.TRANSPORT_ETHERNET
                ) -> "ETHERNET"

                else -> "OTHER"
            }
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.typeName ?: "UNKNOWN"
        }
    }
}
