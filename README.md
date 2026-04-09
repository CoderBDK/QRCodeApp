# QRCodeApp

A modern Android application built with **Jetpack Compose** and **Kotlin**. This project implements the **QR Code Version 1** generation algorithm from scratch, focusing on the fundamental principles of encoding and error correction.

---

## 🖼️ Visual Implementation (Current Output)

The following preview demonstrates the core engine generating QR Version 1 codes with four different **Error Correction Level (L, M, Q, and H)** with dynamic content.

<p align="center">
  <img src="https://github.com/user-attachments/assets/4f4f08d6-1f4c-4716-87ac-a0abcb5e3a7a" alt="Jetpack Compose UI showing Version 1 QR Codes" width="400"/>
  <br>
  <em>Figure 1: Comparison of ECC Levels (L, M, Q, H) using custom rendering logic.</em>
</p>

---
## 🚀 Progress Tracking & Limitations

The generation process follows these precise technical steps.

### **Current Limitations:**
- [x] **Supported Version**: Focused on **QR Version 1** (21x21 modules) for core logic validation.
- [x] **Reserved Patterns**: Implemented Finder, Timing, and Dark Module patterns.
- [ ] **Alignment Patterns:** Since Version 1 does not use alignment patterns, the logic for positioning them has **not been implemented yet**. It will be required for Version 2 and above.
- [ ] **Auto-Data Analysis**: Automatic QR mode detection (Numeric/Alphanumeric) is planned for future updates.

### **Generation Pipeline:**
- [x] **1. Data Analysis:** Evaluates input text to determine the encoding mode (currently handled via manual configuration).
- [x] **2. Data Encoding:** Converts input text into a binary stream based on the selected mode (Numeric, Alphanumeric).
- [x] **3. Error Correction:** Uses the `ReedSolomonEncoder` to generate parity bits for fault tolerance.
- [x] **4. Matrix Construction:** Positions the reserved patterns (Finder, Separator, Timing patterns).
- [x] **5. Data Placement:** The `DataPlacement` class injects the bitstream into the matrix using a zig-zag traversal.
- [ ] **6. Mask Selection:** Currently, only **Mask Pattern 0** is applied. Iterative evaluation using 4 penalty rules (N1-N4) to select the optimal mask is not yet implemented.
- [x] **7. Format Information:** Finalizes the matrix by adding the Mask ID and Error Correction Level bits.
---

## 📁 Project Structure
```plaintext
📦 qr-generator (Module)
└── com.coderbdk.qrgenerator
    ├── encoding                 # Bitstream generation and data padding logic
    │   ├── DataEncoder          # Converts text to binary data
    │   └── ReedSolomonEncoder   # Generate error correction codewords.
    ├── engine                   # The core orchestrator
    │   └── QRGenerator          # Coordinates the entire generation flow
    ├── errorcorrection          # Mathematical foundation for ECC
    │   ├── GaloisField          # Galois Field (GF(256)) helper for Reed-Solomon Error Correction.
    │   └── GeneratorPolynomial  # Generates the Generator Polynomial required for Reed-Solomon error correction.
    ├── masking                  # Pattern optimization
    │   └── MaskApplier          # Object responsible for applying QR code mask patterns to the data modules.
    ├── matrix                   # Physical layout logic
    │   ├── DataPlacement        # Zig-zag bit injection logic
    │   └── QRCodeMatrix         # Matrix state and reserved module handling
    ├── model                    # Data definitions and constants
    │   ├── ECCLevel             # Represents the Error Correction Code (ECC) levels for a QR Code.
    │   ├── ECCLevelSpecs        # Technical specifications for a specific Error Correction Code (ECC) level.
    │   ├── QRMode               # The QRMode used for encoding (e.g., Numeric, Alphanumeric).
    │   ├── QRVersion            # Represents a specific QR Code version and its physical constraints.
    │   └── QRVersionSpecs       # Specs for Version
    ├── render                   # Visual output logic
    │   └── QRImageGenerator     # Converts bit-matrix to Android Bitmap
    └── utils                    # Helper functions
        └── QRUtils              # Common utilities
```
## 💻 Usage
---
```kotlin
@Composable
fun QRCodeApp(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val qrVersion = QRVersionSpecs.version1
        val eccLevel = ECCLevel.L
        val generator = QRGenerator(qrVersion)
        val qrGrid =
            generator.generate("QRCodeApp", QRMode.ALPHANUMERIC, qrVersion.eccSpecs[eccLevel]!!)
        val bitmap = remember { QRImageGenerator().generateBitmap(qrGrid) }

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Generated QR Code",
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            filterQuality = FilterQuality.None
        )
    }
}
```
---
## 🛠️ Installation

### Clone the repository
```bash
git clone https://github.com/CoderBDK/QRCodeApp.git
```

---
## 🙏 Acknowledgments

This project wouldn't be possible without the incredible resources and documentation provided by:
* **[Thonky's QR Code Tutorial](https://www.thonky.com/qr-code-tutorial/):** The primary guide used for understanding the step-by-step logic of QR encoding, error correction, and masking.
* **[Introduction To QR Code Technology](https://www.researchgate.net/publication/318125149_An_Introduction_to_QR_Code_Technology):** For academic insights into QR structure.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
