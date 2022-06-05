package org.traderepublic.candlesticks.services

import org.springframework.stereotype.Service
import org.traderepublic.candlesticks.mappers.ModelToEntityMapper
import org.traderepublic.candlesticks.models.InstrumentEvent
import org.traderepublic.candlesticks.repositories.InstrumentRepository

@Service
class InstrumentService(private val instrumentRepository: InstrumentRepository) {

    fun handleInstrumentEvent(instrumentEvent: InstrumentEvent) {
        val instrumentEntity = ModelToEntityMapper.toInstrumentEntity(instrumentEvent)
        when (instrumentEvent.type) {
            InstrumentEvent.Type.ADD -> instrumentRepository.save(instrumentEntity)
            InstrumentEvent.Type.DELETE -> instrumentRepository.delete(instrumentEntity)
        }
    }
}
