package com.bisc.portal.util

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtil {
    private const val RESET_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    fun generateResetCode(): String {
        val rng = SecureRandom()
        return (0 until 16)
            .map { RESET_CODE_CHARS[rng.nextInt(RESET_CODE_CHARS.length)] }
            .chunked(4)
            .joinToString("-") { it.joinToString("") }
    }

    fun hashResetCode(code: String): String {
        val normalized = code.uppercase().replace("-", "").replace(" ", "")
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(normalized.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
