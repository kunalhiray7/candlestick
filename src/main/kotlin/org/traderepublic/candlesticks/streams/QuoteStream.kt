package org.traderepublic.candlesticks.streams

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.traderepublic.candlesticks.models.QuoteEvent
import org.traderepublic.candlesticks.utils.ObjectMapperUtil.Companion.getObjectMapper

@Component
class QuoteStream(
    @Value("\${partner-service.quote-stream-url}")
    private val uriString: String
) {

    private val wsURI = Uri.of(uriString)

    private lateinit var ws: Websocket

    fun connect(onEvent: (QuoteEvent) -> Unit) {
        ws = WebsocketClient.nonBlocking(wsURI) { println("Connected quote stream") }

        ws.onMessage {
            val event = getObjectMapper().readValue<QuoteEvent>(it.body.stream)
            onEvent(event)
        }

        ws.onClose {
            println("Disconnected quote stream: ${it.code}; ${it.description}")
            runBlocking {
                launch {
                    delay(5000L)
                    println("Attempting reconnect for quote stream")
                    connect(onEvent)
                }
            }
        }

        ws.onError {
            println("Exception in quote stream: $it")
        }
    }
}
