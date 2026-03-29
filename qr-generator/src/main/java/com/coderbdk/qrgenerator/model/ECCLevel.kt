package com.coderbdk.qrgenerator.model

/**
 * Represents the Error Correction Code (ECC) levels for a QR Code.
 *
 * The ECC level determines the amount of damage or dirt a QR code can withstand
 * while remaining scannable. Higher levels provide more recovery at the cost of
 * reduced data capacity.
 *
 * @see <a href="https://www.qrcode.com/en/about/error_correction.html">QR Code Error Correction</a>
 */
enum class ECCLevel {
    /**
     * Level L: Recovers up to 7% of data.
     * Offers the highest storage capacity.
     */
    L,

    /**
     * Level M: Recovers up to 15% of data.
     * The default choice for most QR codes.
     */
    M,

    /**
     * Level Q: Recovers up to 25% of data.
     * Used in environments with significant risk of damage.
     */
    Q,

    /**
     * Level H: Recovers up to 30% of data.
     * Provides maximum recovery but the lowest storage capacity.
     */
    H
}