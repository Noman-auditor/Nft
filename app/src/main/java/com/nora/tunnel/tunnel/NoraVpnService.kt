package com.nora.tunnel.tunnel

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.nora.tunnel.core.model.TunnelProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NoraVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    private val scope = CoroutineScope(
        Dispatchers.IO + SupervisorJob()
    )

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_CONNECT -> {
                val profile =
                    intent.getParcelableExtra<TunnelProfile>(EXTRA_PROFILE)

                if (profile == null) {
                    Log.e(TAG, "Missing tunnel profile")
                    stopSelf()
                    return START_NOT_STICKY
                }

                scope.launch {
                    establishVpn(profile)
                }
            }

            ACTION_DISCONNECT -> {
                teardown()
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun establishVpn(
        profile: TunnelProfile
    ) {

        if (
            profile.serverAddress.isBlank() ||
            profile.serverPort !in 1..65535
        ) {
            Log.e(TAG, "Invalid server configuration")
            teardown()
            return
        }

        try {

            val builder = Builder()
                .addAddress("10.8.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("1.1.1.1")
                .setSession(profile.name)
                .setMtu(1500)

            vpnInterface = builder.establish()

                ?: throw IllegalStateException(
                    "VPN permission not granted"
                )

            Log.i(
                TAG,
                "VPN interface established for ${profile.name}"
            )

            /*
             * Core adapters are responsible for the actual
             * authorized tunnel implementation.
             *
             * Do not claim CONNECTED until the adapter
             * confirms a real connection.
             */

        } catch (e: Exception) {

            Log.e(
                TAG,
                "VPN setup failed",
                e
            )

            teardown()
        }
    }

    private fun teardown() {

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.w(TAG, "VPN close failed", e)
        }

        vpnInterface = null
    }

    override fun onRevoke() {
        teardown()
        super.onRevoke()
    }

    override fun onDestroy() {
        teardown()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "NoraVpnService"

        const val ACTION_CONNECT =
            "com.nora.tunnel.action.CONNECT"

        const val ACTION_DISCONNECT =
            "com.nora.tunnel.action.DISCONNECT"

        const val EXTRA_PROFILE =
            "com.nora.tunnel.extra.PROFILE"
    }
}
