package com.example.registro.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

            saveBitmapToFile(bitmap, file, "C_${file.name}")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Comprime y redimensiona una imagen desde una Uri (Galería).
     */
    fun compressImageFromUri(context: Context, uri: Uri, maxWidth: Int = 1600, maxHeight: Int = 1600): Uri? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false

            val inputStream2 = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, options) ?: return null
            inputStream2?.close()

            val tempFile = File(context.cacheDir, "GAL_${System.currentTimeMillis()}.jpg")
            saveBitmapToFile(bitmap, tempFile, tempFile.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, originalFile: File, newName: String): Uri? {
        return try {
            val compressedFile = File(originalFile.parent, newName)
            val out = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

            // Eliminar original si es del sistema (cámara)
            if (originalFile.exists() && originalFile.absolutePath != compressedFile.absolutePath && originalFile.name.startsWith("IMG_")) {
                originalFile.delete()
            }

            Uri.fromFile(compressedFile)
        } catch (e: Exception) {
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
