package org.traderepublic.candlesticks.mappers

import org.junit.jupiter.api.Test
import org.traderepublic.candlesticks.models.Instrument
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.models.InstrumentEvent.Type.ADD
import org.traderepublic.candlesticks.models.Quote
import org.traderepublic.candlesticks.models.QuoteEvent
import kotlin.test.assertEquals

class ModelToEntityMapperTest {

    @Test
    fun `should return correct entity object from given instrument event`() {
        val isin = "123ABC"
        val description = "New Event"
        val instrumentEvent = InstrumentEvent(ADD, Instrument(isin, description))

        val instrumentEntity = ModelToEntityMapper.toInstrumentEntity(instrumentEvent)

        assertEquals(isin, instrumentEntity.isin)
        assertEquals(description, instrumentEntity.description)
    }

    @Test
    fun `should return correct quote entity from given quote event`() {
        val quoteEvent = QuoteEvent(Quote(isin = "123abc", price = 123.0))

        val quoteEntity = ModelToEntityMapper.toQuoteEntity(quoteEvent)

        assertEquals(quoteEvent.data.price, quoteEntity.price)
    }
}
