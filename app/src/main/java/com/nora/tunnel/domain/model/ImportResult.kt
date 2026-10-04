package com.nora.tunnel.domain.model

import com.nora.tunnel.core.model.TunnelProfile

sealed class ImportResult {

    data class Preview(
        val profile: TunnelProfile
    ) : ImportResult()

    data class Error(
        val field: String,
        val reason: String,
        val action: String
    ) : ImportResult()
}
