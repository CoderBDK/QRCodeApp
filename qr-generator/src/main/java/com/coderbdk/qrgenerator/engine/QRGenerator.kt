package com.coderbdk.qrgenerator.engine

import com.coderbdk.qrgenerator.encoding.DataEncoder
import com.coderbdk.qrgenerator.encoding.ReedSolomonEncoder
import com.coderbdk.qrgenerator.masking.MaskApplier
import com.coderbdk.qrgenerator.matrix.DataPlacement
import com.coderbdk.qrgenerator.matrix.QRCodeMatrix
import com.coderbdk.qrgenerator.model.ECCLevel
import com.coderbdk.qrgenerator.model.ECCLevelSpecs
import com.coderbdk.qrgenerator.model.QRMode
import com.coderbdk.qrgenerator.model.QRVersion

class QRGenerator(val qrVersion: QRVersion) {

    fun generate(text: String, mode: QRMode, specs: ECCLevelSpecs): Array<IntArray> {
        val dataEncoder = DataEncoder()

        // 1. Data Encoding to BitString
        val finalBitString = dataEncoder.encode(text, qrVersion.versionNumber, mode, specs)

        // 2. Data to Codewords & ECC Generation
        val dataCodewords = dataEncoder.convertToCodewords(finalBitString)
        val reedSolomonEncoder = ReedSolomonEncoder()
        val eccCodewords = reedSolomonEncoder.encode(dataCodewords, specs.eccCodewordsPerBlock)

        // Combine Data + ECC and convert back to a long BitString
        val finalBitSequence = createFinalBitStream(dataCodewords, eccCodewords)

        // 3. Initialize Matrix & Place Fixed Patterns (Finder, Timing, etc.)
        val qrCodeMatrix = QRCodeMatrix(qrVersion)

        val baseGrid = qrCodeMatrix.grid

        // 4. Place Data Bits in Zig-Zag pattern
        val placement = DataPlacement()
        placement.placeData(baseGrid, finalBitSequence, qrVersion.versionNumber)

        // 5. Apply Mask Pattern. Currently hardcoded to Mask 0
        // TODO: Implement Penalty Calculation to automatically select the optimal mask (0-7).
        MaskApplier.applyMask(baseGrid, qrVersion.versionNumber, 0)

        // 6. Format Information (Mask ID + ECC Level)
        addFormatInformation(baseGrid, 0, specs.level)

        return baseGrid
    }

    /**
     * Adds the 15-bit format information (ECC Level + Mask ID) to the matrix.
     * Format info is placed around the Finder Patterns.
     */
    private fun addFormatInformation(matrix: Array<IntArray>, maskId: Int, eccLevel: ECCLevel) {
        val size = matrix.size

        // Step 1: Get 2-bit indicator for ECC Level
        val eccIndicator = eccLevel.indicator

        // Step 2: Combine ECC (2 bits) and Mask (3 bits) = 5 bits
        val data = (eccIndicator shl 3) or maskId

        // Step 3: Calculate BCH Error Correction (10 bits)
        var rem = data
        for (i in 0 until 10) {
            rem = (rem shl 1) xor (if ((rem shr 9) == 1) 0x537 else 0)
        }
        val formatBits = (data shl 10) or rem

        // Step 4: XOR with Mask String (101010000010010) to avoid all zeros
        val finalFormat = formatBits xor 0x5412

        // Step 5: Place bits in the matrix (Version 1 specific positions)
        for (i in 0 until 15) {
            val bit = (finalFormat shr i) and 1

            // Horizontal & Vertical placement around Finder Patterns
            // (This follows the standard QR coordinate mapping for Format Info)
            val coords = getFormatCoords(i, size)
            matrix[coords.first.first][coords.first.second] = bit
            matrix[coords.second.first][coords.second.second] = bit
        }
    }

    /**
     * Returns the two coordinate pairs where each format bit is mirrored.
     */
    private fun getFormatCoords(bitIndex: Int, size: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return when (bitIndex) {
            in 0..5 -> (8 to size - 1 - bitIndex) to (bitIndex to 8)
            6 -> (8 to size - 7) to (7 to 8)
            7 -> (8 to size - 8) to (8 to 8)
            8 -> (size - 7 to 8) to (8 to 7)
            in 9..14 -> (size - 1 - (14 - bitIndex) to 8) to (8 to 14 - bitIndex)
            else -> (0 to 0) to (0 to 0)
        }
    }
    private fun createFinalBitStream(data: IntArray, ecc: IntArray): String {
        return (data + ecc).joinToString("") { it.toString(2).padStart(8, '0') }
    }
}