package com.example.registro.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.registro.model.BackupData
import com.google.gson.Gson
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object BackupUtils {

    /**
     * Exporta los datos a un archivo JSON y abre el menú de compartir.
     */
    fun exportBackup(context: Context, data: BackupData) {
        try {
            val gson = Gson()
            val jsonString = gson.toJson(data)
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Respaldo_Registro_$timestamp.json"
            val file = File(context.cacheDir, fileName)
            
            file.writeText(jsonString)
            
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "com.example.registro.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_SUBJECT, "Respaldo de Datos Registro")
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(intent, "Enviar respaldo por:"))
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Lee un archivo JSON desde una URI y lo convierte en BackupData.
     */
    fun importBackup(context: Context, uri: Uri): BackupData? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = InputStreamReader(inputStream)
                Gson().fromJson(reader, BackupData::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
