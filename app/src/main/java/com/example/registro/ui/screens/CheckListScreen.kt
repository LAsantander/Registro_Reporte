package com.example.registro.ui.screens // Define el paquete donde se encuentra esta pantalla

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background // Importa la capacidad de poner fondos de color
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.* // Importa herramientas de diseño como Box, Column, Row, etc.
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState // Importa el estado para recordar la posición del scroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // Importa la capacidad de hacer scroll vertical
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Importa los componentes de Material Design 3
import androidx.compose.runtime.* // Importa las herramientas de manejo de estado (remember, mutableStateOf)
import androidx.compose.ui.Alignment // Importa alineaciones para los elementos
import androidx.compose.ui.Modifier // Importa modificadores para personalizar componentes
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color // Importa la clase para manejar colores
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight // Importa estilos de grosor de fuente
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview // Importa la capacidad de ver previas en el IDE
import androidx.compose.ui.unit.dp // Importa unidades de medida dp (densidad de píxeles)
import androidx.compose.ui.unit.sp // Importa unidades de medida sp (para texto)
import androidx.core.content.FileProvider
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.utils.ImageUtils
import com.example.registro.ui.utils.PrintUtils
import com.example.registro.ui.theme.RegistroTheme // Importa el tema visual del proyecto
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import java.io.File

/**
 * Pantalla de Inspección Técnica (Checklist).
 * Permite visualizar y gestionar la inspección de las unidades.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable // Indica que esta función es un componente de interfaz de usuario
fun CheckListScreen(
    viewModel: UnitViewModel? = null,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    
    // Estado para la búsqueda y datos de la unidad
    var searchQuery by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var numeroUnidad by remember { mutableStateOf("") }
    var camion by remember { mutableStateOf("") }
    var voltaje by remember { mutableStateOf("") }
    var modeloUnidad by remember { mutableStateOf("") }

    // Estado para comentarios y fotos
    var comentarios by remember { mutableStateOf("") }
    var sugerenciasFinales by remember { mutableStateOf("") }
    val fotosCapturadas = remember { mutableStateListOf<Uri>() }
    var tempFile by remember { mutableStateOf<File?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    
    // Estado para confirmar salida
    var showExitDialog by remember { mutableStateOf(false) }

    // Observar mensajes del ViewModel
    val errorMessage by (viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val successMessage by (viewModel?.successMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val hallazgos by (viewModel?.hallazgosChecklist?.collectAsState() ?: remember { mutableStateOf(emptyList()) })

    // Mostrar Alerta si hay un error
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Aviso") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) {
                    Text("Entendido")
                }
            },
            containerColor = Color(0xFFB71C1C),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Mostrar Alerta si el registro fue exitoso
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Éxito") },
            text = { Text(successMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) {
                    Text("Aceptar")
                }
            },
            containerColor = Color(0xFF1B5E20),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Alerta de Confirmación para Finalizar
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Finalizar Inspección?") },
            text = { Text("Se borrarán todos los datos actuales y regresará al menú principal. Asegúrese de haber impreso el reporte si lo necesita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel?.limpiarSesionChecklist()
                        searchQuery = ""
                        placa = ""
                        numeroUnidad = ""
                        camion = ""
                        voltaje = ""
                        modeloUnidad = ""
                        comentarios = ""
                        sugerenciasFinales = ""
                        fotosCapturadas.clear()
                        onBackClick()
                    }
                ) {
                    Text("SÍ, FINALIZAR", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("CANCELAR", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF37474F), // Gris oscuro profesional
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Configuracion de la cámara con compresión
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempFile != null) {
            // Comprimimos la imagen antes de añadirla a la lista
            val compressedUri = ImageUtils.compressAndResizeImage(tempFile!!)
            if (compressedUri != null) {
                fotosCapturadas.add(compressedUri)
            }
        }
    }

    // Configuración del selector de galería múltiple
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        uris.forEach { uri ->
            val compressedUri = ImageUtils.compressImageFromUri(context, uri)
            if (compressedUri != null) {
                fotosCapturadas.add(compressedUri)
            }
        }
    }


    // Lógica de búsqueda automática
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && viewModel != null) {
            val unidadEncontrada = viewModel.buscarUnidad(searchQuery)
            if (unidadEncontrada != null) {
                placa = unidadEncontrada.placa
                numeroUnidad = unidadEncontrada.numeroUnidad
                modeloUnidad = "${unidadEncontrada.marca} ${unidadEncontrada.modelo}"
            }
        }
    }

    // Foco inicial en el buscador
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Contenedor principal que ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize() // Hace que el contenedor ocupe todo el espacio disponible
            .background(Color(0xFF052A50)), // Aplica el fondo azul oscuro característico
        contentAlignment = Alignment.TopCenter // Alinea el contenido al centro en la parte superior
    ) {
        // Botón de retroceso
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.White
            )
        }

        // Columna que organiza los elementos uno debajo de otro con scroll
        Column(
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho de la pantalla
                .statusBarsPadding()
                .padding(horizontal = 24.dp) // Añade un margen lateral de 24dp
                .padding(top = 48.dp) // Añade un margen superior de 48dp
                .verticalScroll(rememberScrollState()), // Permite que la columna sea deslizable
            horizontalAlignment = Alignment.CenterHorizontally, // Centra los elementos de forma horizontal
            verticalArrangement = Arrangement.spacedBy(20.dp) // Añade un espacio de 20dp entre cada elemento
        ) {
            // Texto del título de la pantalla
            Text(
                text = "Inspección Técnica", // Texto que se muestra
                color = Color.White, // Color de texto blanco
                fontSize = 24.sp, // Tamaño de fuente de 24sp
                fontWeight = FontWeight.Bold, // Texto en negrita
                modifier = Modifier.padding(bottom = 8.dp) // Margen inferior de 8dp
            )

            // BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar unidad (Placa o ID)", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = Color.White.copy(alpha = 0.2f)
            )

            // Datos de la unidad encontrada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = placa,
                    onValueChange = { placa = it },
                    label = { Text("Placa", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = numeroUnidad,
                    onValueChange = { numeroUnidad = it },
                    label = { Text("N.Unidad", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }

            // Campo para el Modelo de Unidad (Fila Completa)
            OutlinedTextField(
                value = modeloUnidad,
                onValueChange = { modeloUnidad = it },
                label = { Text("Modelo de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Fila para Voltaje y Camión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = voltaje,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            voltaje = newValue
                        }
                    },
                    label = { Text("Voltaje", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                OutlinedTextField(
                    value = camion,
                    onValueChange = { camion = it },
                    label = { Text("Camión", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = Color.White.copy(alpha = 0.2f)
            )

            // CONTENEDOR UNIFICADO: COMENTARIOS Y FOTOS
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Observaciones y Evidencia",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Campo de Comentarios
                    OutlinedTextField(
                        value = comentarios,
                        onValueChange = { comentarios = it },
                        placeholder = { Text("Escribe aquí tus observaciones...", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            cursorColor = Color.White
                        )
                    )

                    // Sección de Fotos
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Sugerencia: Toma las fotos en horizontal para un mejor reporte",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botón para capturar foto
                            Card(
                            onClick = {
                                val file = File(context.cacheDir, "IMG_${System.currentTimeMillis()}.jpg")
                                tempFile = file
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                tempUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.size(70.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Tomar Foto",
                                    tint = Color.White
                                )
                            }
                        }

                        // Botón para seleccionar de la galería
                        Card(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.size(70.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Elegir de Galería",
                                    tint = Color.White
                                )
                            }
                        }

                        // Lista horizontal de fotos capturadas
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(end = 8.dp)
                        ) {
                            items(fotosCapturadas) { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Foto capturada",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Botón para Guardar el Reporte
                    Button(
                        onClick = {
                            if (placa.isNotBlank() && (comentarios.isNotBlank() || fotosCapturadas.isNotEmpty())) {
                                viewModel?.guardarInspeccion(
                                    placa = placa,
                                    comentarios = comentarios,
                                    fotos = fotosCapturadas.toList()
                                ) {
                                    // Limpiamos los campos al tener éxito
                                    comentarios = ""
                                    fotosCapturadas.clear()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = placa.isNotBlank() && (comentarios.isNotBlank() || fotosCapturadas.isNotEmpty()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF52A8EE),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("GUARDAR REPORTE", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // CAMPO DE SUGERENCIAS FINALES
            OutlinedTextField(
                value = sugerenciasFinales,
                onValueChange = { sugerenciasFinales = it },
                label = { Text("Sugerencias Finales / Recomendaciones", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Fila de Botones: Imprimir y Compartir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para Generar PDF (Imprimir)
                Button(
                    onClick = {
                        if (placa.isNotBlank() && hallazgos.isNotEmpty()) {
                            PrintUtils.imprimirChecklist(
                                context = context,
                                placa = placa,
                                unidad = numeroUnidad,
                                camion = camion,
                                voltaje = voltaje,
                                modeloUnidad = modeloUnidad,
                                hallazgos = hallazgos,
                                sugerencias = sugerenciasFinales
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = placa.isNotBlank() && hallazgos.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B5E20),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF1B5E20).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("IMPRIMIR", fontWeight = FontWeight.Bold)
                }

                // Botón para Compartir PDF
                Button(
                    onClick = {
                        if (placa.isNotBlank() && hallazgos.isNotEmpty()) {
                            PrintUtils.compartirChecklist(
                                context = context,
                                placa = placa,
                                unidad = numeroUnidad,
                                camion = camion,
                                voltaje = voltaje,
                                modeloUnidad = modeloUnidad,
                                hallazgos = hallazgos,
                                sugerencias = sugerenciasFinales
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = placa.isNotBlank() && hallazgos.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF52A8EE),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF52A8EE).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("COMPARTIR", fontWeight = FontWeight.Bold)
                }
            }

            // Botón para Finalizar (Limpiar y Salir)
            OutlinedButton(
                onClick = { showExitDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("FINALIZAR Y CERRAR", fontWeight = FontWeight.SemiBold)
            }

            // Espacio al final de la columna para que el scroll no corte el contenido
            Spacer(modifier = Modifier.height(40.dp)) // Margen inferior de 40dp
        }
    }
}

/**
 * Función que permite visualizar la pantalla en el panel de diseño de Android Studio.
 */
@Preview(showBackground = true) // Muestra el fondo en la previa
@Composable // Es un componente visual
fun CheckListScreenPreview() { 
    RegistroTheme { // Envuelve la pantalla en el tema del proyecto
        CheckListScreen() // Llama a la pantalla principal
    }
}
