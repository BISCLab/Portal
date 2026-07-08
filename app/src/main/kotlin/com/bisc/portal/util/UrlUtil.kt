package com.bisc.portal.util

fun normalizeUrl(raw: String): String {
    val trimmed = raw.trim()
    return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed
    else "https://$trimmed"
}

fun extractDomain(url: String): String {
    return try {
        val normalized = normalizeUrl(url)
        java.net.URI(normalized).host?.removePrefix("www.") ?: normalized
    } catch (_: Exception) {
        url
    }
}
