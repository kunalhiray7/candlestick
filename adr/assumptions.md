# Assumptions

## Status

Accepted

## Service Assumptions
1. There might be the case where system missed processing an instrument event(for reasons like service unavailable etc.)
and a quote is received for that instrument. In this case we do not process such quote events as system will be in inconsistent
state.

2. If users try to retrieve the quotes for the non-existent instrument, service will respond with HTTP 404(Not Found).

3. If there are no quotes available for a given ISIN and the time window, service will return an empty list.

4. Minimum temporal unit for candlestick aggregation is 1 minute.      
