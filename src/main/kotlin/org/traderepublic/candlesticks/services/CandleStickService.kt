package org.traderepublic.candlesticks.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.traderepublic.candlesticks.entities.Quote
import org.traderepublic.candlesticks.models.Candlestick
import org.traderepublic.candlesticks.repositories.QuoteRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class CandleStickService(
    private val quoteRepository: QuoteRepository,
    @Value("\${app.quote-fetch-threshold-in-seconds}")
    private val quoteFetchThresholdInSeconds: Long
) {

    private val logger = LoggerFactory.getLogger(CandleStickService::class.java)

    fun getCandleSticks(isin: String): List<Candlestick> {
        val creationTimestampThreshold = Instant.now().minusSeconds(quoteFetchThresholdInSeconds)
        println(creationTimestampThreshold)
        val quotes = quoteRepository.findAllByIsinAndWithCreationDateTimeAfter(isin, creationTimestampThreshold)
        return prepareCandleSticks(quotes)
    }

    private fun prepareCandleSticks(quotes: List<Quote>): List<Candlestick> {
        val sortedQuotes = quotes.sortedBy { it.creationTimestamp }
        var floorTimestamp = getFloorTimeStamp(sortedQuotes[0])
        val ceilTimestamp = getCeilTimestamp(sortedQuotes.last())
        var nextTimestamp: Instant
        val chunks = mutableMapOf<Instant, List<Quote>>()

        while (floorTimestamp.isBefore(ceilTimestamp)) {
            nextTimestamp = floorTimestamp.plusSeconds(59)
            val chunk = sortedQuotes.groupBy { it.creationTimestamp in floorTimestamp..nextTimestamp }
            chunk[true]?.let { chunks.put(floorTimestamp, it) }
            floorTimestamp = nextTimestamp.plusSeconds(1)
        }

        return chunksToCandleSticks(chunks)
    }

    private fun chunksToCandleSticks(chunks: MutableMap<Instant, List<Quote>>) = chunks.map {
        Candlestick(
            openTimestamp = it.key,
            closeTimestamp = getCeilTimestamp(it.value.last()),
            openPrice = it.value.first().price,
            highPrice = it.value.maxOf { q -> q.price },
            lowPrice = it.value.minOf { q -> q.price },
            closingPrice = it.value.last().price
        )
    }

    private fun getFloorTimeStamp(quote: Quote): Instant {
        val zdt = ZonedDateTime.ofInstant(quote.creationTimestamp, ZoneId.of("UTC"))
        return quote.creationTimestamp.minusSeconds(zdt.second.toLong())
    }

    private fun getCeilTimestamp(quote: Quote): Instant {
        return getFloorTimeStamp(quote).plusSeconds(60L)
    }

}
