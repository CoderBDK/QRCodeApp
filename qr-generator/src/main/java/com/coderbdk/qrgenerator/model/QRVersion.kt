package com.coderbdk.qrgenerator.model

/**
 * Represents a specific QR Code version and its physical constraints.
 * @property versionNumber The version of the QR Code (ranging from 1 to 40).
 * @property gridSize The total number of modules (dots) per side (e.g., 21 for Version 1).
 * @property eccSpecs A mapping of [ECCLevel] to its corresponding [ECCLevelSpecs],
 * defining data and error correction capacities for this version.
 */
data class QRVersion(
    val versionNumber: Int,
    val gridSize: Int,
    val eccSpecs: Map<ECCLevel, ECCLevelSpecs>
)