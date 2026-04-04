package com.coderbdk.qrgenerator.encoding

import com.coderbdk.qrgenerator.errorcorrection.GaloisField
import com.coderbdk.qrgenerator.errorcorrection.GeneratorPolynomial


/**
 * Performs Reed-Solomon encoding to generate error correction codewords.
 * Uses polynomial long division over GF(256).
 */
class ReedSolomonEncoder {

    /**
     * Encodes data codewords into error correction codewords.
     * @param dataCodewords The original message bytes.
     * @param eccCount The number of error correction codewords required.
     * @return An [IntArray] containing only the error correction codewords.
     */
    fun encode(dataCodewords: IntArray, eccCount: Int): IntArray {
        val generator = GeneratorPolynomial().generate(eccCount)

        // Prepare the division: The message polynomial is shifted by eccCount positions
        // This is equivalent to multiplying the message by x^n
        val result = IntArray(dataCodewords.size + eccCount)
        System.arraycopy(dataCodewords, 0, result, 0, dataCodewords.size)

        // Polynomial Long Division
        for (i in dataCodewords.indices) {
            val leadCoeff = result[i]

            // If lead coefficient is 0, we skip this step (nothing to divide)
            if (leadCoeff != 0) {
                for (j in 1 until generator.size) {
                    // Calculate the term to XOR: leadCoeff * generator[j]
                    val term = GaloisField.multiply(generator[j], leadCoeff)

                    // Update the result array with XOR (GF Addition/Subtraction)
                    result[i + j] = result[i + j] xor term
                }
            }
        }

        // The remainder is the last 'eccCount' elements of our result array
        return result.copyOfRange(dataCodewords.size, result.size)
    }
}