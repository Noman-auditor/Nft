package com.nora.tunnel.diagnostics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import javax.net.ssl.SSLSocketFactory

enum class TestStatus {
    PASS,
    FAIL,
    NOT_SUPPORTED,
    NOT_TESTED
}

data class NetworkTestResult(
    val name: String,
    val status: TestStatus,
    val message: String,
    val latencyMs: Long? = null
)

class NetworkLab {

    suspend fun tcpTest(
        host: String,
        port: Int,
        timeoutMs: Int = 5000
    ): NetworkTestResult = withContext(Dispatchers.IO) {

        val start = System.currentTimeMillis()

        try {
            Socket().use { socket ->
                socket.connect(
                    InetSocketAddress(host, port),
                    timeoutMs
                )
            }

            NetworkTestResult(
                name = "TCP",
                status = TestStatus.PASS,
                message = "TCP connection successful",
                latencyMs = System.currentTimeMillis() - start
            )
        } catch (e: Exception) {
            NetworkTestResult(
                name = "TCP",
                status = TestStatus.FAIL,
                message = e.message ?: "TCP connection failed"
            )
        }
    }

    suspend fun dnsTest(
        host: String
    ): NetworkTestResult = withContext(Dispatchers.IO) {

        val start = System.currentTimeMillis()

        try {
            val addresses = java.net.InetAddress.getAllByName(host)

            if (addresses.isNotEmpty()) {
                NetworkTestResult(
                    name = "DNS",
                    status = TestStatus.PASS,
                    message = "DNS resolution successful",
                    latencyMs = System.currentTimeMillis() - start
                )
            } else {
                NetworkTestResult(
                    name = "DNS",
                    status = TestStatus.FAIL,
                    message = "No DNS address returned"
                )
            }
        } catch (e: Exception) {
            NetworkTestResult(
                name = "DNS",
                status = TestStatus.FAIL,
                message = e.message ?: "DNS resolution failed"
            )
        }
    }

    suspend fun tlsTest(
        host: String,
        port: Int = 443,
        timeoutMs: Int = 7000
    ): NetworkTestResult = withContext(Dispatchers.IO) {

        val start = System.currentTimeMillis()

        try {
            val factory = SSLSocketFactory.getDefault() as SSLSocketFactory

            factory.createSocket().use { raw ->
                raw.connect(
                    InetSocketAddress(host, port),
                    timeoutMs
                )

                val socket = raw as javax.net.ssl.SSLSocket
                socket.soTimeout = timeoutMs
                socket.startHandshake()
            }

            NetworkTestResult(
                name = "TLS",
                status = TestStatus.PASS,
                message = "TLS handshake successful",
                latencyMs = System.currentTimeMillis() - start
            )
        } catch (e: Exception) {
            NetworkTestResult(
                name = "TLS",
                status = TestStatus.FAIL,
                message = e.message ?: "TLS handshake failed"
            )
        }
    }

    suspend fun urlTlsTest(
        url: String
    ): NetworkTestResult {
        return try {
            val uri = URI(url)

            val host = uri.host
                ?: return NetworkTestResult(
                    name = "TLS",
                    status = TestStatus.FAIL,
                    message = "Invalid hostname"
                )

            val port = if (uri.port > 0) uri.port else 443

            tlsTest(host, port)
        } catch (e: Exception) {
            NetworkTestResult(
                name = "TLS",
                status = TestStatus.FAIL,
                message = e.message ?: "Invalid URL"
            )
        }
    }

    suspend fun runBasicDiagnostics(
        host: String,
        port: Int
    ): List<NetworkTestResult> {
        val results = mutableListOf<NetworkTestResult>()

        results += dnsTest(host)
        results += tcpTest(host, port)

        if (port == 443) {
            results += tlsTest(host, port)
        } else {
            results += NetworkTestResult(
                name = "TLS",
                status = TestStatus.NOT_TESTED,
                message = "TLS test skipped for non-443 port"
            )
        }

        return results
    }
}
