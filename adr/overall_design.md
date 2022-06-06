# Overall Design

## Status

Accepted

## Context

After having a quick glance at the problem statement, we can divide it in the two sub-problems.
1. Process the incoming instrument and quote events from partner service
2. Aggregate quotes and prepare candlesticks for the requested ISIN

First sub-problem can be kept isolated from the second and needs to be processed perpetually. Whereas second sub-problem
is solved only on demand i.e. when the candlesticks are requested by the users.

We can handle the quotes and instrument events in the corresponding event handler services. Which will abstract out the
CRUD operations in the repository.

For preparing candlesticks for each request, we can extract the historic quotes from the database and process them in 
the memory. This aggregation operation can also be done in the database with queries. However, considering the possible
future complexity of the candlestick preparation logic, we have decided to do in the JVM. We can simulate these two
approaches on the production load and should revisit this decision.     

## Tech Decisions

__Database__ - For the purpose of this PoC, we chose in-memory H2 database and abstract out the repository operations using
spring JPA so that we can replace the DB easily in the future.

__Framework__ - Spring Boot and JPA: Easy to implement RESTful services. Most of the cross-cutting concerns are handled 
easily. JPA allows us to operate on the DB irrespective of underlying implementation. Future enhancements will be easier.

__Test Framework__ - Spring Mock MVC + Kotlin Mockito

## Consequences

In memory H2 database is in the scope only for this PoC, it will not be able to handle the load in production and will 
create a lot of files on the disk.

__Mitigations__: 
1. Database should be replaced by any of battle-tested, industry-standard databases like Postgresql.     

## Future Enhancements
1. Short term - Implement a scheduler that will delete the older quotes(older than 30 minutes) from the H2 database.
2. Long Term - Replace H2 with persistent, battle-tested relational database. This will serve the purpose of reading the
quotes to prepare the candlesticks for a given duration(e.g. 30 minutes). The historic quotes should be archived into some
kind of time-series database like Casandra.   
