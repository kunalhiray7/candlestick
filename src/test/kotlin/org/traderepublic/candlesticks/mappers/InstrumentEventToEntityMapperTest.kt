package org.traderepublic.candlesticks.mappers

import org.junit.jupiter.api.Test
import org.traderepublic.candlesticks.mappers.InstrumentEventToEntityMapper.Companion.toInstrumentEntity
import org.traderepublic.candlesticks.models.Instrument
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.models.InstrumentEvent.Type.ADD
import kotlin.test.assertEquals

class InstrumentEventToEntityMapperTest {

    @Test
    fun `should return correct entity object from given instrument event`() {
        val isin = "123ABC"
        val description = "New Event"
        val instrumentEvent = InstrumentEvent(ADD, Instrument(isin, description))

        val instrumentEntity = toInstrumentEntity(instrumentEvent)

        assertEquals(isin, instrumentEntity.isin)
        assertEquals(description, instrumentEntity.description)
    }
}
