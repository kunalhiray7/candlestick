package org.traderepublic.candlesticks.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.traderepublic.candlesticks.entities.Instrument
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.repositories.InstrumentRepository
import utils.TestUtil.Companion.capture

@ExtendWith(MockitoExtension::class)
class InstrumentServiceTest {

    @Mock
    private lateinit var instrumentRepository: InstrumentRepository

    @InjectMocks
    private lateinit var service: InstrumentService

    @Captor
    private lateinit var instrumentCaptor: ArgumentCaptor<Instrument>

    @Test
    fun `handleInstrumentEvent() should call repository to save the instrument`() {
        val instrument = Instrument(isin = "123abc", description = "new instrument")
        val instrumentEvent = InstrumentEvent(
            InstrumentEvent.Type.ADD,
            org.traderepublic.candlesticks.models.Instrument(instrument.isin, instrument.description)
        )

        service.handleInstrumentEvent(instrumentEvent)

        verify(instrumentRepository, times(1)).save(capture(instrumentCaptor))
        assertEquals(instrument.isin, instrumentCaptor.value.isin)
        assertEquals(instrument.description, instrumentCaptor.value.description)
    }

    @Test
    fun `handleInstrumentEvent() should call repository to delete the instrument when event is of type DELETE`() {
        val instrument = Instrument(isin = "123abc", description = "new instrument")
        val instrumentEvent = InstrumentEvent(
            InstrumentEvent.Type.DELETE,
            org.traderepublic.candlesticks.models.Instrument(instrument.isin, instrument.description)
        )

        service.handleInstrumentEvent(instrumentEvent)

        verify(instrumentRepository, times(1)).delete(capture(instrumentCaptor))
        assertEquals(instrument.isin, instrumentCaptor.value.isin)
        assertEquals(instrument.description, instrumentCaptor.value.description)
    }
}
