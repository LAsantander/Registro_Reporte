package com.example.registro.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.utils.QrUtils
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianDataScreen(
    viewModel: UnitViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    
    var isEditMode by remember { mutableStateOf(false) }
    
    // Estados locales para el formulario
    var nombre by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var profesion by remember { mutableStateOf("") }
    var asunto by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }

    // Sincronizar estados locales con los ajustes guardados al entrar o cambiar modo
    LaunchedEffect(userSettings, isEditMode) {
        if (!isEditMode) {
            nombre = userSettings.defaultTechnician
            cedula = userSettings.techCedula
            empresa = userSettings.techEmpresa
            destino = userSettings.techDestino
            profesion = userSettings.techProfesion
            asunto = userSettings.techAsunto
            placa = userSettings.techPlaca
        }
    }

    // Determinar si debemos mostrar el formulario o el QR
    val hasData = userSettings.defaultTechnician.isNotBlank() && userSettings.techCedula.isNotBlank()
    val showForm = !hasData || isEditMode

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Datos del Técnico", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    if (!showForm) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Default.Edit, "Editar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF052A50))
            )
        },
        containerColor = Color(0xFF052A50)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showForm) {
                Text(
                    "Completa tu información para generar el código QR de acceso.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                TechnicianTextField("Nombre Completo", nombre) { nombre = it }
                TechnicianTextField("Cédula / ID", cedula) { cedula = it }
                TechnicianTextField("Empresa", empresa) { empresa = it }
                TechnicianTextField("Profesión", profesion) { profesion = it }
                TechnicianTextField("Destino de Visita", destino) { destino = it }
                TechnicianTextField("Asunto de la Visita", asunto) { asunto = it }
                TechnicianTextField("Placa de Vehículo", placa) { placa = it }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.updateTechnicianData(nombre, cedula, empresa, destino, profesion, asunto, placa)
                        isEditMode = false
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = nombre.isNotBlank() && cedula.isNotBlank()
                ) {
                    Text("GUARDAR Y GENERAR QR", fontWeight = FontWeight.Bold)
                }
                
                if (isEditMode) {
                    TextButton(onClick = { isEditMode = false }) {
                        Text("CANCELAR", color = Color.White.copy(alpha = 0.6f))
                    }
                }
            } else {
                // Vista del QR
                // Optimizamos el texto: eliminamos acentos en las etiquetas para máxima compatibilidad
                val qrText = """
                    Nombre: $nombre
                    Cedula: $cedula
                    Empresa: $empresa
                    Profesion: $profesion
                    Destino: $destino
                    Asunto: $asunto
                    Vehiculo: $placa
                """.trimIndent()

                val qrBitmap = remember(qrText) { QrUtils.generateQrCode(qrText) }

                Spacer(modifier = Modifier.height(40.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(300.dp).padding(16.dp)
                ) {
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Código QR",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    nombre,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Cédula: $cedula",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        qrBitmap?.let { shareQrCode(context, it, "QR_Tecnico_$nombre") }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COMPARTIR CÓDIGO QR", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TechnicianTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            cursorColor = Color.White
        )
    )
}

fun shareQrCode(context: android.content.Context, bitmap: Bitmap, fileName: String) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "$fileName.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir QR"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
