package org.traderepublic.candlesticks.listeners

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.traderepublic.candlesticks.services.QuoteService
import org.traderepublic.candlesticks.streams.QuoteStream

@Component
class QuoteEventListener(
    private val quoteStream: QuoteStream,
    private val quoteService: QuoteService
) {

    @EventListener(ApplicationReadyEvent::class)
    fun listenAfterStartup() {
        quoteStream.connect { event ->
            println(event)
            quoteService.handleQuoteEvent(event)
        }
    }
}
