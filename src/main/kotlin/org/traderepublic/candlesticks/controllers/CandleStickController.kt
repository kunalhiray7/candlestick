package org.traderepublic.candlesticks.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.traderepublic.candlesticks.services.CandleStickService

@RestController
class CandleStickController(private val service: CandleStickService) {

    @GetMapping("/candlesticks")
    fun getCandleSticks(@RequestParam("isin") isin: String) = service.getCandleSticks(isin)
}
