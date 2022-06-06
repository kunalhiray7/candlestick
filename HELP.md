# Candlesticks

A simple microservice that provides [candlestick](https://en.wikipedia.org/wiki/Candlestick_chart) for a given instrument.

### Building project and Running tests
* Run `./gradlew clean build` for building the project and running the unit test cases.

### How to run locally??
* Run `./gradlew bootrun`.
* The server should be running on `http://localhost:9000`.

### Fetch Candlesticks
* Call the endpoint - `http://localhost:9000/candlesticks?isin=<ISIN>`. Refer [README](./README.md) for additional information
and terminology.

### Health Check
* Visit `http://localhost:9000/actuator/health` for checking the health of the service.

### H2 Console
* Visit `http://localhost:8080/h2_console` if you want to see/analyze the data in the H2 database. The credentials are in
`application.yml`.

