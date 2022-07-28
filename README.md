#### What is a candlestick? 
A [candlestick](https://en.wikipedia.org/wiki/Candlestick_chart) is a representation that describes the price movement for a given instrument in a fixed amount of time, usually one minute.
We will be using a simplified version of candlesticks for this challenge.

![chart](https://t4.ftcdn.net/jpg/02/79/65/79/360_F_279657943_FALhJZ6g4shXyfqMIRifp1l6lhiwhbwm.jpg)

The basic idea is that we don't need to know about _all_ prices changes within a given timeframe.
Usually we want them grouped in 1 minute chunks, because we are more interested in some key data points within any given minute.
In theory, a candlestick “contains” all quotes, where the timestamp of the quote is higher than the openTimestamp and lower than the closeTimestamp (`openTimestamp <= quoteTimestamp < closeTimestamp`).
However, for each candle for each given minute, we only present the following data points to the user:
- the first quotes price, that was received (openPrice)
- the last quotes, that was received (closePrice) 
- the highest quote price that was observed (highPrice)
- the lowest quote price that was observed (lowPrice)
- the timestamp when the candlestick was opened (openTimestamp)
- the timestamp when the candlestick was closed (closeTimestamp)
