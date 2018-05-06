package com.svetylkovo.neuralsound.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.Math.round

class NumberExtensionsTest {

    @Test
    fun normalizationTest() {
        val input = (-10..10).map { it / 10.0 }

        val result = input.map { it.toNormalized().fromNormalized() }

        assertThat(input.map { round(it * 10) }).isEqualTo(result.map { round(it * 10) })
    }

}