package com.example.registro.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    /**
     * Comprime y redimensiona una imagen para ahorrar espacio.
     * @param file El archivo original capturado por la cámara.
     * @return Uri del nuevo archivo comprimido.
     */
    fun compressAndResizeImage(file: File, maxWidth: Int = 1600, maxHeight: Int = 1600): Uri? {
        return try {
            // 1. Cargar solo las dimensiones para calcular el factor de escala
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            // 2. Calcular el factor de reducción (inSampleSize)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false

            // 3. Decodificar el Bitmap con el tamaño reducido
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return null

            // 4. Crear el archivo de destino (con prefijo 'C_')
            val compressedFile = File(file.parent, "C_${file.name}")
            val out = FileOutputStream(compressedFile)
            
            // 5. Comprimir a JPEG con calidad 90% (Mejor calidad para impresión)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            
            // 6. Eliminar el archivo original pesado para liberar espacio
            if (file.exists() && file.absolutePath != compressedFile.absolutePath) {
                file.delete()
            }

            Uri.fromFile(compressedFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
