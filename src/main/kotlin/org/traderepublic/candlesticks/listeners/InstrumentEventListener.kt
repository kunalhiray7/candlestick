package org.traderepublic.candlesticks.listeners

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.traderepublic.candlesticks.services.InstrumentService
import org.traderepublic.candlesticks.streams.InstrumentStream

@Component
class InstrumentEventListener(
    private val instrumentStream: InstrumentStream,
    private val instrumentService: InstrumentService
) {

    private val logger = LoggerFactory.getLogger(InstrumentEventListener::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun listenAfterStartup() {
        instrumentStream.connect { event ->
            logger.info("Received instrument event: $event")
            instrumentService.handleInstrumentEvent(event)
        }
    }
}
