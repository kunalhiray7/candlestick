package org.traderepublic.candlesticks.services

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.traderepublic.candlesticks.mappers.ModelToEntityMapper
import org.traderepublic.candlesticks.models.QuoteEvent
import org.traderepublic.candlesticks.repositories.InstrumentRepository
import org.traderepublic.candlesticks.repositories.QuoteRepository

@Service
class QuoteService(
    private val instrumentRepository: InstrumentRepository,
    private val quoteRepository: QuoteRepository,
) {

    private val logger = LoggerFactory.getLogger(QuoteService::class.java)

    fun handleQuoteEvent(quoteEvent: QuoteEvent) {
        val instrument = instrumentRepository.findById(quoteEvent.data.isin)
        if (instrument.isPresent) {
            val quote = ModelToEntityMapper.toQuoteEntity(quoteEvent)
            quote.instrument = instrument.get()
            quoteRepository.save(quote)
        } else {
            logger.warn("Cannot save quote, No instrument found for ISIN - ${quoteEvent.data.isin}")
        }
    }
}
