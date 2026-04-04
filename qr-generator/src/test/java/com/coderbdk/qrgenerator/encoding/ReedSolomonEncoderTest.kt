package com.coderbdk.qrgenerator.encoding

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ReedSolomonEncoderTest {

    @Test
    fun testEncodeBasic() {
        val encoder = ReedSolomonEncoder()

        // 1. Define sample data codewords
        val dataCodewords = intArrayOf(32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236)
        val eccCount = 13

        // 2. Perform the Reed-Solomon encoding
        val eccResult = encoder.encode(dataCodewords, eccCount)

        // 3. Verify the output size matches the requested ECC count
        assertEquals("ECC count should match the requested size", eccCount, eccResult.size)

        val expectedEcc = intArrayOf(168, 72, 22, 82, 217, 54, 156, 0, 46, 15, 180, 122, 16)

        assertArrayEquals(
            "The generated ECC codewords do not match the expected standard output",
            expectedEcc, eccResult
        )
    }

    @Test
    fun testWithZeroData() {
        val encoder = ReedSolomonEncoder()
        val data = intArrayOf(0)
        val eccCount = 5

        val result = encoder.encode(data, eccCount)

        assertArrayEquals(intArrayOf(0, 0, 0, 0, 0), result)
    }
}