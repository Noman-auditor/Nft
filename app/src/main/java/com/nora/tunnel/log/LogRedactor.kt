package com.nora.tunnel.log

object LogRedactor {

    private val patterns = listOf(
        Regex("""password\s*=\s*\S+""", RegexOption.IGNORE_CASE),
        Regex("""privateKey\s*=\s*\S+""", RegexOption.IGNORE_CASE),
        Regex("""token\s*=\s*\S+""", RegexOption.IGNORE_CASE),
        Regex("""psk\s*=\s*\S+""", RegexOption.IGNORE_CASE),
        Regex("""secret\s*=\s*\S+""", RegexOption.IGNORE_CASE),
        Regex("""authorization\s*:\s*\S+""", RegexOption.IGNORE_CASE),
        Regex("""bearer\s+\S+""", RegexOption.IGNORE_CASE)
    )

    fun redact(line: String): String {
        var result = line

        patterns.forEach { regex ->
            result = result.replace(regex) { match ->
                val text = match.value
                val separatorIndex = text.indexOf('=')

                if (separatorIndex >= 0) {
                    text.substring(0, separatorIndex + 1) + "******"
                } else {
                    val colonIndex = text.indexOf(':')
                    if (colonIndex >= 0) {
                        text.substring(0, colonIndex + 1) + "******"
                    } else {
                        "******"
                    }
                }
            }
        }

        return result
    }

    fun redactLines(lines: List<String>): List<String> =
        lines.map(::redact)
}
