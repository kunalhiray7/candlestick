package org.traderepublic.candlesticks.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.entities.Instrument
import org.traderepublic.candlesticks.entities.Quote
import org.traderepublic.candlesticks.models.QuoteEvent
import org.traderepublic.candlesticks.repositories.InstrumentRepository
import org.traderepublic.candlesticks.repositories.QuoteRepository
import java.util.*

@ExtendWith(MockitoExtension::class)
class CandleStickServiceTest {

    @Mock
    private lateinit var quoteRepository: QuoteRepository

    @Mock
    private lateinit var instrumentRepository: InstrumentRepository

    @InjectMocks
    private lateinit var service: CandleStickService

    @Test
    fun `handleInstrumentEvent() should call repository to save the instrument`() {
        val instrument = Instrument(isin = "123abc", description = "new instrument")
        val instrumentEvent = InstrumentEvent(
            InstrumentEvent.Type.ADD,
            org.traderepublic.candlesticks.models.Instrument(instrument.isin, instrument.description)
        )

        service.handleInstrumentEvent(instrumentEvent)

        verify(instrumentRepository, times(1)).save(instrument)
    }

    @Test
    fun `handleInstrumentEvent() should call repository to delete the instrument when event is of type DELETE`() {
        val instrument = Instrument(isin = "123abc", description = "new instrument")
        val instrumentEvent = InstrumentEvent(
            InstrumentEvent.Type.DELETE,
            org.traderepublic.candlesticks.models.Instrument(instrument.isin, instrument.description)
        )

        service.handleInstrumentEvent(instrumentEvent)

        verify(instrumentRepository, times(1)).delete(instrument)
    }

    @Test
    fun `handleQuoteEvent() should call repository to save the quotes`() {
        val quoteEvent = QuoteEvent(org.traderepublic.candlesticks.models.Quote(isin = "123abc", price = 123.0))
        val instrument = Instrument(isin = quoteEvent.data.isin, description = "new instrument")
        val quote = Quote(price = quoteEvent.data.price, instrument = instrument)
        doReturn(Optional.of(instrument)).`when`(instrumentRepository).findById(quoteEvent.data.isin)

        service.handleQuoteEvent(quoteEvent)

        verify(instrumentRepository, times(1)).findById(quoteEvent.data.isin)
        verify(quoteRepository, times(1)).save(quote)
    }

    @Test
    fun `handleQuoteEvent() should not call repository to save the quotes if corresponding instrument does not exist`() {
        val quoteEvent = QuoteEvent(org.traderepublic.candlesticks.models.Quote(isin = "123abc", price = 123.0))
        val instrument = Instrument(isin = quoteEvent.data.isin, description = "new instrument")
        val quote = Quote(price = quoteEvent.data.price, instrument = instrument)
        doReturn(Optional.ofNullable(null)).`when`(instrumentRepository).findById(quoteEvent.data.isin)

        service.handleQuoteEvent(quoteEvent)

        verify(instrumentRepository, times(1)).findById(quoteEvent.data.isin)
        verify(quoteRepository, times(0)).save(quote)
    }
}
