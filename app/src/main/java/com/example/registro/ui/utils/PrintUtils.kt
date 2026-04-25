package com.example.registro.ui.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.registro.data.TemperatureEntity
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintUtils {
    fun imprimirReporteDelDia(
        context: Context,
        registros: List<TemperatureEntity>
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val hoy = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val jobName = "Reporte_Temperaturas_$hoy"

        printManager.print(jobName, object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                val pdi = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(pdi, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                val paint = Paint()

                // Encabezado
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText("Reporte Diario de Temperaturas", 40f, 50f, paint)
                
                paint.textSize = 11f
                paint.isFakeBoldText = false
                canvas.drawText("Fecha: $hoy", 40f, 70f, paint)

                // Tabla - Encabezados
                var y = 110f
                paint.isFakeBoldText = true
                paint.textSize = 10f
                canvas.drawText("Hora", 40f, y, paint)
                canvas.drawText("Placa", 90f, y, paint)
                canvas.drawText("Unidad", 160f, y, paint)
                canvas.drawText("Temp 1", 230f, y, paint)
                canvas.drawText("Temp 2", 290f, y, paint)
                canvas.drawText("Comentarios", 350f, y, paint)
                
                // Línea de cabecera
                y += 8f
                paint.strokeWidth = 1.2f
                canvas.drawLine(40f, y, 555f, y, paint)
                
                y += 25f // Espacio para el primer registro
                paint.isFakeBoldText = false

                // Filas de la tabla
                registros.forEach { reg ->
                    if (y > 800) { /* Paginación básica no implementada */ }
                    
                    val hora = reg.fechaHora.split(" ").getOrNull(1) ?: ""
                    canvas.drawText(hora, 40f, y, paint)
                    canvas.drawText(reg.placa, 90f, y, paint)
                    canvas.drawText(reg.numeroUnidad, 160f, y, paint)
                    canvas.drawText("${reg.temp1}°", 230f, y, paint)
                    canvas.drawText("${reg.temp2}°", 290f, y, paint)
                    
                    val comentarioLimpio = reg.comentarios.replace("\n", " ")
                    val comentarioCorto = if (comentarioLimpio.length > 40) {
                        comentarioLimpio.substring(0, 37) + "..."
                    } else {
                        comentarioLimpio
                    }
                    canvas.drawText(comentarioCorto, 350f, y, paint)
                    
                    // Línea divisoria claramente debajo del texto actual
                    val oldColor = paint.color
                    paint.color = Color.LTGRAY
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(40f, y + 6f, 555f, y + 6f, paint)
                    paint.color = oldColor
                    
                    y += 25f // Moverse a la siguiente posición con buen margen
                }

                pdfDocument.finishPage(page)

                try {
                    pdfDocument.writeTo(FileOutputStream(destination?.fileDescriptor))
                } catch (e: IOException) {
                    callback?.onWriteFailed(e.toString())
                } finally {
                    pdfDocument.close()
                }

                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
        }, null)
    }
}
