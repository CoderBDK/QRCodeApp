package com.coderbdk.qrgenerator.errorcorrection

/**
 * Generates the Generator Polynomial required for Reed-Solomon error correction.
 *
 * In QR code generation, the generator polynomial is used to divide the message
 * polynomial. The remainder of this division becomes the Error Correction Codewords.
 *
 * The polynomial is constructed by multiplying factors:
 * G(x) = (x - α^0)(x - α^1)(x - α^2) ... (x - α^(n-1))
 * where 'n' is the number of error correction codewords and α is the primitive element.
 *
 * @see <a href="https://www.thonky.com/qr-code-tutorial/generator-polynomial-tool">Thonky: Generator Polynomial Construction</a>
 */
class GeneratorPolynomial {

    /**
     * Generates a polynomial of a specific degree based on the required ECC count.
     *
     * @param eccCount The number of error correction codewords (n) required by the ECC level.
     * @return An [IntArray] representing the coefficients of the generator polynomial.
     */
    fun generate(eccCount: Int): IntArray {
        // Start with a polynomial of degree 0: P(x) = 1
        var coefficients = intArrayOf(1)

        for (i in 0 until eccCount) {
            // Successively multiply the current polynomial by (x - α^i)
            // In GF(256), (x - α^i) is represented as [1, GaloisField.exp(i)]
            coefficients = multiply(coefficients, intArrayOf(1, GaloisField.exp(i)))
        }

        return coefficients
    }

    /**
     * Multiplies two polynomials within the Galois Field (GF256).
     *
     * The multiplication follows standard polynomial distribution rules, but
     * uses GF(256) arithmetic for coefficient multiplication (Log/Antilog)
     * and addition (XOR).
     *
     * @param poly1 The first polynomial coefficients.
     * @param poly2 The second polynomial coefficients.
     * @return The resulting polynomial coefficients after multiplication.
     */
    private fun multiply(poly1: IntArray, poly2: IntArray): IntArray {
        // The degree of the resulting polynomial is (degree1 + degree2)
        val result = IntArray(poly1.size + poly2.size - 1)

        for (i in poly1.indices) {
            for (j in poly2.indices) {
                // Multiply coefficients using Galois Field multiplication
                val product = GaloisField.multiply(poly1[i], poly2[j])

                // Add the product to the result using XOR (GF addition)
                // result[i + j] represents the coefficient of x^(total_degree - (i + j))
                result[i + j] = result[i + j] xor product
            }
        }
        return result
    }
}