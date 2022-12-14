package org.traderepublic.candlesticks.entities

import org.traderepublic.candlesticks.models.ISIN
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
data class Instrument(

    @Id
    val isin: ISIN = "",

    @get: NotBlank
    val description: String = "",

    val creationTimestamp: Instant = Instant.now()
)
