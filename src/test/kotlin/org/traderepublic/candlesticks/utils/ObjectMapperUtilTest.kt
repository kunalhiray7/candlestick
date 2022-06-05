package org.traderepublic.candlesticks.utils

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.traderepublic.candlesticks.utils.ObjectMapperUtil.Companion.getObjectMapper
import java.time.Instant

class ObjectMapperUtilTest {

    @Test
    fun `objectMapper should have Java Time module`() {
        val objectMapper = getObjectMapper()

        val registeredModuleIds = objectMapper.registeredModuleIds

        assertTrue(objectMapper.canSerialize(Instant::class.java))
        assertTrue(objectMapper.canDeserialize(objectMapper.constructType(Instant::class.java)))
        assertTrue(registeredModuleIds.contains("com.fasterxml.jackson.module.kotlin.KotlinModule"))
        assertTrue(registeredModuleIds.contains("jackson-datatype-jsr310"))
    }
}
