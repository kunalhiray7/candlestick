package org.traderepublic.candlesticks.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.traderepublic.candlesticks.models.Candlestick
import org.traderepublic.candlesticks.repositories.QuoteRepository
import java.time.Instant

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
        quoteRepository.findAllByIsinAndWithCreationDateTimeAfter(isin, creationTimestampThreshold)
        return emptyList()
    }

}
