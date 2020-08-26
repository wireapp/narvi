package com.wire.bots.narvi.server

import com.wire.bots.sdk.WireClient
import com.wire.bots.sdk.server.model.Conversation
import com.wire.bots.sdk.server.model.User
import com.wire.bots.sdk.user.UserClient
import java.util.UUID

/**
 * Simple wrapper around UserClient, new instance should be created for the conversations
 */
class NarviWireClient(
    private val wireClient: UserClient
) : WireClient by wireClient {

    fun getTeam(): UUID = wireClient.team

    fun createConversation(name: String, users: List<UUID>): Conversation =
        createConversation(name, wireClient.team, users)

    fun createConversation(name: String, teamId: UUID, users: List<UUID>): Conversation =
        wireClient.createConversation(name, teamId, users)

    fun createOne2One(teamId: UUID, userId: UUID): Conversation =
        wireClient.createOne2One(teamId, userId)

    fun leaveConversation(userId: UUID) =
        wireClient.leaveConversation(userId)

    fun addParticipants(vararg userIds: UUID): User =
        wireClient.addParticipants(*userIds)

    fun addService(serviceId: UUID, providerId: UUID): User =
        wireClient.addService(serviceId, providerId)

    fun deleteConversation(): Boolean =
        deleteConversation(wireClient.team)

    fun deleteConversation(teamId: UUID): Boolean =
        wireClient.deleteConversation(teamId)
}
