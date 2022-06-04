package org.traderepublic.candlesticks.listeners

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.traderepublic.candlesticks.streams.InstrumentStream

@Component
class InstrumentEventListener(private val instrumentStream: InstrumentStream) {

    @EventListener(ApplicationReadyEvent::class)
    fun listenAfterStartup() {
        instrumentStream.connect { event ->
            // TODO - implement
            println(event)
        }
    }
}