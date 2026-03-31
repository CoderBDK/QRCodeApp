package com.coderbdk.qrgenerator.errorcorrection

/**
 * Galois Field (GF(256)) helper for Reed-Solomon Error Correction.
 *
 * This class pre-calculates Log and Antilog (Exponent) tables to speed up
 * multiplication and division in GF(256). It uses the primitive polynomial
 * 285 (x^8 + x^4 + x^3 + x^2 + 1).
 *
 * @see <a href="https://www.thonky.com/qr-code-tutorial/log-antilog-table">Thonky: Log and Antilog Table</a>
 */
object GaloisField {
    private val logTable = IntArray(256)
    private val expTable = IntArray(256)

    init {
        var x = 1
        for (i in 0 until 255) {
            logTable[x] = i
            expTable[i] = x

            // Multiply by 2 (Left shift)
            x = x shl 1

            // If the value overflows 8 bits (>= 256), XOR with primitive polynomial 285
            if (x >= 256) {
                x = x xor 285
            }
        }
        // Exponent 255 is the same as 0 (wraps around for log calculations)
        expTable[255] = expTable[0]
    }

    /**
     * Multiplies two numbers in GF(256) using pre-calculated tables.
     * Rule: exp((log(a) + log(b)) % 255)
     */
    fun multiply(a: Int, b: Int): Int {
        if (a == 0 || b == 0) return 0
        val sumOfLogs = logTable[a] + logTable[b]
        return expTable[sumOfLogs % 255]
    }

    /**
     * Returns the exponent (antilog) of n.
     */
    fun exp(n: Int): Int = expTable[n]

    /**
     * Returns the logarithm of n.
     */
    fun log(n: Int): Int {
        if (n == 0) throw IllegalArgumentException("Log of 0 is undefined in GF(256)")
        return logTable[n]
    }
}