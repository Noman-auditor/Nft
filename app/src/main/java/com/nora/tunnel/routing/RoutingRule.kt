package com.nora.tunnel.routing

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "routing_rules")
data class RoutingRule(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val type: RuleType,

    val value: String,

    val action: RouteAction,

    val enabled: Boolean = true,

    val priority: Int = 0
)

enum class RuleType {
    DOMAIN,
    IP_CIDR,
    APP,
    GEOIP
}

enum class RouteAction {
    PROXY,
    DIRECT,
    BLOCK
} 
