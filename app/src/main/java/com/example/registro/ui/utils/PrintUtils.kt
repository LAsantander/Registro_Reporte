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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore

object PrintUtils {
    /**
     * Imprime un reporte de inspección técnica (Checklist) con fotos y comentarios.
     */
    fun imprimirChecklist(
        context: Context,
        placa: String,
        unidad: String,
        camion: String,
        voltaje: String,
        modeloUnidad: String,
        hallazgos: List<Pair<String, List<Uri>>>,
        sugerencias: String
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val hoy = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val jobName = "Inspeccion_$placa"

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
                val paint = Paint()
                val titlePaint = Paint().apply {
                    textSize = 18f
                    isFakeBoldText = true
                }
                val bodyPaint = Paint().apply {
                    textSize = 11f
                }
                
                var currentPageNumber = 1
                var pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPageNumber).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas
                var currentY = 50f
                val margin = 40f

                // Encabezado
                canvas.drawText("REPORTE DE INSPECCIÓN TÉCNICA", margin, currentY, titlePaint)
                currentY += 30f
                canvas.drawText("Placa: $placa", margin, currentY, bodyPaint)
                currentY += 20f
                canvas.drawText("Unidad: $unidad", margin, currentY, bodyPaint)
                currentY += 20f
                canvas.drawText("Modelo Unidad: $modeloUnidad", margin, currentY, bodyPaint)
                currentY += 20f
                canvas.drawText("Voltaje: $voltaje", margin, currentY, bodyPaint)
                currentY += 20f
                canvas.drawText("Camión: $camion", margin, currentY, bodyPaint)
                currentY += 20f
                canvas.drawText("Fecha: $hoy", margin, currentY, bodyPaint)
                currentY += 15f
                canvas.drawLine(margin, currentY, 555f, currentY, paint)
                currentY += 30f

                // Hallazgos
                hallazgos.forEachIndexed { index, hallazgo ->
                    val (comentario, fotos) = hallazgo

                    // Si nos quedamos sin espacio en la página, creamos una nueva
                    if (currentY > 700f) {
                        pdfDocument.finishPage(page)
                        currentPageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        currentY = 50f
                    }

                    canvas.drawText("Observación #${index + 1}:", margin, currentY, titlePaint.apply { textSize = 12f })
                    currentY += 20f
                    
                    // Comentario (Manejo de saltos de línea básico)
                    val lines = comentario.chunked(70)
                    lines.forEach { line ->
                        canvas.drawText(line, margin + 10f, currentY, bodyPaint)
                        currentY += 15f
                    }

                    // Fotos del hallazgo
                    if (fotos.isNotEmpty()) {
                        currentY += 10f
                        var currentX = margin + 10f
                        var rowMaxHeight = 0f
                        
                        fotos.forEach { uri ->
                            try {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                                if (originalBitmap != null) {
                                    val maxWidth = 150f 
                                    val maxHeight = 180f // Altura máxima para evitar que fotos verticales sean gigantes
                                    
                                    var finalWidth = maxWidth
                                    var finalHeight = originalBitmap.height * (maxWidth / originalBitmap.width)

                                    // Si es vertical y excede la altura, ajustamos proporcionalmente por la altura
                                    if (finalHeight > maxHeight) {
                                        finalHeight = maxHeight
                                        finalWidth = originalBitmap.width * (maxHeight / originalBitmap.height)
                                    }
                                    
                                    // Si la foto no cabe en la fila actual, bajamos a la siguiente
                                    if (currentX + finalWidth > 555f) {
                                        currentY += rowMaxHeight + 10f
                                        currentX = margin + 10f
                                        rowMaxHeight = 0f
                                        
                                        // Nueva página si es necesario
                                        if (currentY > 750f) {
                                            pdfDocument.finishPage(page)
                                            currentPageNumber++
                                            pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPageNumber).create()
                                            page = pdfDocument.startPage(pageInfo)
                                            canvas = page.canvas
                                            currentY = 50f
                                        }
                                    }

                                    val dstRect = android.graphics.RectF(currentX, currentY, currentX + finalWidth, currentY + finalHeight)
                                    canvas.drawBitmap(originalBitmap, null, dstRect, null)
                                    
                                    // Guardamos la altura máxima de la fila actual para el siguiente salto
                                    if (finalHeight > rowMaxHeight) rowMaxHeight = finalHeight
                                    currentX += finalWidth + 10f
                                }
                                inputStream?.close()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        // Al terminar las fotos, sumamos la altura de la última fila
                        currentY += rowMaxHeight + 10f
                    }
                    currentY += 20f
                    canvas.drawLine(margin + 10f, currentY - 10f, 500f, currentY - 10f, paint.apply { color = Color.LTGRAY })
                }

                // Dibujar Sugerencias Finales
                if (sugerencias.isNotBlank()) {
                    if (currentY > 750f) {
                        pdfDocument.finishPage(page)
                        currentPageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        currentY = 50f
                    }
                    currentY += 20f
                    canvas.drawText("Sugerencias Finales / Recomendaciones:", margin, currentY, titlePaint.apply { textSize = 12f; color = Color.BLACK })
                    currentY += 20f
                    val lines = sugerencias.chunked(70)
                    lines.forEach { line ->
                        canvas.drawText(line, margin + 10f, currentY, bodyPaint)
                        currentY += 15f
                    }
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
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN) // Paginación dinámica
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
                val paint = Paint()
                val marginStart = 25f
                val marginEnd = 570f
                val itemsPerPage = 28 // Aproximado para que quepan bien con encabezados
                
                var currentPageNumber = 1
                var recordsProcessed = 0
                
                while (recordsProcessed < registros.size) {
                    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, currentPageNumber).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas: Canvas = page.canvas
                    var currentY = 50f

                    // Títulos (Solo en la primera página o en todas según prefieras)
                    paint.textSize = 18f
                    paint.isFakeBoldText = true
                    canvas.drawText("Reporte Diario de Temperaturas", marginStart, currentY, paint)
                    
                    paint.textSize = 11f
                    paint.isFakeBoldText = false
                    currentY += 20f
                    canvas.drawText("Fecha: $hoy | Página: $currentPageNumber", marginStart, currentY, paint)

                    // Encabezados de tabla
                    currentY += 40f
                    paint.isFakeBoldText = true
                    paint.textSize = 10f
                    canvas.drawText("Hora", marginStart, currentY, paint)
                    canvas.drawText("Placa", 70f, currentY, paint)
                    canvas.drawText("Unidad", 135f, currentY, paint)
                    canvas.drawText("Temp 1", 200f, currentY, paint)
                    canvas.drawText("Temp 2", 255f, currentY, paint)
                    canvas.drawText("Comentarios", 315f, currentY, paint)
                    
                    currentY += 8f
                    paint.strokeWidth = 1.2f
                    canvas.drawLine(marginStart, currentY, marginEnd, currentY, paint)
                    
                    currentY += 25f
                    paint.isFakeBoldText = false
                    paint.textSize = 9f

                    // Filas de datos (Procesamos hasta llenar la página)
                    var countInPage = 0
                    while (recordsProcessed < registros.size && countInPage < itemsPerPage) {
                        val reg = registros[recordsProcessed]
                        
                        val hora = reg.fechaHora.split(" ").getOrNull(1) ?: ""
                        canvas.drawText(hora, marginStart, currentY, paint)
                        canvas.drawText(reg.placa, 70f, currentY, paint)
                        canvas.drawText(reg.numeroUnidad, 135f, currentY, paint)
                        canvas.drawText("${reg.temp1}${reg.unidadTemp}", 200f, currentY, paint)
                        canvas.drawText("${reg.temp2}${reg.unidadTemp}", 255f, currentY, paint)
                        
                        val comentarioLimpio = reg.comentarios.replace("\n", " ")
                        canvas.drawText(comentarioLimpio, 315f, currentY, paint)
                        
                        // Línea divisoria
                        val oldColor = paint.color
                        paint.color = Color.LTGRAY
                        paint.strokeWidth = 0.5f
                        canvas.drawLine(marginStart, currentY + 6f, marginEnd, currentY + 6f, paint)
                        paint.color = oldColor
                        
                        currentY += 25f
                        recordsProcessed++
                        countInPage++
                    }

                    pdfDocument.finishPage(page)
                    currentPageNumber++
                }

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
