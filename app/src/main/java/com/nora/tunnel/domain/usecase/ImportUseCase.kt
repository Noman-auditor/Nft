package com.nora.tunnel.domain.usecase

import com.nora.tunnel.core.CapabilityRegistry
import com.nora.tunnel.core.model.Core
import com.nora.tunnel.core.model.Protocol
import com.nora.tunnel.core.model.Transport
import com.nora.tunnel.core.model.TunnelProfile
import com.nora.tunnel.domain.model.ImportResult
import java.util.UUID

class ImportUseCase {

    fun import(input: String): ImportResult {

        val trimmed = input.trim()

        if (trimmed.isBlank()) {
            return ImportResult.Error(
                field = "input",
                reason = "Configuration is empty",
                action = "Paste a valid VPN configuration"
            )
        }

        return when {
            trimmed.startsWith("wireguard://", ignoreCase = true) ||
                trimmed.contains("[Interface]", ignoreCase = true) ->
                parseWireGuard(trimmed)

            trimmed.startsWith("vless://", ignoreCase = true) ->
                parseVless(trimmed)

            trimmed.startsWith("vmess://", ignoreCase = true) ->
                parseVmess(trimmed)

            trimmed.startsWith("trojan://", ignoreCase = true) ->
                parseTrojan(trimmed)

            trimmed.startsWith("ss://", ignoreCase = true) ->
                parseShadowsocks(trimmed)

            trimmed.startsWith("{") ->
                parseJson(trimmed)

            trimmed.contains("client", ignoreCase = true) &&
                trimmed.contains("remote", ignoreCase = true) ->
                parseOpenVpn(trimmed)

            else ->
                ImportResult.Error(
                    field = "format",
                    reason = "Unknown configuration format",
                    action = "Check the URI or configuration and try again"
                )
        }
    }

    private fun parseVless(
        uri: String
    ): ImportResult {

        return try {

            val parsed = java.net.URI(uri)

            val host = parsed.host
            val port = parsed.port

            if (host.isNullOrBlank()) {
                return ImportResult.Error(
                    "host",
                    "Server address is missing",
                    "Check the VLESS URI"
                )
            }

            if (port !in 1..65535) {
                return ImportResult.Error(
                    "port",
                    "Invalid server port",
                    "Check the VLESS URI"
                )
            }

            val transport = detectTransport(
                parsed.rawQuery
            )

            val core = Core.XRAY

            if (
                !CapabilityRegistry.isValid(
                    Protocol.VLESS,
                    core,
                    transport
                )
            ) {
                return ImportResult.Error(
                    "transport",
                    "Unsupported VLESS transport",
                    "Choose a supported transport"
                )
            }

            ImportResult.Preview(
                TunnelProfile(
                    id = UUID.randomUUID().toString(),
                    name = "Imported VLESS",
                    protocol = Protocol.VLESS,
                    core = core,
                    transport = transport,
                    serverAddress = host,
                    serverPort = port,
                    config = uri
                )
            )

        } catch (e: Exception) {

            ImportResult.Error(
                "uri",
                e.message ?: "VLESS parsing failed",
                "Verify that the complete URI was copied"
            )
        }
    }

    private fun parseVmess(
        uri: String
    ): ImportResult {

        return createUnsupportedPreviewError(
            "VMess parser is not implemented yet"
        )
    }

    private fun parseWireGuard(
        input: String
    ): ImportResult {

        return createUnsupportedPreviewError(
            "WireGuard parser is not implemented yet"
        )
    }

    private fun parseTrojan(
        uri: String
    ): ImportResult {

        return createUnsupportedPreviewError(
            "Trojan parser is not implemented yet"
        )
    }

    private fun parseShadowsocks(
        uri: String
    ): ImportResult {

        return createUnsupportedPreviewError(
            "Shadowsocks parser is not implemented yet"
        )
    }

    private fun parseJson(
        json: String
    ): ImportResult {

        return createUnsupportedPreviewError(
            "Xray/sing-box JSON parser is not implemented yet"
        )
    }

    private fun parseOpenVpn(
        config: String
    ): ImportResult {

        return createUnsupportedPreviewError(
            "OpenVPN parser is not implemented yet"
        )
    }

    private fun detectTransport(
        query: String?
    ): Transport {

        val value = query.orEmpty().lowercase()

        return when {
            "type=ws" in value -> Transport.WS
            "type=grpc" in value -> Transport.GRPC
            "type=quic" in value -> Transport.QUIC
            else -> Transport.TCP
        }
    }

    private fun createUnsupportedPreviewError(
        message: String
    ): ImportResult {

        return ImportResult.Error(
            field = "parser",
            reason = message,
            action = "Use a supported configuration format"
        )
    }
}
