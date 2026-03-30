package com.coderbdk.qrgenerator.encoding

import com.coderbdk.qrgenerator.model.ECCLevelSpecs
import com.coderbdk.qrgenerator.model.QRMode

class DataEncoder {

    fun encode(data: String, version: Int, mode: QRMode, specs: ECCLevelSpecs): String {
        val bitStream = StringBuilder()

        // 1. Mode Indicator
        bitStream.append(mode.modeIndicator.toBinaryString(4))

        // 2. Character Count Indicator
        val charCountBits = mode.getCharacterCountBits(mode, version)
        bitStream.append(data.length.toBinaryString(charCountBits))

        // 3. Encode Data
        val encodedData = when (mode) {
            QRMode.NUMERIC -> encodeNumeric(data)
            QRMode.ALPHANUMERIC -> encodeAlphanumeric(data)
        }
        bitStream.append(encodedData)

        val totalDataBits = specs.totalDataCodewords * 8
        return finalizeBitStream(bitStream.toString(), totalDataBits)
    }

    private fun isValidNumeric(input: String): Boolean = input.all { it in '0'..'9' }

    /**
     * Encodes the input numeric string into a bit stream using the Numeric Mode rules.
     *
     * According to the QR specification, Numeric Mode is the most efficient encoding
     * method, packing three decimal digits into 10 bits. If the total number of digits
     * is not a multiple of 3, the remaining digits are encoded as follows:
     * - 3 digits -> 10 bits (Max value 999 fits in 2^10 = 1024)
     * - 2 digits -> 7 bits  (Max value 99 fits in 2^7 = 128)
     * - 1 digit  -> 4 bits  (Max value 9 fits in 2^4 = 16)
     *
     * @param data A string containing only decimal digits (0-9).
     * @return A [String] of bits (0s and 1s) representing the encoded numeric data.
     * @see <a href="https://www.thonky.com/qr-code-tutorial/numeric-mode-encoding">Thonky: Numeric Mode Encoding Step-by-Step</a>
     */
    private fun encodeNumeric(data: String): String {
        val result = StringBuilder()
        var i = 0
        while (i < data.length) {
            val remaining = data.length - i

            when {
                // Group of 3 digits: Encode into 10 bits
                remaining >= 3 -> {
                    val chunk = data.substring(i, i + 3).toInt()
                    result.append(chunk.toBinaryString(10))
                    i += 3
                }
                // Remaining 2 digits: Encode into 7 bits
                remaining == 2 -> {
                    val chunk = data.substring(i, i + 2).toInt()
                    result.append(chunk.toBinaryString(7))
                    i += 2
                }
                // Remaining 1 digit: Encode into 4 bits
                else -> {
                    val chunk = data.substring(i, i + 1).toInt()
                    result.append(chunk.toBinaryString(4))
                    i += 1
                }
            }
        }
        return result.toString()
    }

    /**
     * Encodes the input string into a bit stream using the Alphanumeric Mode rules.
     *
     * According to the QR specification, Alphanumeric Mode encodes characters in pairs
     * to optimize space. Each pair of characters is converted into an 11-bit binary
     * value using a base-45 calculation. If an odd number of characters remains,
     * the final character is encoded into 6 bits.
     *
     * The formula for a pair (C1, C2) is: (Value of C1 * 45) + (Value of C2).
     *
     * Supported characters include:
     * - Digits: 0-9
     * - Uppercase letters: A-Z
     * - Symbols: Space, $, %, *, +, -, ., /, :
     *
     * @param data The string to encode. Non-uppercase letters are automatically converted.
     * @return A [String] of bits (0s and 1s) representing the encoded alphanumeric data.
     * @throws IllegalArgumentException if a character is not supported in Alphanumeric Mode.
     * @see <a href="http://thonky.com/qr-code-tutorial/alphanumeric-mode-encoding">Thonky: Alphanumeric Mode Encoding Step-by-Step</a>
     */
    private fun encodeAlphanumeric(data: String): String {
        val result = StringBuilder()
        val upperData = data.uppercase()

        var i = 0
        while (i < upperData.length) {
            val remaining = upperData.length - i

            when {
                // Pair of characters: Encode into 11 bits
                remaining >= 2 -> {
                    val char1 = getAlphanumericValue(upperData[i])
                    val char2 = getAlphanumericValue(upperData[i + 1])
                    val value = (char1 * 45) + char2
                    result.append(value.toBinaryString(11))
                    i += 2
                }
                // Single remaining character: Encode into 6 bits
                else -> {
                    val char1 = getAlphanumericValue(upperData[i])
                    result.append(char1.toBinaryString(6))
                    i += 1
                }
            }
        }
        return result.toString()
    }

    /**
     * Maps a single character to its corresponding Alphanumeric value (0-44).
     *
     * This mapping is essential for the Base-45 calculation used in Alphanumeric Mode.
     * Each supported character is assigned a specific integer value as defined in the
     * QR Code standard.
     *
     * The character set includes:
     * - 0 to 9 -> Values 0 to 9
     * - A to Z -> Values 10 to 35
     * - Space -> Value 36
     * - $ -> Value 37
     * - % -> Value 38
     * - * -> Value 39
     * - + -> Value 40
     * - - -> Value 41
     * - . -> Value 42
     * - / -> Value 43
     * - : -> Value 44
     *
     * @param c The character to map. Must be one of the 45 supported characters.
     * @return The integer value (0-44) of the character.
     * @throws IllegalArgumentException if the character is not supported in the Alphanumeric set.
     * @see <a href="http://thonky.com/qr-code-tutorial/alphanumeric-table">Thonky: Alphanumeric Character Table</a>
     */
    private fun getAlphanumericValue(c: Char): Int {
        val alphaMap = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:"
        val index = alphaMap.indexOf(c)
        if (index == -1) {
            throw IllegalArgumentException("Invalid Alphanumeric character: '$c'. Only 0-9, A-Z, and space $%*+-./: are allowed.")
        }
        return index
    }

    /**
     * Finalizes the bit stream by adding Terminator, Bit Alignment, and Padding Bytes.
     *
     * @param bitStream The current bit string (Mode + Count + Data).
     * @param totalDataBits Total capacity for the version/ECC level (Total Codewords * 8).
     */
    private fun finalizeBitStream(bitStream: String, totalDataBits: Int): String {
        var result = bitStream

        // 1. Terminator
        val terminatorLength = minOf(4, totalDataBits - result.length)
        if (terminatorLength > 0) {
            result += "0".repeat(terminatorLength)
        }

        // 2. (Byte Alignment)
        if (result.length % 8 != 0) {
            val paddingToByte = 8 - (result.length % 8)
            result += "0".repeat(paddingToByte)
        }

        // 3. Padding Bytes (236 and 17)
        val paddingPatterns = listOf("11101100", "00010001")
        var patternIndex = 0

        while (result.length < totalDataBits) {
            result += paddingPatterns[patternIndex]
            patternIndex = (patternIndex + 1) % 2
        }

        return result
    }

    /**
     * Converts an integer to a binary string representation with a fixed bit length.
     *
     * In QR code encoding, specific fields (like Mode Indicator, Character Count, or
     * encoded data chunks) must be represented by a precise number of bits. This
     * function ensures that the resulting binary string is padded with leading zeros
     * if its natural binary length is shorter than the required [bitCount].
     *
     * Example:
     * 5.toBinaryString(4)  -> "0101"
     * 45.toBinaryString(11) -> "00000101101"
     *
     * @param bitCount The required number of bits for the output string.
     * @return A [String] of 0s and 1s representing the binary value, padded to [bitCount].
     * @throws IllegalArgumentException if the integer value is too large to fit in [bitCount] bits.
     */
    private fun Int.toBinaryString(bitCount: Int): String {
        val binary = Integer.toBinaryString(this)

        if (binary.length > bitCount) {
            throw IllegalArgumentException("Value $this is too large for $bitCount bits.")
        }

        return binary.padStart(bitCount, '0')
    }
}