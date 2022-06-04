package org.traderepublic.candlesticks.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.traderepublic.candlesticks.entities.Quote

@Repository
interface QuoteRepository: JpaRepository<Quote, Long>
