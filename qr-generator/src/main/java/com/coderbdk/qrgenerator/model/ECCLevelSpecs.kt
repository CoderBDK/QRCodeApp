package com.coderbdk.qrgenerator.model

/**
 * Technical specifications for a specific Error Correction Code (ECC) level.
 *
 * This configuration defines the data capacity and recovery limits for a specific
 * QR Code version based on the official specifications from Denso Wave.
 *
 * @property level The [ECCLevel] representing the error recovery capability (L, M, Q, or H).
 * @property totalDataCodewords Total number of 8-bit codewords reserved for the actual message.
 * @property errorCorrectionCodewords Number of codewords used for Reed-Solomon error correction.
 * @property maxNumericCapacity Maximum number of digits (0-9) that can be stored in this configuration.
 * @property maxAlphanumericCapacity Maximum number of characters (0-9, A-Z, etc.) that can be stored.
 * @see <a href="https://www.qrcode.com/en/about/version.html">Official QR Code Capacity Table</a>
 * @see <a href="https://www.thonky.com/qr-code-tutorial/error-correction-table">Thonky: Error Correction Table</a>
 */
data class ECCLevelSpecs(
    val level: ECCLevel,
    val totalDataCodewords: Int,
    val errorCorrectionCodewords: Int,
    val maxNumericCapacity: Int,
    val maxAlphanumericCapacity: Int,
)