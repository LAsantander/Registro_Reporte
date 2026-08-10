package com.example.registro.ui.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

object QrUtils {
    /**
     * Genera un Bitmap que contiene un código QR con el texto proporcionado.
     * Incluye configuración de codificación UTF-8 para evitar errores con caracteres especiales.
     */
    fun generateQrCode(text: String, size: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
