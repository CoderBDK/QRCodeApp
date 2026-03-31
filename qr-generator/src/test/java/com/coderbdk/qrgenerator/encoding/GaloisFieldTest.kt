package com.coderbdk.qrgenerator.encoding

import com.coderbdk.qrgenerator.errorcorrection.GaloisField
import org.junit.Test
import org.junit.Assert.assertEquals

class GaloisFieldTest {

    @Test
    fun `print galois field log and exp tables`() {
        println("--- Galois Field (GF256) Exponent Table ---")
        println("Index | Value (alpha^index)")
        println("---------------------------")
        for (i in 0..255) {
            // We use % 255 because alpha^255 wrap around occurs in QR
            val value = GaloisField.exp(i)
            println("${i.toString().padEnd(5)} | $value")
        }

        println("\n--- Galois Field (GF256) Logarithm Table ---")
        println("Value | Log (index of alpha)")
        println("---------------------------")
        // Log of 0 is undefined, so we start from 1
        for (i in 1..255) {
            val logValue = GaloisField.log(i)
            println("${i.toString().padEnd(5)} | $logValue")
        }
    }

    @Test
    fun `test basic log and exp values`() {
        // Standard QR Code GF(256) values (Base 2)
        // alpha^0 = 1
        assertEquals(1, GaloisField.exp(0))
        assertEquals(0, GaloisField.log(1))

        // alpha^1 = 2
        assertEquals(2, GaloisField.exp(1))
        assertEquals(1, GaloisField.log(2))

        // alpha^2 = 4
        assertEquals(4, GaloisField.exp(2))
        assertEquals(2, GaloisField.log(4))

        // alpha^5 = 32
        assertEquals(32, GaloisField.exp(5))
        assertEquals(5, GaloisField.log(32))
    }

    @Test
    fun `test primitive polynomial overflow`() {
        // alpha^7 = 128
        // alpha^8 = 128 * 2 = 256 -> (256 XOR 285) = 29
        assertEquals(29, GaloisField.exp(8))
        assertEquals(8, GaloisField.log(29))

        // alpha^9 = 29 * 2 = 58
        assertEquals(58, GaloisField.exp(9))
        assertEquals(9, GaloisField.log(58))

        // alpha^254 (The last element before wrap)
        // From Thonky: alpha^254 should be 142
        assertEquals(142, GaloisField.exp(254))
        assertEquals(254, GaloisField.log(142))
    }

    @Test
    fun `test multiplication logic`() {
        // Test 1: 3 * 7
        // log(3)=25, log(7)=198 -> 25+198 = 223. exp(223) = 9
        assertEquals(9, GaloisField.multiply(3, 7))

        // Test 2: 15 * 10
        assertEquals(102, GaloisField.multiply(15, 10))

        // Test 3: Large values (Overflowing 255 sum of logs)
        // log(100)=195, log(150)=180 -> 195+180 = 375 mod 255=120. exp(120) = 59
        assertEquals(59, GaloisField.multiply(100, 150))
    }
}