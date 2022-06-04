package org.traderepublic.candlesticks.streams

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.traderepublic.candlesticks.domain.InstrumentEvent
import org.traderepublic.candlesticks.utils.ObjectMapperUtil.Companion.getObjectMapper

@Component
class InstrumentStream(
  @Value("\${partner-service.instrument-stream-url}")
  private val uriString: String
) {

  private val uri = Uri.of(uriString)

  private lateinit var ws: Websocket

  fun connect(onEvent: (InstrumentEvent) -> Unit) {
    ws = WebsocketClient.nonBlocking(uri) { println("Connected instrument stream")}

    ws.onMessage {
      val event = getObjectMapper().readValue(it.body.stream, InstrumentEvent::class.java)
      onEvent(event)
    }

    ws.onClose {
      println("Disconnected instrument stream: ${it.code}; ${it.description}")
      runBlocking {
        launch {
          delay(5000L)
          println("Attempting reconnect for instrument stream")
          connect(onEvent)
        }
      }
    }

    ws.onError {
      println("Exception in instrument stream: $it")
    }
  }
}
