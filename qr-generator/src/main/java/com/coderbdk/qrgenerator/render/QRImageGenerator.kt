package com.coderbdk.qrgenerator.render

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

/**
 * Responsible for rendering the logical QR matrix into a visual Android Bitmap.
 */
class QRImageGenerator {

    /**
     * Converts a 2D integer array (QR Matrix) into a displayable Android Bitmap.
     * @param qrGrid The 2D array where 1 represents a dark module and 0/other represents a light module.
     * @param moduleSize The pixel width/height for each individual QR module (default is 15px).
     * @return A [Bitmap] containing the rendered QR code with a mandatory quiet zone.
     */
    fun generateBitmap(qrGrid: Array<IntArray>, moduleSize: Int = 15): Bitmap {
        val gridSize = qrGrid.size

        // A "Quiet Zone" of at least 4 modules is required by ISO 18004 standards
        // to help scanners identify the boundary of the QR code.
        val quietZoneOffset = 4 * moduleSize
        val imageSize = (gridSize * moduleSize) + (2 * quietZoneOffset)

        // Create a blank bitmap with the calculated dimensions
        val bitmap = createBitmap(imageSize, imageSize)

        // Initialize the entire bitmap with a white background
        bitmap.eraseColor(Color.WHITE)

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                // Determine module color (1 = Black, 0 = White)
                val color = if (qrGrid[i][j] == 1) Color.BLACK else Color.WHITE

                // Render each logical module as a 'moduleSize x moduleSize' block of pixels
                for (rowOffset in 0 until moduleSize) {
                    for (colOffset in 0 until moduleSize) {
                        // Calculate final pixel coordinates including the quiet zone offset
                        val x = (j * moduleSize) + colOffset + quietZoneOffset
                        val y = (i * moduleSize) + rowOffset + quietZoneOffset

                        bitmap[x, y] = color
                    }
                }
            }
        }
        return bitmap
    }
}