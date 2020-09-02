package com.wire.bots.narvi.server.resources

import com.wire.bots.narvi.tracking.github.GithubWebhookHandler
import com.wire.bots.narvi.tracking.github.GithubWebhookValidator
import mu.KLogging
import pw.forst.tools.katlib.newLine
import pw.forst.tools.katlib.parseJson
import pw.forst.tools.katlib.whenNull
import javax.ws.rs.Consumes
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Consumes(MediaType.APPLICATION_JSON)
@Path("/hooks/github")
class GithubResource(
    private val handler: GithubWebhookHandler,
    private val validator: GithubWebhookValidator
) {

    private companion object : KLogging()

    @POST
    fun webHook(
        @HeaderParam("X-GitHub-Event") event: String,
        @HeaderParam("X-Hub-Signature") signature: String,
        payload: String?
    ): Response {
        if (payload == null) {
            logger.warn { "Received request with no payload!" }
            return Response.status(Response.Status.BAD_REQUEST).build()
        } else if (!validator.isPayloadValid(signature, payload)) {
            logger.warn { "Payload is not " }
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        val hook = parseJson<GitWebhook>(payload)
            .whenNull { logger.warn { "It was not possible to parse GitResponse, payload:$newLine$payload" } }
            ?: return Response.status(Response.Status.BAD_REQUEST).build()

        return runCatching {
            handler.handle(event, hook)
        }.fold(
            onSuccess = {
                logger.info { "Webhook handled." }
                Response.ok().build()
            },
            onFailure = {
                logger.error(it) { "Exception during webhook handling, payload:$newLine$payload" }
                Response.serverError().build()
            }
        )
    }
}

