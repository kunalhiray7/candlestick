package org.traderepublic.candlesticks.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.traderepublic.candlesticks.models.Candlestick
import org.traderepublic.candlesticks.services.CandleStickService
import org.traderepublic.candlesticks.utils.ObjectMapperUtil.Companion.getObjectMapper
import java.time.Instant

@SpringBootTest
class CandleStickControllerTest {
    private val mapper = getObjectMapper()
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var candleStickService: CandleStickService

    @InjectMocks
    private lateinit var candleStickController: CandleStickController

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(candleStickController).build()
    }

    @Test
    fun `GET should return the candlesticks`() {
        val isin = "12345"
        val candlesticks = listOf(
            Candlestick(
                openTimestamp = Instant.now(),
                closeTimestamp = Instant.now().plusSeconds(60L),
                openPrice = 123.0,
                highPrice = 130.0,
                lowPrice = 120.0,
                closingPrice = 127.0
            )
        )
        doReturn(candlesticks).`when`(candleStickService).getCandleSticks(isin)

        mockMvc.get("/candlesticks?isin=$isin").andExpect {
            status { isOk() }
            content {
                string(mapper.writeValueAsString(candlesticks))
            }
        }
        verify(candleStickService, times(1)).getCandleSticks(isin)
    }

    @Test
    fun `GET should return Bad Request when not isin is not passed as query parameter`() {
        val isin = "12345"
        mockMvc.get("/candlesticks").andExpect {
            status { isBadRequest() }
        }
        verify(candleStickService, times(0)).getCandleSticks(isin)
    }
}
