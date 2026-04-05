package com.coderbdk.qrgenerator.matrix

import com.coderbdk.qrgenerator.model.QRVersion

class QRCodeMatrix(val version: QRVersion) {
    private val size = version.gridSize
    val grid = Array(size) { IntArray(size) { -1 } }

    init {
        addFinderPatterns()
        addSeparatorPatterns()
        addTimingPatterns()
        addDarkModule()
        reserveFormatAreas()
    }

    /**
     * Adds the three mandatory 7x7 Finder Patterns to the corners.
     */
    private fun addFinderPatterns() {
        drawFinder(0, 0)                // Top-Left
        drawFinder(0, size - 7)         // Top-Right
        drawFinder(size - 7, 0)         // Bottom-Left
    }

    /**
     * Draws a single 7x7 Finder Pattern at the given row and column.
     * A Finder Pattern consists of a 7x7 black outer frame, a 5x5 white inner frame,
     * and a 3x3 black solid center.
     */
    private fun drawFinder(row: Int, col: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                // Logic: Black if on the outer edge, OR if in the 3x3 center
                if (r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4)) {
                    grid[row + r][col + c] = 1 // Black
                } else {
                    grid[row + r][col + c] = 0 // White
                }
            }
        }
    }
    /**
     * Adds 1-pixel wide white separators around all three Finder Patterns.
     */
    private fun addSeparatorPatterns() {
        // Top-Left Separator (Horizontal and Vertical)
        for (i in 0..8) {
            if (i < size) {
                grid[8][i] = 0 // Horizontal line at Row 8
                grid[i][8] = 0 // Vertical line at Col 8
            }
        }

        // Top-Right Separator
        for (i in 0..8) {
            // Horizontal line at Row 8, from right edge inwards
            if (size - 9 + i < size) grid[8][size - 9 + i] = 0
            // Vertical line at Col size-9
            if (i < 8) grid[i][size - 9] = 0
        }

        // Bottom-Left Separator
        for (i in 0..8) {
            // Vertical line at Col 8, from bottom edge upwards
            if (size - 9 + i < size) grid[size - 9 + i][8] = 0
            // Horizontal line at Row size-9
            if (i < 8) grid[size - 9][i] = 0
        }
    }

    private fun addTimingPatterns() {
        for (i in 8 until size - 8) {
            val color = if (i % 2 == 0) 1 else 0
            grid[6][i] = color // Horizontal Timing
            grid[i][6] = color // Vertical Timing
        }
    }

    private fun addDarkModule() {
        // Always at (4 * version + 9, 8)
        grid[13][8] = 1
    }

    private fun reserveFormatAreas() {
        // Mark Format Info areas as 0 temporarily so isDataArea recognizes them as reserved
        // We will place actual Format bits after masking
        for (i in 0..8) {
            if (grid[8][i] == -1) grid[8][i] = 0
            if (grid[i][8] == -1) grid[i][8] = 0
        }
        for (i in size - 8 until size) {
            if (grid[8][i] == -1) grid[8][i] = 0
            if (grid[i][8] == -1) grid[i][8] = 0
        }
    }
}