package com.coderbdk.qrgenerator.encoding

import com.coderbdk.qrgenerator.model.ECCLevel
import com.coderbdk.qrgenerator.model.ECCLevelSpecs
import com.coderbdk.qrgenerator.model.QRMode
import com.coderbdk.qrgenerator.model.QRVersionSpecs
import org.junit.Assert.assertEquals
import org.junit.Test

class DataEncoderTest {

    private val encoder = DataEncoder()
    private val eccLevelSpecs = QRVersionSpecs.version1.eccSpecs[ECCLevel.Q]!!

    @Test
    fun `test complete numeric encoding flow`() {
        // Thonky Example: Numeric Mode, Version 1, Data: 8675309
        val data = "8675309"

        // 1. Mode Indicator (Numeric): 0001
        // 2. Char Count (Version 1-9 Numeric is 10 bits): 7 -> 0000000111
        // 3. Data: 867(1101100011), 530(1000010010), 9(1001)
        val expectedStart = "0001" + "0000000111" + "110110001110000100101001"

        val actual = encoder.encode(data, version = 1, mode = QRMode.NUMERIC, specs = eccLevelSpecs)

        assertEquals(true, actual.startsWith(expectedStart))
    }

    @Test
    fun `test alphanumeric encoding HELLO WORLD`() {
        val data = "HELLO WORLD"
        // Mode Indicator (Alphanumeric): 0010
        // Char Count (Version 1-9 Alphanumeric is 9 bits): 11 -> 000001011
        val expectedStart = "0010" + "000001011"

        val actual = encoder.encode(data, version = 1, mode = QRMode.ALPHANUMERIC, specs = eccLevelSpecs)

        assertEquals(true, actual.startsWith(expectedStart))
    }
}