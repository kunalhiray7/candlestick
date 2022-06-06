package org.traderepublic.candlesticks.constants

class AppConstants {
    companion object {
        const val SECONDS_TO_NEXT_CANDLESTICK = 60L
        const val SECONDS_TO_NEXT_TO_NEXT_CANDLESTICK = SECONDS_TO_NEXT_CANDLESTICK + 60L
        const val MIN_SECONDS_BEFORE_NEXT_CANDLESTICK = SECONDS_TO_NEXT_CANDLESTICK - 1L
    }
}
