package org.traderepublic.candlesticks.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.traderepublic.candlesticks.entities.Quote
import java.time.Instant

@Repository
interface QuoteRepository : JpaRepository<Quote, Long> {

    @Query("select q from Quote q where q.instrument.isin = :isin and q.creationTimestamp >= :creationTimeThreshold")
    fun findAllByIsinAndWithCreationDateTimeAfter(
        @Param("isin") isin: String,
        @Param("creationTimeThreshold") creationTimeThreshold: Instant
    ): List<Quote>
}
