package org.traderepublic.candlesticks.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoInstrumentFoundException(override val message: String?): Exception(message)
