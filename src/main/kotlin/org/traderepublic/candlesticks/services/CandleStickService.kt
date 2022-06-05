package org.traderepublic.candlesticks.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.traderepublic.candlesticks.mappers.ModelToEntityMapper.Companion.toInstrumentEntity
import org.traderepublic.candlesticks.mappers.ModelToEntityMapper.Companion.toQuoteEntity
import org.traderepublic.candlesticks.models.Candlestick
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.models.InstrumentEvent.Type.ADD
import org.traderepublic.candlesticks.models.InstrumentEvent.Type.DELETE
import org.traderepublic.candlesticks.models.QuoteEvent
import org.traderepublic.candlesticks.repositories.InstrumentRepository
import org.traderepublic.candlesticks.repositories.QuoteRepository
import java.time.Instant

@Service
class CandleStickService(
    private val instrumentRepository: InstrumentRepository,
    private val quoteRepository: QuoteRepository,
    @Value("\${app.quote-fetch-threshold-in-seconds}")
    private val quoteFetchThresholdInSeconds: Long
) {

    private val logger = LoggerFactory.getLogger(CandleStickService::class.java)

    fun getCandleSticks(isin: String): List<Candlestick> {
        val creationTimestampThreshold = Instant.now().minusSeconds(quoteFetchThresholdInSeconds)
        println(creationTimestampThreshold)
        quoteRepository.findAllByIsinAndWithCreationDateTimeAfter(isin, creationTimestampThreshold)
        return emptyList()
    }

    fun handleInstrumentEvent(instrumentEvent: InstrumentEvent) {
        val instrumentEntity = toInstrumentEntity(instrumentEvent)
        when (instrumentEvent.type) {
            ADD -> instrumentRepository.save(instrumentEntity)
            DELETE -> instrumentRepository.delete(instrumentEntity)
        }
    }

    fun handleQuoteEvent(quoteEvent: QuoteEvent) {
        val instrument = instrumentRepository.findById(quoteEvent.data.isin)
        if (instrument.isPresent) {
            val quote = toQuoteEntity(quoteEvent)
            quote.instrument = instrument.get()
            quoteRepository.save(quote)
        } else {
            logger.warn("Cannot save quote, No instrument found for ISIN - ${quoteEvent.data.isin}")
        }
    }

}
