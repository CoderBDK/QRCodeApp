package com.coderbdk.qrgenerator.utils

object QRUtils {

    /**
     * Checks if a specific coordinate (row, col) is part of the Data Area.
     * Any coordinate returning 'false' must NOT be touched by DataPlacement or MaskApplier.
     */
    fun isDataArea(r: Int, c: Int, size: Int, version: Int): Boolean {
        // 1. Finder Patterns + Separators (0-8 indices in corners)
        // Top-left (9x9)
        if (r < 9 && c < 9) return false
        // Top-right (9x9)
        if (r < 9 && c >= size - 8) return false
        // Bottom-left (9x9)
        if (r >= size - 8 && c < 9) return false

        // 2. Timing Patterns (Row 6 and Column 6)
        // Note: Timing patterns run between the separators of the finders.
        if (r == 6 || c == 6) return false

        // 3. Dark Module (Fixed black pixel)
        // Standard formula for Version 1 is [4*version + 9, 8] -> [13, 8]
        if (r == (4 * version + 9) && c == 8) return false

        // 4. Format Information Areas
        // Horizontal strip below Top-Right Finder (Row 8, Col size-8 to size-1)
        if (r == 8 && c >= size - 8) return false

        // Vertical strip right of Bottom-Left Finder (Col 8, Row size-8 to size-1)
        if (c == 8 && r >= size - 8) return false

        // 5. Alignment Patterns (Version 2+)
        // Note: For Version 1, no alignment patterns exist.
        // For higher versions, add alignment coordinate checks here.

        return true
    }
}