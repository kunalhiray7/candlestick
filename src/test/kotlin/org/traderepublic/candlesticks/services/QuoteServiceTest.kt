package org.traderepublic.candlesticks.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.traderepublic.candlesticks.entities.Instrument
import org.traderepublic.candlesticks.models.Quote
import org.traderepublic.candlesticks.models.QuoteEvent
import org.traderepublic.candlesticks.repositories.InstrumentRepository
import org.traderepublic.candlesticks.repositories.QuoteRepository
import org.traderepublic.candlesticks.utils.TestUtil.Companion.capture
import java.util.*

@ExtendWith(MockitoExtension::class)
class QuoteServiceTest {

    @Mock
    private lateinit var quoteRepository: QuoteRepository

    @Mock
    private lateinit var instrumentRepository: InstrumentRepository

    @InjectMocks
    private lateinit var service: QuoteService

    @Captor
    private lateinit var quoteCaptor: ArgumentCaptor<org.traderepublic.candlesticks.entities.Quote>

    @Test
    fun `handleQuoteEvent() should call repository to save the quotes`() {
        val quoteEvent = QuoteEvent(Quote(isin = "123abc", price = 123.0))
        val instrument = Instrument(isin = quoteEvent.data.isin, description = "new instrument")
        val quote =
            org.traderepublic.candlesticks.entities.Quote(price = quoteEvent.data.price, instrument = instrument)
        doReturn(Optional.of(instrument)).`when`(instrumentRepository).findById(quoteEvent.data.isin)

        service.handleQuoteEvent(quoteEvent)

        verify(instrumentRepository, times(1)).findById(quoteEvent.data.isin)
        verify(quoteRepository, times(1)).save(capture(quoteCaptor))
        assertEquals(quote.instrument, quoteCaptor.value.instrument)
        assertEquals(quote.price, quoteCaptor.value.price)
    }

    @Test
    fun `handleQuoteEvent() should not call repository to save the quotes if corresponding instrument does not exist`() {
        val quoteEvent = QuoteEvent(Quote(isin = "123abc", price = 123.0))
        val instrument = Instrument(isin = quoteEvent.data.isin, description = "new instrument")
        val quote =
            org.traderepublic.candlesticks.entities.Quote(price = quoteEvent.data.price, instrument = instrument)
        doReturn(Optional.ofNullable(null)).`when`(instrumentRepository).findById(quoteEvent.data.isin)

        service.handleQuoteEvent(quoteEvent)

        verify(instrumentRepository, times(1)).findById(quoteEvent.data.isin)
        verify(quoteRepository, times(0)).save(quote)
    }

}
