package com.wire.bots.narvi.tracking.github

import com.wire.bots.sdk.tools.Util

/**
 * Validates payload sent by Github to the bot.
 */
class GithubWebhookValidator(
    private val githubSignSecret: String
) {
    /**
     * Returns true if signature match the payload.
     */
    fun isPayloadValid(signature: String, payload: String): Boolean {
        val hmacSHA1 = Util.getHmacSHA1(payload, githubSignSecret)
        val challenge = "sha1=$hmacSHA1"
        return challenge == signature
    }
}
