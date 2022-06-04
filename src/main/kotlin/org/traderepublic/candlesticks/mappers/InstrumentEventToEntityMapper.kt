package org.traderepublic.candlesticks.mappers

import org.traderepublic.candlesticks.entities.Instrument
import org.traderepublic.candlesticks.models.InstrumentEvent

class InstrumentEventToEntityMapper {

    companion object {
        fun toInstrumentEntity(instrumentEvent: InstrumentEvent) = Instrument(
            isin = instrumentEvent.data.isin,
            description = instrumentEvent.data.description
        )
    }
}
