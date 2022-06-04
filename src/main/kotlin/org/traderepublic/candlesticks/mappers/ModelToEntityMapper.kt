package org.traderepublic.candlesticks.mappers

import org.traderepublic.candlesticks.entities.Instrument
import org.traderepublic.candlesticks.entities.Quote
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.models.QuoteEvent

class ModelToEntityMapper {

    companion object {
        fun toInstrumentEntity(instrumentEvent: InstrumentEvent) = Instrument(
            isin = instrumentEvent.data.isin,
            description = instrumentEvent.data.description
        )

        fun toQuoteEntity(quoteEvent: QuoteEvent) = Quote(
            price = quoteEvent.data.price
        )
    }
}
