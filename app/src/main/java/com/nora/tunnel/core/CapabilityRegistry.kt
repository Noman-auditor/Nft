package com.nora.tunnel.core

import com.nora.tunnel.core.model.Core
import com.nora.tunnel.core.model.Protocol
import com.nora.tunnel.core.model.Transport

object CapabilityRegistry {

    data class Capability(
        val core: Core,
        val protocols: Set<Protocol>,
        val transports: Set<Transport>
    )

    val registry = listOf(
        Capability(
            Core.XRAY,
            setOf(
                Protocol.VLESS,
                Protocol.VMESS,
                Protocol.TROJAN,
                Protocol.SHADOWSOCKS
            ),
            setOf(
                Transport.TCP,
                Transport.WS,
                Transport.GRPC,
                Transport.QUIC,
                Transport.TLS
            )
        ),

        Capability(
            Core.SINGBOX,
            setOf(
                Protocol.VLESS,
                Protocol.HYSTERIA2,
                Protocol.TUIC,
                Protocol.SHADOWSOCKS
            ),
            setOf(
                Transport.TCP,
                Transport.UDP,
                Transport.WS,
                Transport.QUIC
            )
        ),

        Capability(
            Core.WIREGUARD,
            setOf(Protocol.WIREGUARD),
            setOf(Transport.UDP)
        ),

        Capability(
            Core.OPENVPN,
            setOf(Protocol.OPENVPN),
            setOf(
                Transport.TCP,
                Transport.UDP
            )
        ),

        Capability(
            Core.SSH,
            setOf(Protocol.SSH),
            setOf(
                Transport.TCP,
                Transport.WS,
                Transport.TLS
            )
        )
    )

    fun getTransports(
        protocol: Protocol,
        core: Core
    ): Set<Transport> {
        return registry
            .find {
                it.core == core &&
                protocol in it.protocols
            }
            ?.transports
            ?: emptySet()
    }

    fun isValid(
        protocol: Protocol,
        core: Core,
        transport: Transport
    ): Boolean {
        return transport in getTransports(protocol, core)
    }
}
