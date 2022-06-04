package org.traderepublic.candlesticks.services

import org.springframework.stereotype.Service
import org.traderepublic.candlesticks.mappers.InstrumentEventToEntityMapper.Companion.toInstrumentEntity
import org.traderepublic.candlesticks.models.Candlestick
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.models.InstrumentEvent.Type.ADD
import org.traderepublic.candlesticks.models.InstrumentEvent.Type.DELETE
import org.traderepublic.candlesticks.repositories.InstrumentRepository

@Service
class CandleStickService(private val instrumentRepository: InstrumentRepository) {
    fun getCandleSticks(isin: String): List<Candlestick> {
        TODO("Not yet implemented")
    }

    fun handleInstrumentEvent(instrumentEvent: InstrumentEvent) {
        val instrumentEntity = toInstrumentEntity(instrumentEvent)
        when(instrumentEvent.type) {
            ADD -> instrumentRepository.save(instrumentEntity)
            DELETE -> instrumentRepository.delete(instrumentEntity)
        }
    }

}
