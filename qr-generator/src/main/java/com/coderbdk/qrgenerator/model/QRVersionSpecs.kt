package com.coderbdk.qrgenerator.model

object QRVersionSpecs {
    val version1 = QRVersion(
        versionNumber = 1,
        gridSize = 21,
        eccSpecs = mapOf(
            ECCLevel.L to ECCLevelSpecs(ECCLevel.L, 19, 7, 41,25),
            ECCLevel.M to ECCLevelSpecs(ECCLevel.M, 16, 10, 34,20),
            ECCLevel.Q to ECCLevelSpecs(ECCLevel.Q, 13, 13, 27,16),
            ECCLevel.H to ECCLevelSpecs(ECCLevel.H, 9, 17, 17,10)
        )
    )
}