package org.traderepublic.candlesticks.services

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.traderepublic.candlesticks.models.ISIN
import org.traderepublic.candlesticks.repositories.QuoteRepository
import utils.TestUtil.Companion.capture
import java.time.Instant
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

    @BeforeEach
    fun setUp() {
        service = CandleStickService(quoteRepository, quoteFetchThresholdInSeconds)
    }

    @Test
    fun `getCandleSticks() should fetch quotes for given isin for last n minutes`() {
        val isin = "123abc"
        val time = Instant.now().minusSeconds(quoteFetchThresholdInSeconds)

        service.getCandleSticks(isin)

        verify(quoteRepository, times(1)).findAllByIsinAndWithCreationDateTimeAfter(
            capture(isinCaptor),
            capture(instantCaptor)
        )
        assertEquals(isin, isinCaptor.value)
        assertTrue(time.isBefore(instantCaptor.value))
    }
}
