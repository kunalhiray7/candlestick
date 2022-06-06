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
        val quotes = quoteRepository.findAllByIsinAndWithCreationDateTimeAfter(isin, creationTimestampThreshold)
        logger.info("Total number of quotes found for ISIN: $isin and creationTimestampThreshold: $creationTimestampThreshold are: ${quotes.size}")
        return if (quotes.isEmpty()) emptyList() else prepareCandleSticks(quotes)
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
        logger.info("No of chunks created: ${chunks.size}")
        return chunksToCandleSticks(chunks)
    }

    private fun chunksToCandleSticks(chunks: MutableMap<Instant, List<Quote>>): List<Candlestick> {

        var prevChunkOpeningTime: Instant? = null
        val candleSticks = mutableListOf<Candlestick>()

        chunks.forEach { (k, v) ->
            if(prevChunkOpeningTime != null && !prevChunkOpeningTime!!.plusSeconds(60).equals(k)) {
                candleSticks.add(toSingleCandleStick(prevChunkOpeningTime!!.plusSeconds(60), chunks[prevChunkOpeningTime]!!, prevChunkOpeningTime!!.plusSeconds(120)))
            }
            candleSticks.add(toSingleCandleStick(k, v, getCeilTimestamp(v.last())))
            prevChunkOpeningTime = k
        }

        return candleSticks
    }

    private fun toSingleCandleStick(openTimestamp: Instant, value: List<Quote>, closeTimestamp: Instant) = Candlestick(
        openTimestamp = openTimestamp,
        closeTimestamp = closeTimestamp,
        openPrice = value.first().price,
        highPrice = value.maxOf { q -> q.price },
        lowPrice = value.minOf { q -> q.price },
        closingPrice = value.last().price
    )

    private fun getFloorTimeStamp(quote: Quote): Instant {
        val zdt = ZonedDateTime.ofInstant(quote.creationTimestamp, ZoneId.of("UTC"))
        return quote.creationTimestamp.minusSeconds(zdt.second.toLong())
    }

    private fun getCeilTimestamp(quote: Quote): Instant {
        return getFloorTimeStamp(quote).plusSeconds(60L)
    }

}
