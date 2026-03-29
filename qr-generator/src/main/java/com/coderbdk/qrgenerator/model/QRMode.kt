package com.coderbdk.qrgenerator.model

/**
 * Each mode has a unique 4-bit Mode Indicator that informs the QR reader
 * how the data bits should be interpreted.
 *
 * @property modeIndicator The 4-bit binary identifier for the encoding mode (e.g., 0001 for Numeric).
 * @see <a href="https://www.thonky.com/qr-code-tutorial/data-encoding">QR Code Data Encoding Tutorial (Thonky)</a>
 */
enum class QRMode(val modeIndicator: Int) {
    /**
     * Numeric mode is used for decimal digits (0-9).
     * It is the most density-efficient mode, packing 3 digits into 10 bits.
     */
    NUMERIC(0b0001),
    ALPHANUMERIC(0b0010);

    /**
     * Returns the number of bits required for the Character Count Indicator.
     *
     * The bit length varies based on both the QR version and the encoding mode,
     * as defined in the specifications.
     *
     * @param mode The [QRMode] used for encoding (e.g., Numeric, Alphanumeric).
     * @param version The QR version number (1 to 40).
     * @return The required bit length for the character count.
     * @see <a href="https://www.thonky.com/qr-code-tutorial/data-encoding">Character Count Indicator Table (Thonky)</a>
     */
    fun getCharacterCountBits(mode: QRMode, version: Int): Int {
        return when (version) {
            in 1..9 -> when (mode) {
                NUMERIC -> 10
                ALPHANUMERIC -> 9
            }
            in 10..26 -> when (mode) {
                NUMERIC -> 12
                ALPHANUMERIC -> 11
            }
            in 27..40 -> when (mode) {
                NUMERIC -> 14
                ALPHANUMERIC -> 13
            }
            else -> throw IllegalArgumentException("Version must be between 1 and 40")
        }
    }
}