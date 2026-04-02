package com.coderbdk.qrgenerator.errorcorrection

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class GeneratorPolynomialTest {

    private val generator = GeneratorPolynomial()

    @Test
    fun `test generator polynomial for 7 ECC codewords`() {
        // Based on Thonky's Generator Polynomial Tool for n=7
        val eccCount = 7
        val actualCoefficients = generator.generate(eccCount)

        // Expected coefficients for n=7 (from Thonky):
        // g(x) = ɑ^0 x^7 + ɑ^87 x^6 + ɑ^229 x^5 + ɑ^146 x^4 + ɑ^149 x^3 + ɑ^238 x^2 + ɑ^102 x^1 + ɑ^21 x^0
        val expectedCoefficients = intArrayOf(
            1, 127, 122, 154, 164, 11, 68, 117
        )

        assertArrayEquals(
            "Generator polynomial coefficients for n=7 should match standard values",
            expectedCoefficients,
            actualCoefficients
        )
    }


    @Test
    fun `test generator polynomial for 2 ECC codewords`() {
        // g(x)= ɑ^0 x^2 + ɑ^25 x^1 + ɑ^1 x^0
        val eccCount = 2
        val actual = generator.generate(eccCount)
        val expected = intArrayOf(1, 3, 2)

        assertArrayEquals(expected, actual)
    }
}