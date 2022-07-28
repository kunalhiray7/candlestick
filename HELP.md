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


### Example
Assume the following (simplified) data was received for an instrument:
```
@2019-03-05 13:00:05 price: 10
@2019-03-05 13:00:06 price: 11
@2019-03-05 13:00:13 price: 15
@2019-03-05 13:00:19 price: 11
@2019-03-05 13:00:32 price: 13
@2019-03-05 13:00:49 price: 12
@2019-03-05 13:00:57 price: 12
@2019-03-05 13:01:00 price: 9
```
The resulting minute candle would have these attributes:
```
openTimestamp: 2019-03-05 13:00:00
openPrice: 10
highPrice: 15
lowPrice: 10
closePrice: 12
closeTimestamp: 13:01:00
```
Note that the last received quote with a price of 9 is not part of this candlestick anymore, but belongs to the new candlestick.


### Running the Partner Service

To run a partner service you can either use docker-compose. Docker v3 or above will require slight changes to the docker-compose files.
``` 
docker-compose up -d
```
or Java
```
java -jar partner-service-1.0.1-all.jar --port=8032
```

### Running the app
To run the app you can use the following gradle commands  
```
./gradlew build
./gradlew test
./gradlew bootrun
```

Once the server is running you can check the results at
```
http://localhost:9000/candlesticks?isin={ISIN}
```
Note: If you don't implement your service, you will see an exception here
