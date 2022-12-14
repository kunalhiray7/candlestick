package org.traderepublic.candlesticks.services

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.traderepublic.candlesticks.entities.Instrument
import org.traderepublic.candlesticks.entities.Quote
import org.traderepublic.candlesticks.exceptions.NoInstrumentFoundException
import org.traderepublic.candlesticks.models.ISIN
import org.traderepublic.candlesticks.repositories.InstrumentRepository
import org.traderepublic.candlesticks.repositories.QuoteRepository
import org.traderepublic.candlesticks.utils.TestUtil.Companion.capture
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CandleStickServiceTest {

    private val quoteFetchThresholdInSeconds = 300L

    @Mock
    private lateinit var quoteRepository: QuoteRepository

    private lateinit var service: CandleStickService

    @Captor
    private lateinit var instantCaptor: ArgumentCaptor<Instant>

    @Captor
    private lateinit var isinCaptor: ArgumentCaptor<ISIN>

    @Mock
    private lateinit var instrumentRepository: InstrumentRepository

    @BeforeEach
    fun setUp() {
        service = CandleStickService(quoteRepository, quoteFetchThresholdInSeconds, instrumentRepository)
    }

    @Test
    fun `getCandleSticks() should fetch quotes for given isin for last n minutes`() {
        val isin = "123abc"
        val time = Instant.now().minusSeconds(quoteFetchThresholdInSeconds)
        val instrument = Instrument(isin = isin, description = "Some instrument")
        val quotes = listOf(
            Quote(1L, 10.0, LocalDateTime.of(2019, 2, 5, 13, 0, 5).toInstant(UTC), instrument))
        doReturn(Optional.of(instrument)).`when`(instrumentRepository).findById(isin)
        doReturn(quotes).`when`(quoteRepository).findAllByIsinAndWithCreationDateTimeAfter(any(), any())

        service.getCandleSticks(isin)

        verify(instrumentRepository, times(1)).findById(isin)
        verify(quoteRepository, times(1)).findAllByIsinAndWithCreationDateTimeAfter(
            capture(isinCaptor),
            capture(instantCaptor)
        )
        assertEquals(isin, isinCaptor.value)
        assertTrue(time.isBefore(instantCaptor.value))
    }

    @Test
    fun `getCandleSticks() should prepare the candle sticks for fetched quotes`() {
//        @2019-03-05 13:00:05 price: 10
//        @2019-03-05 13:00:06 price: 11
//        @2019-03-05 13:00:13 price: 15
//        @2019-03-05 13:00:19 price: 11
//        @2019-03-05 13:00:32 price: 13
//        @2019-03-05 13:00:49 price: 12
//        @2019-03-05 13:00:57 price: 12
//        @2019-03-05 13:01:00 price: 9
        val instrument = Instrument(isin = "123ABC", description = "Some instrument")
        val quotes = listOf(
            Quote(1L, 10.0, LocalDateTime.of(2019, 2, 5, 13, 0, 5).toInstant(UTC), instrument),
            Quote(1L, 11.0, LocalDateTime.of(2019, 2, 5, 13, 0, 6).toInstant(UTC), instrument),
            Quote(1L, 15.0, LocalDateTime.of(2019, 2, 5, 13, 0, 13).toInstant(UTC), instrument),
            Quote(1L, 11.0, LocalDateTime.of(2019, 2, 5, 13, 0, 19).toInstant(UTC), instrument),
            Quote(1L, 13.0, LocalDateTime.of(2019, 2, 5, 13, 0, 32).toInstant(UTC), instrument),
            Quote(1L, 12.0, LocalDateTime.of(2019, 2, 5, 13, 0, 49).toInstant(UTC), instrument),
            Quote(1L, 12.0, LocalDateTime.of(2019, 2, 5, 13, 0, 57).toInstant(UTC), instrument),
            Quote(1L, 9.0, LocalDateTime.of(2019, 2, 5, 13, 1, 0).toInstant(UTC), instrument),
        )
        doReturn(Optional.of(instrument)).`when`(instrumentRepository).findById(instrument.isin)
        doReturn(quotes).`when`(quoteRepository).findAllByIsinAndWithCreationDateTimeAfter(any(), any())

        val candleSticks = service.getCandleSticks(instrument.isin)

        assertEquals(2, candleSticks.size)
        val firstCandleStick = candleSticks[0]
        assertEquals(LocalDateTime.of(2019, 2, 5, 13, 0, 0).toInstant(UTC), firstCandleStick.openTimestamp)
        assertEquals(10.0, firstCandleStick.openPrice)
        assertEquals(15.0, firstCandleStick.highPrice)
        assertEquals(10.0, firstCandleStick.lowPrice)
        assertEquals(12.0, firstCandleStick.closingPrice)
        assertEquals(LocalDateTime.of(2019, 2, 5, 13, 1, 0).toInstant(UTC), firstCandleStick.closeTimestamp)

        val secondCandleStick = candleSticks[1]
        assertEquals(LocalDateTime.of(2019, 2, 5, 13, 1, 0).toInstant(UTC), secondCandleStick.openTimestamp)
        assertEquals(9.0, secondCandleStick.openPrice)
        assertEquals(9.0, secondCandleStick.highPrice)
        assertEquals(9.0, secondCandleStick.lowPrice)
        assertEquals(9.0, secondCandleStick.closingPrice)
        assertEquals(LocalDateTime.of(2019, 2, 5, 13, 2, 0).toInstant(UTC), secondCandleStick.closeTimestamp)
    }

    @Test
    fun `getCandleSticks() should return empty list if no quotes found for given isin and time threshold`() {
        val isin = "123abc"
        val instrument = Instrument(isin = isin, description = "Some instrument")
        doReturn(Optional.of(instrument)).`when`(instrumentRepository).findById(instrument.isin)
        doReturn(emptyList<Quote>()).`when`(quoteRepository).findAllByIsinAndWithCreationDateTimeAfter(any(), any())

        val candleSticks = service.getCandleSticks(isin)

        assertTrue(candleSticks.isEmpty())
    }

    @Test
    fun `getCandleSticks() should fill previous chunk candlesticks if current chunk is empty`() {
        val instrument = Instrument(isin = "123ABC", description = "Some instrument")
        val quotes = listOf(
            Quote(1L, 10.0, LocalDateTime.of(2019, 2, 5, 13, 0, 5).toInstant(UTC), instrument),
            Quote(1L, 11.0, LocalDateTime.of(2019, 2, 5, 13, 1, 6).toInstant(UTC), instrument),
            Quote(1L, 12.0, LocalDateTime.of(2019, 2, 5, 13, 3, 13).toInstant(UTC), instrument),
            Quote(1L, 13.0, LocalDateTime.of(2019, 2, 5, 13, 4, 19).toInstant(UTC), instrument),
        )
        doReturn(Optional.of(instrument)).`when`(instrumentRepository).findById(instrument.isin)
        doReturn(quotes).`when`(quoteRepository).findAllByIsinAndWithCreationDateTimeAfter(any(), any())

        val candleSticks = service.getCandleSticks(instrument.isin)

        assertEquals(5, candleSticks.size)
        val secondCandleStick = candleSticks[1]
        val thirdCandleStick = candleSticks[2]
        assertEquals(secondCandleStick.openTimestamp.plusSeconds(60), thirdCandleStick.openTimestamp)
        assertEquals(secondCandleStick.openPrice, thirdCandleStick.openPrice)
        assertEquals(secondCandleStick.closingPrice, thirdCandleStick.closingPrice)
        assertEquals(secondCandleStick.highPrice, thirdCandleStick.highPrice)
        assertEquals(secondCandleStick.lowPrice, thirdCandleStick.lowPrice)
        assertEquals(secondCandleStick.closeTimestamp.plusSeconds(60), thirdCandleStick.closeTimestamp)
    }

    @Test
    fun `getCandleSticks() should throw NoInstrumentFoundException when no instrument found for given ISIN`() {
        val isin = "123ABC"
        doReturn(Optional.ofNullable(null)).`when`(instrumentRepository).findById(isin)

        val exception = assertThrows<NoInstrumentFoundException> { service.getCandleSticks(isin) }

        assertEquals("No instrument found for ISIN: $isin", exception.message)
    }
}
