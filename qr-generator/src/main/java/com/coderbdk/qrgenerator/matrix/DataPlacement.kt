package com.coderbdk.qrgenerator.matrix

import com.coderbdk.qrgenerator.utils.QRUtils

class DataPlacement {

    fun placeData(matrix: Array<IntArray>, bitStream: String, version: Int) {
        val size = matrix.size
        var bitIndex = 0
        var upward = true // Direction flag: true for upward, false for downward

        // Iterate from right to left, processing 2 columns at a time
        var c = size - 1
        while (c > 0) {
            // Skip the Vertical Timing Pattern (Column 6)
            if (c == 6) c--

            val rowRange = if (upward) (size - 1 downTo 0) else (0 until size)

            for (r in rowRange) {
                // Loop through the 2-column wide strip (right column first, then left)
                for (colOffset in 0..1) {
                    val currentCol = c - colOffset

                    // Ensure the module is part of the Data Area (not reserved for patterns)
                    if (QRUtils.isDataArea(r, currentCol, size, version)) {
                        if (bitIndex < bitStream.length) {
                            // Place the bit: 1 represents Black (1), 0 represents White (0)
                            matrix[r][currentCol] = if (bitStream[bitIndex] == '1') 1 else 0
                            bitIndex++
                        } else {
                            // If bitstream ends, fill the remaining Data Area with 0 (Padding)
                            matrix[r][currentCol] = 0
                        }
                    }
                }
            }
            // Reverse direction and move to the next 2-column strip
            upward = !upward
            c -= 2
        }
    }
}