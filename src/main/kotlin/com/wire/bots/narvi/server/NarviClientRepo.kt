package com.wire.bots.narvi.server

import com.wire.bots.sdk.ClientRepo
import com.wire.bots.sdk.factories.CryptoFactory
import com.wire.bots.sdk.factories.StorageFactory
import com.wire.bots.sdk.user.API
import com.wire.bots.sdk.user.UserClient
import java.util.UUID
import javax.ws.rs.client.Client


/**
 * Factory that allows to create [NarviWireClient].
 */
class NarviClientRepo(
    client: Client,
    cf: CryptoFactory,
    sf: StorageFactory,
    private val userId: UUID
) : ClientRepo(client, cf, sf) {

    override fun getClient(convId: UUID): NarviWireClient {
        val state = sf.create(userId).state
        val crypto = cf.create(userId)
        val api = API(httpClient, convId, state.token)
        val client = UserClient(userId, state.client, convId, crypto, api)
        return NarviWireClient(client)
    }

    override fun purgeBot(botId: UUID) {}
}
