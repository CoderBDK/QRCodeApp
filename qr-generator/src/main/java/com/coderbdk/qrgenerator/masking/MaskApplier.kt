package com.coderbdk.qrgenerator.masking

import com.coderbdk.qrgenerator.utils.QRUtils

/**
 * Object responsible for applying QR code mask patterns to the data modules.
 * Masking is performed to avoid patterns in the matrix that are difficult for scanners to read.
 * Logic based on: https://www.thonky.com/qr-code-tutorial/mask-patterns
 */
object MaskApplier {

    /**
     * Applies the specified mask pattern to the QR matrix.
     * Only modules in the 'Data Area' are affected; function patterns (Finder,
     * Timing, etc.) remain unchanged.
     *
     * @param matrix The QR code bit matrix (0 for white, 1 for black).
     * @param version The QR version used to determine reserved areas.
     * @param maskPattern The mask pattern index (0 to 7).
     */
    fun applyMask(matrix: Array<IntArray>, version: Int, maskPattern: Int) {
        val size = matrix.size
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (QRUtils.isDataArea(row, col, size, version)) {
                    if (shouldInvert(row, col, maskPattern)) {
                        matrix[row][col] = matrix[row][col] xor 1
                    }
                }
            }
        }
    }

    /**
     * Determines whether a specific module should be inverted based on the mask formula.
     * @param i Row index
     * @param j Column index
     * @param pattern Mask pattern index (0-7)
     * @return True if the bit should be flipped, false otherwise.
     */
    private fun shouldInvert(i: Int, j: Int, pattern: Int): Boolean {
        return when (pattern) {
            0 -> (i + j) % 2 == 0
            1 -> i % 2 == 0
            2 -> j % 3 == 0
            3 -> (i + j) % 3 == 0
            4 -> ((i / 2) + (j / 3)) % 2 == 0
            5 -> ((i * j) % 2) + ((i * j) % 3) == 0
            6 -> (((i * j) % 2) + ((i * j) % 3)) % 2 == 0
            7 -> (((i + j) % 2) + ((i * j) % 3)) % 2 == 0
            else -> false
        }
    }
}