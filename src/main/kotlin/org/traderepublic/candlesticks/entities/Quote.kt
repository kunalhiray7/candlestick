package org.traderepublic.candlesticks.entities

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.traderepublic.candlesticks.models.Price
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class Quote(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    @get: NotNull
    val price: Price = 0.0,

    val creationTimestamp: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "isin", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    var instrument: Instrument = Instrument()
)
