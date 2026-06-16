package com.pec.kekefit

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.media.AudioManager
import android.media.ToneGenerator
import android.widget.Toast
import java.io.File
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.animateContentSize
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.pec.kekefit.chatbot.ChatScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private const val SUPABASE_URL = "https://erwhlxfirpwjdhzfjwbz.supabase.co"
private const val SUPABASE_KEY = "sb_publishable_G1emvEzrptUBFjDH8hwtzw_9I_PdtQh"

private val VerdePrincipal = Color(0xFF38BDF8)
private val VerdeSecundario = Color(0xFF7DD3FC)
private val VerdeOscuro = Color(0xFF0F4C81)
private val VerdeMuyOscuro = Color(0xFF082F49)
private val FondoVerdeClaro = Color(0xFFF0F9FF)
private val VerdeTextoSuave = Color(0xFFE0F2FE)

object KekeFitVisualState {
    var modoOscuro by mutableStateOf(false)
}

fun prefsTemaKekeFit(context: Context) =
    context.applicationContext.getSharedPreferences("keke_fit_tema", Context.MODE_PRIVATE)

fun cargarModoOscuro(context: Context): Boolean {
    return prefsTemaKekeFit(context).getBoolean("modo_oscuro", false)
}

fun guardarModoOscuro(context: Context, valor: Boolean) {
    prefsTemaKekeFit(context).edit().putBoolean("modo_oscuro", valor).apply()
    KekeFitVisualState.modoOscuro = valor
}

@Composable
fun modoOscuroActual(): Boolean {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        KekeFitVisualState.modoOscuro = cargarModoOscuro(context)
    }

    return KekeFitVisualState.modoOscuro
}

@Composable
fun fondoPantallaActual(): Color {
    return if (modoOscuroActual()) Color(0xFF071827) else FondoVerdeClaro
}

@Composable
fun tarjetaActual(): Color {
    return if (modoOscuroActual()) Color(0xFF0F2A3D) else Color.White
}

@Composable
fun tarjetaSuaveActual(): Color {
    return if (modoOscuroActual()) Color(0xFF102F46) else Color(0xFFFFF7ED)
}

@Composable
fun textoPrincipalActual(): Color {
    return if (modoOscuroActual()) Color.White else VerdeMuyOscuro
}

@Composable
fun textoSecundarioActual(): Color {
    return if (modoOscuroActual()) Color(0xFFBAE6FD) else VerdeOscuro
}

@Composable
fun IconoFuegoRachaAnimado(
    activa: Boolean,
    modifier: Modifier = Modifier,
    sizeSp: Int = 22
) {
    val transition = rememberInfiniteTransition(label = "fuegoRacha")
    val escala by transition.animateFloat(
        initialValue = if (activa) 0.92f else 1f,
        targetValue = if (activa) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 620, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escalaFuegoRacha"
    )
    val rotacion by transition.animateFloat(
        initialValue = if (activa) -4f else 0f,
        targetValue = if (activa) 4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotacionFuegoRacha"
    )
    val alphaIcono by transition.animateFloat(
        initialValue = if (activa) 0.82f else 0.48f,
        targetValue = if (activa) 1f else 0.48f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaFuegoRacha"
    )
    val movimientoY by transition.animateFloat(
        initialValue = if (activa) 1.8f else 0f,
        targetValue = if (activa) -2.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 540, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "movimientoFuegoRacha"
    )

    Text(
        text = if (activa) "🔥" else "♨",
        modifier = modifier
            .offset(y = movimientoY.dp)
            .scale(escala)
            .rotate(rotacion)
            .alpha(alphaIcono),
        color = if (activa) Color.Unspecified else Color(0xFF94A3B8),
        fontSize = sizeSp.sp,
        fontWeight = FontWeight.Bold
    )
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                KekeFitVisualState.modoOscuro = cargarModoOscuro(context)
            }

            val modoOscuro = KekeFitVisualState.modoOscuro

            MaterialTheme(
                colorScheme = if (modoOscuro) {
                    darkColorScheme(
                        primary = VerdePrincipal,
                        secondary = VerdeSecundario,
                        background = Color(0xFF071827),
                        surface = Color(0xFF0F2A3D),
                        onPrimary = Color.White,
                        onSecondary = Color.White,
                        onBackground = Color.White,
                        onSurface = Color.White
                    )
                } else {
                    lightColorScheme(
                        primary = VerdePrincipal,
                        secondary = VerdeSecundario,
                        background = if (KekeFitVisualState.modoOscuro) Color(0xFF071827) else FondoVerdeClaro,
                        surface = Color.White
                    )
                }
            ) {
                AppPrincipal()
            }
        }
    }
}

enum class Pantalla {
    ACCESO, ONBOARDING, HOME, PLAN, INFORME, AYUDA, PERFIL, CHAT, CARGANDO
}

data class UsuarioPerfil(
    val nombre: String = "",
    val apellido: String = "",
    val fechaNacimiento: String = "",
    val edad: Int = 18,
    val alturaCm: Int = 170,
    val pesoKg: Double = 70.0,
    val genero: String = "Masculino",
    val restricciones: Set<String> = emptySet(),
    val meta: String = "Mantener peso",
    val actividad: String = "Moderada"
)

data class ComidaPlan(
    val id: Int = 0,
    val nombre: String,
    val calorias: Int,
    val tipo: String,
    val proteinas: Int = 0,
    val carbohidratos: Int = 0,
    val grasas: Int = 0,
    val restricciones: List<String> = emptyList(),
    val ingredientes: List<String> = emptyList()
)

data class ResultadoNutricional(
    val calorias: Int,
    val aguaTexto: String,
    val vasosMeta: Int,
    val imc: String,
    val estadoImc: String,
    val proteinas: Int,
    val recomendacion: String
)

data class RegistroComidaMensual(
    val dia: Int,
    val tipo: String,
    val comidaRecomendada: String,
    val comidaReal: String = "",
    val comidaCumplida: Boolean = false
)



data class PlanComidaSupabase(
    val idPlan: Int = 0,
    val idUsuario: Int = 0,
    val fecha: String = "",
    val tipoComida: String = "",
    val idComida: Int = 0
)

data class RegistroAguaDia(
    val fecha: String = fechaHoyDispositivo(),
    val vasosConsumidos: Int = 0,
    val vasosMeta: Int = 8
)

data class EstadoRacha(
    val dias: Int = 0,
    val activa: Boolean = false,
    val informeHoyEnviado: Boolean = false,
    val ultimaFechaInforme: String = ""
)

fun fechaHoyDispositivo(): String {
    val c = Calendar.getInstance()
    val y = c.get(Calendar.YEAR)
    val m = c.get(Calendar.MONTH) + 1
    val d = c.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(y, m, d)
}

fun reproducirClickSuave(context: Context) {
    try {
        val tg = ToneGenerator(AudioManager.STREAM_MUSIC, 35)
        tg.startTone(ToneGenerator.TONE_PROP_BEEP, 55)
    } catch (_: Exception) {
        // Si el dispositivo no permite reproducir el tono, la app sigue igual.
    }
}

fun prefsAgua(context: Context, uid: String) = context.getSharedPreferences("agua_$uid", Context.MODE_PRIVATE)

fun cargarVasosConsumidosLocal(context: Context, uid: String, fecha: String = fechaHoyDispositivo()): Int {
    return prefsAgua(context, uid).getInt("vasos_$fecha", 0)
}

fun cargarMetaVasosLocal(context: Context, uid: String, metaCalculada: Int, fecha: String = fechaHoyDispositivo()): Int {
    return prefsAgua(context, uid).getInt("meta_$fecha", metaCalculada.coerceAtLeast(1))
}

fun guardarAguaLocal(context: Context, uid: String, vasos: Int, meta: Int, fecha: String = fechaHoyDispositivo()) {
    prefsAgua(context, uid).edit()
        .putInt("vasos_$fecha", vasos.coerceIn(0, 30))
        .putInt("meta_$fecha", meta.coerceIn(1, 30))
        .apply()
}

fun textoAguaLocal(context: Context, uid: String, metaCalculada: Int): String {
    val fecha = fechaHoyDispositivo()
    val vasos = cargarVasosConsumidosLocal(context, uid, fecha)
    val meta = cargarMetaVasosLocal(context, uid, metaCalculada, fecha)
    return "$vasos / $meta vasos"
}

fun indiceDiaSemanaActual(): Int {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 6
    }
}

fun nombreDiaSemanaActual(): String = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")[indiceDiaSemanaActual()]

fun fechaDispositivoConOffset(offsetDias: Int): String {
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_MONTH, offsetDias)
    return "%04d-%02d-%02d".format(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}

fun letraDiaDispositivoConOffset(offsetDias: Int): String {
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_MONTH, offsetDias)
    return when (c.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "L"
        Calendar.TUESDAY -> "M"
        Calendar.WEDNESDAY -> "M"
        Calendar.THURSDAY -> "J"
        Calendar.FRIDAY -> "V"
        Calendar.SATURDAY -> "S"
        else -> "D"
    }
}

fun cargarProgresoInformeLocal(context: Context, uid: String, fecha: String): Float {
    val cantidadMarcada = cargarCumplidasLocal(context, uid, fecha).size
    return (cantidadMarcada.toFloat() / 4f).coerceIn(0f, 1f)
}

fun cargarProgresoSemanalLocal(context: Context, uid: String): List<Float> {
    return (-6..0).map { offset ->
        cargarProgresoInformeLocal(context, uid, fechaDispositivoConOffset(offset))
    }
}

fun cargarEtiquetasSemanaDispositivo(): List<String> {
    return (-6..0).map { offset -> letraDiaDispositivoConOffset(offset) }
}

fun fechaAyerDispositivo(): String {
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_MONTH, -1)
    return "%04d-%02d-%02d".format(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}

fun cargarEstadoRacha(context: Context, uid: String): EstadoRacha {
    val prefs = context.getSharedPreferences("racha_$uid", Context.MODE_PRIVATE)
    val hoy = fechaHoyDispositivo()
    val ayer = fechaAyerDispositivo()
    val ultima = prefs.getString("ultimaFechaInforme", "").orEmpty()
    val diasGuardados = prefs.getInt("diasRacha", 0)

    return when {
        ultima == hoy -> EstadoRacha(diasGuardados, true, true, ultima)
        ultima == ayer -> EstadoRacha(diasGuardados, true, false, ultima)
        ultima.isBlank() -> EstadoRacha(0, false, false, ultima)
        else -> EstadoRacha(0, false, false, ultima)
    }
}

fun registrarInformeHoy(context: Context, uid: String): EstadoRacha {
    val prefs = context.getSharedPreferences("racha_$uid", Context.MODE_PRIVATE)
    val actual = cargarEstadoRacha(context, uid)
    val hoy = fechaHoyDispositivo()
    val nuevosDias = if (actual.informeHoyEnviado) actual.dias else if (actual.ultimaFechaInforme == fechaAyerDispositivo()) actual.dias + 1 else 1

    prefs.edit()
        .putString("ultimaFechaInforme", hoy)
        .putInt("diasRacha", nuevosDias)
        .apply()

    return EstadoRacha(nuevosDias, true, true, hoy)
}

fun guardarInformeLocal(context: Context, uid: String, fecha: String, comidasCumplidas: Set<String>, comidasReales: Map<String, String>) {
    val prefs = context.getSharedPreferences("informes_$uid", Context.MODE_PRIVATE)
    prefs.edit()
        .putStringSet("cumplidas_$fecha", comidasCumplidas)
        .putString("reales_$fecha", comidasReales.entries.joinToString("||") { "${it.key}::${it.value}" })
        .apply()
}

fun cargarCumplidasLocal(context: Context, uid: String, fecha: String): Set<String> {
    return context.getSharedPreferences("informes_$uid", Context.MODE_PRIVATE)
        .getStringSet("cumplidas_$fecha", emptySet()).orEmpty()
}

fun crearPdfInforme(context: Context, uid: String, porcentaje: Int, comidasCumplidas: Set<String>, comidasReales: Map<String, String>) {
    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 16f }
    var y = 48f
    canvas.drawText("Keke Fit - Informe ${fechaHoyDispositivo()}", 40f, y, paint)
    y += 32f
    canvas.drawText("Progreso: $porcentaje%", 40f, y, paint)
    y += 28f
    canvas.drawText("Comidas marcadas: ${comidasCumplidas.size}", 40f, y, paint)
    y += 28f
    comidasReales.entries.take(22).forEach {
        canvas.drawText("${it.key}: ${it.value.take(55)}", 40f, y, paint)
        y += 24f
    }
    pdf.finishPage(page)
    val file = File(context.getExternalFilesDir(null), "keke_informe_${uid}_${fechaHoyDispositivo()}.pdf")
    file.outputStream().use { pdf.writeTo(it) }
    pdf.close()
    Toast.makeText(context, "PDF guardado: ${file.name}", Toast.LENGTH_LONG).show()
}

@Composable
fun BotonVolverFlotante(onVolver: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        FloatingActionButton(
            onClick = onVolver,
            containerColor = VerdePrincipal,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }
    }
}

suspend fun leerTablaSupabase(tabla: String, query: String = "select=*"): JSONArray = withContext(Dispatchers.IO) {
    val separador = if (query.isBlank()) "select=*" else query.trim().removePrefix("?")
    val url = URL("$SUPABASE_URL/rest/v1/$tabla?$separador")
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("apikey", SUPABASE_KEY)
        connection.setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Prefer", "count=exact")

        val code = connection.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val body = stream.bufferedReader().use { it.readText() }

        Log.d("SUPABASE_DEBUG", "URL: $url")
        Log.d("SUPABASE_DEBUG", "Código: $code")
        Log.d("SUPABASE_DEBUG", "Respuesta: $body")

        if (code !in 200..299) {
            throw Exception("Supabase respondió error $code en $tabla: $body")
        }

        JSONArray(body)
    } finally {
        connection.disconnect()
    }
}

suspend fun insertarSupabase(tabla: String, jsonBody: String) = withContext(Dispatchers.IO) {
    val url = URL("$SUPABASE_URL/rest/v1/$tabla")
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "POST"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.doOutput = true
        connection.setRequestProperty("apikey", SUPABASE_KEY)
        connection.setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Prefer", "count=exact")
        connection.setRequestProperty("Prefer", "return=minimal")
        connection.outputStream.use { it.write(jsonBody.toByteArray(Charsets.UTF_8)) }

        val code = connection.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

        Log.d("SUPABASE_DEBUG", "POST $tabla -> $code $body")

        if (code !in 200..299) {
            throw Exception("No se pudo guardar en Supabase ($tabla): $body")
        }
    } finally {
        connection.disconnect()
    }
}

fun separarRestricciones(texto: String): List<String> {
    return texto
        .split(",", ";", "|")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}

fun normalizarListaBusqueda(texto: String): String {
    return normalizarRestriccion(texto)
        .replace("_", " ")
        .replace("-", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}


fun comidasFallbackOfflineKekeFit(): List<ComidaPlan> = listOf(
    ComidaPlan(
        id = 9001,
        nombre = "Avena con banana",
        calorias = 420,
        tipo = "desayuno",
        proteinas = 18,
        carbohidratos = 65,
        grasas = 10,
        restricciones = listOf("Vegetariano"),
        ingredientes = listOf("avena", "banana", "leche", "yogur")
    ),
    ComidaPlan(
        id = 9002,
        nombre = "Tostadas con huevo",
        calorias = 480,
        tipo = "desayuno",
        proteinas = 24,
        carbohidratos = 48,
        grasas = 20,
        restricciones = emptyList(),
        ingredientes = listOf("pan", "huevo", "queso", "fruta")
    ),
    ComidaPlan(
        id = 9003,
        nombre = "Pollo con arroz y ensalada",
        calorias = 720,
        tipo = "almuerzo",
        proteinas = 48,
        carbohidratos = 82,
        grasas = 18,
        restricciones = emptyList(),
        ingredientes = listOf("pollo", "arroz", "lechuga", "tomate")
    ),
    ComidaPlan(
        id = 9004,
        nombre = "Bowl de legumbres",
        calorias = 640,
        tipo = "almuerzo",
        proteinas = 28,
        carbohidratos = 92,
        grasas = 16,
        restricciones = listOf("Vegetariano"),
        ingredientes = listOf("lentejas", "arroz", "verduras", "aceite de oliva")
    ),
    ComidaPlan(
        id = 9005,
        nombre = "Yogur con granola",
        calorias = 360,
        tipo = "merienda",
        proteinas = 16,
        carbohidratos = 48,
        grasas = 11,
        restricciones = listOf("Vegetariano"),
        ingredientes = listOf("yogur", "granola", "fruta")
    ),
    ComidaPlan(
        id = 9006,
        nombre = "Sándwich integral de atún",
        calorias = 430,
        tipo = "merienda",
        proteinas = 32,
        carbohidratos = 44,
        grasas = 12,
        restricciones = emptyList(),
        ingredientes = listOf("pan integral", "atún", "tomate", "queso")
    ),
    ComidaPlan(
        id = 9007,
        nombre = "Carne magra con puré",
        calorias = 690,
        tipo = "cena",
        proteinas = 45,
        carbohidratos = 62,
        grasas = 24,
        restricciones = emptyList(),
        ingredientes = listOf("carne", "papa", "verduras")
    ),
    ComidaPlan(
        id = 9008,
        nombre = "Tortilla de verduras",
        calorias = 520,
        tipo = "cena",
        proteinas = 26,
        carbohidratos = 34,
        grasas = 30,
        restricciones = listOf("Vegetariano"),
        ingredientes = listOf("huevo", "espinaca", "cebolla", "queso")
    )
)

fun condicionesFallbackOfflineKekeFit(): List<String> = listOf(
    "Sin restricciones",
    "Vegetariano",
    "Sin lactosa",
    "Sin gluten",
    "Diabetes",
    "Hipertensión",
    "Alergia al maní",
    "Alergia al huevo"
)

fun esErrorRedKekeFit(e: Exception): Boolean {
    val texto = (e.localizedMessage ?: e.message ?: "").lowercase()
    return e is java.net.UnknownHostException ||
            e is java.net.SocketTimeoutException ||
            texto.contains("unable to resolve host") ||
            texto.contains("failed to connect") ||
            texto.contains("timeout") ||
            texto.contains("no address associated")
}

fun mensajeRedKekeFit(e: Exception): String {
    return if (esErrorRedKekeFit(e)) {
        "No hay conexión con Supabase/Internet en este momento. La app va a usar datos locales temporales."
    } else {
        e.localizedMessage ?: "No se pudo conectar con Supabase."
    }
}

suspend fun obtenerComidasDesdeSupabase(): List<ComidaPlan> {
    return try {
        val json = leerTablaSupabase("comida2", "select=*&order=id_comida2.asc")
        val lista = mutableListOf<ComidaPlan>()

        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)

            val id = obj.optInt("id_comida2", 0)
            val nombre = obj.optString("nombre2", "")
            val calorias = obj.optInt("calorias2", 0)
            val tipo = obj.optString("tipo2", "")
            val proteinas = obj.optInt("proteinas_g2", 0)
            val carbohidratos = obj.optInt("carbohidratos_g2", 0)
            val grasas = obj.optInt("grasas_g2", 0)
            val restriccionesTexto = obj.optString("restricciones2", "")
            val ingredientesTexto = obj.optString("ingredientes2", "")
            val preferenciasUsuario = obj.optString("preferencias_usuario2", "")
            val restricciones = separarRestricciones(restriccionesTexto)
            val ingredientes = separarRestricciones(ingredientesTexto).ifEmpty {
                separarRestricciones(preferenciasUsuario).ifEmpty { listOf(nombre, tipo) }
            }

            if (nombre.isNotBlank()) {
                lista.add(
                    ComidaPlan(
                        id = id,
                        nombre = nombre,
                        calorias = calorias,
                        tipo = tipo,
                        proteinas = proteinas,
                        carbohidratos = carbohidratos,
                        grasas = grasas,
                        restricciones = restricciones,
                        ingredientes = ingredientes
                    )
                )
            }
        }

        lista.ifEmpty { comidasFallbackOfflineKekeFit() }
    } catch (e: Exception) {
        Log.e("SUPABASE_DEBUG", mensajeRedKekeFit(e), e)
        comidasFallbackOfflineKekeFit()
    }
}

suspend fun obtenerCondicionesDesdeSupabase(): List<String> {
    return try {
        val json = leerTablaSupabase("condicion2", "select=*&order=id_condicion2.asc")
        val lista = mutableListOf<String>()

        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            val nombre = obj.optString("nombre2", "")

            if (nombre.isNotBlank() && lista.none { normalizarListaBusqueda(it) == normalizarListaBusqueda(nombre) }) {
                lista.add(nombre)
            }
        }

        lista.ifEmpty { condicionesFallbackOfflineKekeFit() }
    } catch (e: Exception) {
        Log.e("SUPABASE_DEBUG", mensajeRedKekeFit(e), e)
        condicionesFallbackOfflineKekeFit()
    }
}

suspend fun obtenerPlanDesdeSupabase(): List<PlanComidaSupabase> {
    return try {
        val json = leerTablaSupabase("plan2", "select=*&order=fecha2.asc,id_plan2.asc")
        val lista = mutableListOf<PlanComidaSupabase>()

        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            lista.add(
                PlanComidaSupabase(
                    idPlan = obj.optInt("id_plan2", 0),
                    idUsuario = obj.optInt("id_usuario2", 0),
                    fecha = obj.optString("fecha2", ""),
                    tipoComida = obj.optString("tipo_comida2", ""),
                    idComida = obj.optInt("id_comida2", 0)
                )
            )
        }

        lista
    } catch (e: Exception) {
        Log.e("SUPABASE_DEBUG", mensajeRedKekeFit(e), e)
        emptyList()
    }
}

suspend fun obtenerHistorialAguaDesdeSupabase(): List<RegistroAguaDia> {
    return try {
        val json = leerTablaSupabase("historial_agua2", "select=*&order=fecha2.asc")
        val lista = mutableListOf<RegistroAguaDia>()

        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            lista.add(
                RegistroAguaDia(
                    fecha = obj.optString("fecha2", ""),
                    vasosConsumidos = obj.optInt("vasos_consumidos2", 0),
                    vasosMeta = obj.optInt("vasos_meta2", 8)
                )
            )
        }

        lista
    } catch (e: Exception) {
        Log.e("SUPABASE_DEBUG", mensajeRedKekeFit(e), e)
        emptyList()
    }
}

suspend fun guardarAguaSupabase(idUsuario2: Int, vasosConsumidos: Int, vasosMeta: Int) {
    try {
        val body = """{"id_usuario2":$idUsuario2,"fecha2":"${fechaHoyDispositivo()}","vasos_consumidos2":$vasosConsumidos,"vasos_meta2":$vasosMeta}"""
        insertarSupabase("historial_agua2", body)
    } catch (e: Exception) {
        Log.e("SUPABASE_DEBUG", mensajeRedKekeFit(e), e)
    }
}

fun Context.findActivity(): Activity {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> throw IllegalStateException("No se encontró Activity en el contexto.")
    }
}

fun guardarPerfilEnFirestore(
    uid: String,
    perfil: UsuarioPerfil,
    bienvenidaPendiente: Boolean,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    val data = hashMapOf(
        "nombre" to perfil.nombre,
        "apellido" to perfil.apellido,
        "fechaNacimiento" to perfil.fechaNacimiento,
        "edad" to perfil.edad,
        "alturaCm" to perfil.alturaCm,
        "pesoKg" to perfil.pesoKg,
        "genero" to perfil.genero,
        "restricciones" to perfil.restricciones.toList(),
        "meta" to perfil.meta,
        "actividad" to perfil.actividad,
        "perfilCompleto" to true,
        "bienvenidaPendiente" to bienvenidaPendiente,
        "updatedAt" to FieldValue.serverTimestamp()
    )

    db.collection("usuarios")
        .document(uid)
        .set(data)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e ->
            onError(e.localizedMessage ?: "No se pudo guardar el perfil.")
        }
}

fun cargarPerfilDesdeFirestore(
    uid: String,
    onResult: (UsuarioPerfil?, Boolean, Boolean) -> Unit,
    onError: (String) -> Unit
) {
    FirebaseFirestore.getInstance()
        .collection("usuarios")
        .document(uid)
        .get()
        .addOnSuccessListener { doc ->
            if (!doc.exists()) {
                onResult(null, false, false)
                return@addOnSuccessListener
            }

            val restriccionesGuardadas =
                (doc.get("restricciones") as? List<*>)?.filterIsInstance<String>()?.toSet()
                    ?: emptySet()

            val perfil = UsuarioPerfil(
                nombre = doc.getString("nombre") ?: "",
                apellido = doc.getString("apellido") ?: "",
                fechaNacimiento = doc.getString("fechaNacimiento") ?: "",
                edad = (doc.getLong("edad") ?: 18L).toInt(),
                alturaCm = (doc.getLong("alturaCm") ?: 170L).toInt(),
                pesoKg = doc.getDouble("pesoKg") ?: 70.0,
                genero = doc.getString("genero") ?: "Masculino",
                restricciones = restriccionesGuardadas,
                meta = doc.getString("meta") ?: "Mantener peso",
                actividad = doc.getString("actividad") ?: "Moderada"
            )

            val bienvenidaPendiente = doc.getBoolean("bienvenidaPendiente") ?: false
            onResult(perfil, true, bienvenidaPendiente)
        }
        .addOnFailureListener { e ->
            onError(e.localizedMessage ?: "No se pudo leer el perfil.")
        }
}

fun marcarBienvenidaComoVista(uid: String) {
    FirebaseFirestore.getInstance()
        .collection("usuarios")
        .document(uid)
        .update("bienvenidaPendiente", false)
}

@Composable
fun AppPrincipal() {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    var errorSesion by remember { mutableStateOf("") }

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    var pantallaActual by remember { mutableStateOf(Pantalla.CARGANDO) }
    var usuario by remember { mutableStateOf(UsuarioPerfil()) }
    var bienvenidaPendiente by remember { mutableStateOf(false) }

    LaunchedEffect(auth.currentUser?.uid, pantallaActual) {
        if (pantallaActual != Pantalla.CARGANDO) return@LaunchedEffect

        val currentUser = auth.currentUser

        if (currentUser == null) {
            usuario = UsuarioPerfil()
            bienvenidaPendiente = false
            pantallaActual = Pantalla.ACCESO
        } else {
            cargarPerfilDesdeFirestore(
                uid = currentUser.uid,
                onResult = { perfil, existe, bienvenida ->
                    if (existe && perfil != null) {
                        usuario = perfil
                        bienvenidaPendiente = bienvenida
                        pantallaActual = Pantalla.HOME
                    } else {
                        val displayName = currentUser.displayName ?: ""
                        val nombreBase = displayName.substringBefore(" ").trim()
                        val apellidoBase = displayName.substringAfter(" ", "").trim()

                        usuario = UsuarioPerfil(
                            nombre = nombreBase,
                            apellido = apellidoBase
                        )
                        bienvenidaPendiente = true
                        pantallaActual = Pantalla.ONBOARDING
                    }
                },
                onError = { mensajeError ->
                    errorSesion = "Se inició sesión, pero no se pudo leer tu perfil en Firestore. Detalle: $mensajeError"
                    pantallaActual = Pantalla.ACCESO
                }
            )
        }
    }

    when (pantallaActual) {
        Pantalla.CARGANDO -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fondoPantallaActual()),
                contentAlignment = Alignment.Center
            ) {
                IndicadorCargaVerde()
            }
        }

        Pantalla.ACCESO -> PantallaAcceso(
            googleClient = googleClient,
            errorInicial = errorSesion,
            onConsumirErrorInicial = { errorSesion = "" },
            onLoginSuccess = {
                errorSesion = ""
                pantallaActual = Pantalla.CARGANDO
            }
        )

        Pantalla.ONBOARDING -> PantallaFormularioPerfil(
            titulo = "Completá tu perfil",
            subtitulo = "Esto personaliza tus cálculos y tu experiencia.",
            textoBoton = "Continuar",
            perfilInicial = usuario,
            onContinuar = { perfil ->
                val uid = auth.currentUser?.uid ?: return@PantallaFormularioPerfil
                guardarPerfilEnFirestore(
                    uid = uid,
                    perfil = perfil,
                    bienvenidaPendiente = true,
                    onSuccess = {
                        usuario = perfil
                        bienvenidaPendiente = true
                        pantallaActual = Pantalla.HOME
                    },
                    onError = {}
                )
            }
        )

        Pantalla.HOME -> PantallaHomeProfesional(
            usuario = usuario,
            userPhotoUrl = auth.currentUser?.photoUrl?.toString(),
            bienvenidaPendiente = bienvenidaPendiente,
            onConsumirBienvenida = {
                val uid = auth.currentUser?.uid ?: return@PantallaHomeProfesional
                bienvenidaPendiente = false
                marcarBienvenidaComoVista(uid)
            },
            onIrPlan = { pantallaActual = Pantalla.PLAN },
            onIrInforme = { pantallaActual = Pantalla.INFORME },
            onIrAyuda = { pantallaActual = Pantalla.AYUDA },
            onIrPerfil = { pantallaActual = Pantalla.PERFIL },
            onIrChat = { pantallaActual = Pantalla.CHAT },
            onCerrarSesion = {
                auth.signOut()
                googleClient.signOut()
                usuario = UsuarioPerfil()
                bienvenidaPendiente = false
                pantallaActual = Pantalla.ACCESO
            }
        )

        Pantalla.PLAN -> PantallaPlanComidas(
            usuario = usuario,
            onVolver = { pantallaActual = Pantalla.HOME }
        )

        Pantalla.INFORME -> PantallaInformeMensual(
            usuario = usuario,
            onVolver = { pantallaActual = Pantalla.HOME }
        )

        Pantalla.AYUDA -> PantallaAyudaFeedback(
            onVolver = { pantallaActual = Pantalla.HOME }
        )

        Pantalla.PERFIL -> PantallaFormularioPerfil(
            titulo = "Editar perfil",
            subtitulo = "Actualizá tus datos personales.",
            textoBoton = "Guardar cambios",
            perfilInicial = usuario,
            onContinuar = { perfil ->
                val uid = auth.currentUser?.uid ?: return@PantallaFormularioPerfil
                guardarPerfilEnFirestore(
                    uid = uid,
                    perfil = perfil,
                    bienvenidaPendiente = false,
                    onSuccess = {
                        usuario = perfil
                        pantallaActual = Pantalla.HOME
                    },
                    onError = {}
                )
            }
        )

        Pantalla.CHAT -> ChatScreen(
            onVolver = { pantallaActual = Pantalla.HOME }
        )
    }
}

@Composable
fun PantallaAcceso(
    googleClient: com.google.android.gms.auth.api.signin.GoogleSignInClient,
    errorInicial: String,
    onConsumirErrorInicial: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var modoRegistro by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    LaunchedEffect(errorInicial) {
        if (errorInicial.isNotBlank()) {
            error = errorInicial
            onConsumirErrorInicial()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        cargando = false

        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrBlank()) {
                error = "Google devolvió un token vacío."
                return@rememberLauncherForActivityResult
            }

            val credential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { loginTask ->
                    if (loginTask.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        val detalle = loginTask.exception?.localizedMessage
                            ?: "Error al iniciar sesión con Google."
                        error = if (detalle.contains("Failed to connect", ignoreCase = true) ||
                            detalle.contains("Unable to resolve host", ignoreCase = true)
                        ) {
                            "No se pudo conectar con Google. Revisá Internet/DNS del celu o probá con otra red."
                        } else {
                            detalle
                        }
                    }
                }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            error = when (e.statusCode) {
                7 -> "Google Sign-In no pudo conectarse a Google. Revisá Internet/DNS del celu o probá con otra red."
                10 -> "Google Sign-In está mal configurado. Revisá el SHA-1/SHA-256 y el google-services.json de Firebase."
                12501 -> "Inicio con Google cancelado."
                else -> "Google Sign-In falló. Código: ${e.statusCode}"
            }
        } catch (e: Exception) {
            val detalle = e.localizedMessage ?: "Error con Google Sign-In."
            error = if (detalle.contains("Failed to connect", ignoreCase = true) ||
                detalle.contains("Unable to resolve host", ignoreCase = true)
            ) {
                "No se pudo conectar con Google. Revisá Internet/DNS del celu o probá con otra red."
            } else {
                detalle
            }
        }
    }

    fun validarCampos(): Boolean {
        val emailLimpio = email.trim()

        if (emailLimpio.isBlank() || password.isBlank()) {
            error = "Completá correo y contraseña."
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailLimpio).matches()) {
            error = "Ingresá un correo válido."
            return false
        }

        if (modoRegistro && password.length < 6) {
            error = "La contraseña debe tener al menos 6 caracteres."
            return false
        }

        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(VerdeMuyOscuro, Color.Black, VerdePrincipal)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.16f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Logo Keke Fit",
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Keke Fit",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nutrición inteligente y progreso real",
            color = VerdeTextoSuave,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaActual())
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (modoRegistro) "Crear cuenta" else "Iniciar sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        error = ""
                        mensaje = ""
                    },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = ""
                        mensaje = ""
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        error = ""
                        mensaje = ""

                        if (!validarCampos()) return@Button

                        val emailLimpio = email.trim()
                        cargando = true

                        if (modoRegistro) {
                            auth.createUserWithEmailAndPassword(emailLimpio, password)
                                .addOnCompleteListener(activity) { task ->
                                    if (!task.isSuccessful) {
                                        cargando = false
                                        error = task.exception?.localizedMessage
                                            ?: "No se pudo registrar el usuario."
                                        return@addOnCompleteListener
                                    }

                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verifyTask ->
                                            cargando = false

                                            if (verifyTask.isSuccessful) {
                                                auth.signOut()
                                                mensaje =
                                                    "Te enviamos un correo de verificación. Verificá tu mail antes de iniciar sesión."
                                                modoRegistro = false
                                                password = ""
                                            } else {
                                                error = verifyTask.exception?.localizedMessage
                                                    ?: "Se creó la cuenta, pero no se pudo enviar el correo de verificación."
                                            }
                                        }
                                }
                        } else {
                            auth.signInWithEmailAndPassword(emailLimpio, password)
                                .addOnCompleteListener(activity) { task ->
                                    if (!task.isSuccessful) {
                                        cargando = false
                                        error = task.exception?.localizedMessage
                                            ?: "Credenciales inválidas."
                                        return@addOnCompleteListener
                                    }

                                    val user = auth.currentUser
                                    user?.reload()?.addOnCompleteListener {
                                        cargando = false
                                        val verificado = auth.currentUser?.isEmailVerified ?: false

                                        if (!verificado) {
                                            auth.signOut()
                                            error =
                                                "Tu correo todavía no está verificado. Revisá tu mail antes de ingresar."
                                        } else {
                                            onLoginSuccess()
                                        }
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                    enabled = !cargando
                ) {
                    Text(if (modoRegistro) "Registrarme" else "Ingresar")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (modoRegistro) {
                        "¿Ya tenés cuenta? Iniciar sesión"
                    } else {
                        "¿No tenés cuenta? Registrarse"
                    },
                    color = VerdePrincipal,
                    modifier = Modifier.clickable {
                        modoRegistro = !modoRegistro
                        error = ""
                        mensaje = ""
                    }
                )

                if (!modoRegistro) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Restablecer contraseña",
                        color = VerdePrincipal,
                        modifier = Modifier.clickable {
                            error = ""
                            mensaje = ""
                            val emailLimpio = email.trim()

                            if (!Patterns.EMAIL_ADDRESS.matcher(emailLimpio).matches()) {
                                error = "Escribí primero un correo válido para enviarte el enlace."
                                return@clickable
                            }

                            auth.sendPasswordResetEmail(emailLimpio)
                                .addOnSuccessListener {
                                    mensaje = "Te enviamos un correo para restablecer la contraseña."
                                }
                                .addOnFailureListener { e ->
                                    error = e.localizedMessage
                                        ?: "No se pudo enviar el correo de restablecimiento."
                                }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = {
                        error = ""
                        mensaje = ""
                        cargando = true

                        launcher.launch(googleClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    enabled = !cargando
                ) {
                    Text("Continuar con Google")
                }

                if (cargando) {
                    Spacer(modifier = Modifier.height(16.dp))

                    IndicadorCargaVerde(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (mensaje.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = mensaje,
                        color = textoSecundarioActual(),
                        fontSize = 14.sp
                    )
                }

                if (error.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


fun calcularEdadDesdeFecha(fecha: String): Int? {
    val partes = fecha.split("/")
    if (partes.size != 3) return null
    val dia = partes[0].toIntOrNull() ?: return null
    val mes = partes[1].toIntOrNull() ?: return null
    val anio = partes[2].toIntOrNull() ?: return null

    val hoy = Calendar.getInstance()
    var edad = hoy.get(Calendar.YEAR) - anio
    val mesActual = hoy.get(Calendar.MONTH) + 1
    val diaActual = hoy.get(Calendar.DAY_OF_MONTH)

    if (mesActual < mes || (mesActual == mes && diaActual < dia)) edad--
    return edad.coerceAtLeast(0)
}

fun mensajeWidgetRacha(estado: EstadoRacha): String {
    return when {
        estado.informeHoyEnviado && estado.dias >= 7 -> "Vas excelente. Ya cuidaste tu racha hoy y venís muy constante."
        estado.informeHoyEnviado -> "Listo por hoy. Mañana volvé para mantener la racha viva."
        estado.activa && estado.dias >= 3 -> "Buen camino. Completá el informe de hoy antes de dormir."
        estado.activa -> "Tu racha sigue viva. Hacé el informe para no perderla."
        else -> "Hoy podés empezar una nueva racha. Completá tu primer informe."
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioPerfil(
    titulo: String,
    subtitulo: String,
    textoBoton: String,
    perfilInicial: UsuarioPerfil,
    onContinuar: (UsuarioPerfil) -> Unit
) {
    val context = LocalContext.current
    val calendario = remember { Calendar.getInstance() }

    var nombre by remember { mutableStateOf(perfilInicial.nombre) }
    var apellido by remember { mutableStateOf(perfilInicial.apellido) }
    var fechaNacimiento by remember { mutableStateOf(perfilInicial.fechaNacimiento) }
    var edad by remember { mutableStateOf(perfilInicial.edad.toString()) }
    var altura by remember { mutableStateOf(perfilInicial.alturaCm.toString()) }
    var peso by remember { mutableStateOf(perfilInicial.pesoKg.toString()) }
    var genero by remember { mutableStateOf(perfilInicial.genero) }
    var meta by remember { mutableStateOf(perfilInicial.meta) }
    var actividad by remember { mutableStateOf(perfilInicial.actividad) }
    var restriccionesSeleccionadas by remember { mutableStateOf(perfilInicial.restricciones) }
    var error by remember { mutableStateOf("") }
    var mostrarFechaPicker by remember { mutableStateOf(false) }
    var mostrarAvisoMenor by remember { mutableStateOf(false) }
    var perfilPendienteMenor by remember { mutableStateOf<UsuarioPerfil?>(null) }

    var restriccionesDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }
    var cargandoRestricciones by remember { mutableStateOf(true) }
    var errorRestricciones by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            restriccionesDisponibles = obtenerCondicionesDesdeSupabase()
            errorRestricciones = ""
        } catch (e: Exception) {
            restriccionesDisponibles = emptyList()
            errorRestricciones = mensajeRedKekeFit(e)
        } finally {
            cargandoRestricciones = false
        }
    }

    val metas = listOf("Bajar peso", "Mantener peso", "Subir peso", "Ganar músculo", "Mejorar hábitos")
    val actividades = listOf("Sedentaria", "Ligera", "Moderada", "Alta")
    val generos = listOf("Masculino", "Femenino")

    if (mostrarFechaPicker) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaNacimiento = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                calcularEdadDesdeFecha(fechaNacimiento)?.let { edadCalculada ->
                    edad = edadCalculada.toString()
                    if (edadCalculada < 16) mostrarAvisoMenor = true
                }
                mostrarFechaPicker = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoPantallaActual())
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textoPrincipalActual()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitulo,
            color = textoSecundarioActual()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Datos personales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de nacimiento") },
                    leadingIcon = { Icon(Icons.Default.DateRange, null) },
                    trailingIcon = {
                        IconButton(onClick = { mostrarFechaPicker = true }) {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { mostrarFechaPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                ) {
                    Text(if (fechaNacimiento.isBlank()) "Seleccionar fecha" else "Cambiar fecha")
                }

                calcularEdadDesdeFecha(fechaNacimiento)?.let { edadCalculada ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Según la fecha de nacimiento tenés $edadCalculada años.",
                        color = if (edadCalculada < 16) Color(0xFFB45309) else VerdeOscuro,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it.filter { c -> c.isDigit() } },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it.filter { c -> c.isDigit() } },
                    label = { Text("Altura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = peso,
                    onValueChange = {
                        peso = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.')
                    },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Género",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowChips(
                    opciones = generos,
                    seleccionada = genero,
                    onSeleccionar = { genero = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Alergias / condiciones / restricciones",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                when {
                    cargandoRestricciones -> {
                        IndicadorCargaVerde()
                    }

                    errorRestricciones.isNotBlank() -> {
                        Text(
                            text = errorRestricciones,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    restriccionesDisponibles.isEmpty() -> {
                        Text(
                            text = "No hay condiciones cargadas en Supabase.",
                            color = Color.Gray
                        )
                    }

                    else -> {
                        restriccionesDisponibles.chunked(2).forEach { fila ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                fila.forEach { item ->
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = item in restriccionesSeleccionadas,
                                            onCheckedChange = { checked ->
                                                restriccionesSeleccionadas = if (checked) {
                                                    restriccionesSeleccionadas + item
                                                } else {
                                                    restriccionesSeleccionadas - item
                                                }
                                            }
                                        )

                                        Text(
                                            text = item,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                if (fila.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Objetivo y actividad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Meta",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowChips(
                    opciones = metas,
                    seleccionada = meta,
                    onSeleccionar = { meta = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Actividad",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowChips(
                    opciones = actividades,
                    seleccionada = actividad,
                    onSeleccionar = { actividad = it }
                )
            }
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = Color(0xFF991B1B),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val edadDesdeFecha = calcularEdadDesdeFecha(fechaNacimiento)
                val edadInt = edadDesdeFecha ?: edad.toIntOrNull()
                val alturaInt = altura.toIntOrNull()
                val pesoDouble = peso.toDoubleOrNull()

                error = when {
                    nombre.isBlank() || apellido.isBlank() || fechaNacimiento.isBlank() ->
                        "Completá nombre, apellido y fecha de nacimiento."

                    edadInt == null || edadInt !in 10..90 ->
                        "Revisá la fecha de nacimiento o la edad."

                    alturaInt == null || alturaInt !in 120..230 ->
                        "La altura debe estar entre 120 y 230 cm."

                    pesoDouble == null || pesoDouble < 30 || pesoDouble > 250 ->
                        "El peso debe estar entre 30 y 250 kg."

                    else -> ""
                }

                if (error.isBlank()) {
                    val perfilFinal = UsuarioPerfil(
                        nombre = nombre,
                        apellido = apellido,
                        fechaNacimiento = fechaNacimiento,
                        edad = edadInt ?: 18,
                        alturaCm = alturaInt ?: 170,
                        pesoKg = pesoDouble ?: 70.0,
                        genero = genero,
                        restricciones = restriccionesSeleccionadas,
                        meta = meta,
                        actividad = actividad
                    )

                    if ((edadInt ?: 18) < 16) {
                        perfilPendienteMenor = perfilFinal
                        mostrarAvisoMenor = true
                    } else {
                        onContinuar(perfilFinal)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
        ) {
            Text(textoBoton)
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}



@Composable
fun IndicadorCargaVerde(
    modifier: Modifier = Modifier,
    size: Int = 46
) {
    val transition = rememberInfiniteTransition(label = "spinnerVerde")
    val rotacion by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 950,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotacionSpinner"
    )

    Box(
        modifier = modifier
            .size(size.dp)
            .rotate(rotacion),
        contentAlignment = Alignment.Center
    ) {
        val radio = (size / 2.7f).dp

        repeat(8) { index ->
            val alpha = 0.25f + (index * 0.09f)

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .rotate(index * 45f)
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(7.dp)
                        .alpha(alpha.coerceIn(0.25f, 1f)),
                    shape = CircleShape,
                    color = VerdePrincipal
                ) {}
            }

            Spacer(modifier = Modifier.size(radio))
        }
    }
}

@Composable
fun AvatarUsuario(userPhotoUrl: String?) {
    var imagen by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(userPhotoUrl) {
        imagen = null

        if (!userPhotoUrl.isNullOrBlank()) {
            try {
                imagen = withContext(Dispatchers.IO) {
                    val connection = URL(userPhotoUrl).openConnection() as HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000

                    try {
                        BitmapFactory.decodeStream(connection.inputStream)
                    } finally {
                        connection.disconnect()
                    }
                }
            } catch (e: Exception) {
                imagen = null
            }
        }
    }

    Surface(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.55f), CircleShape),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.18f)
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen!!.asImageBitmap(),
                contentDescription = "Foto de cuenta",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Cuenta",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PantallaHomeProfesional(
    usuario: UsuarioPerfil,
    userPhotoUrl: String?,
    bienvenidaPendiente: Boolean,
    onConsumirBienvenida: () -> Unit,
    onIrPlan: () -> Unit,
    onIrInforme: () -> Unit,
    onIrAyuda: () -> Unit,
    onIrPerfil: () -> Unit,
    onIrChat: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "local"
    val resultado = calcularPlanNutricional(usuario)

    var estadoRacha by remember { mutableStateOf(cargarEstadoRacha(context, uid)) }
    var progresoSemanal by remember { mutableStateOf(cargarProgresoSemanalLocal(context, uid)) }
    val etiquetasSemana = remember { cargarEtiquetasSemanaDispositivo() }
    val progresoInforme = progresoSemanal.lastOrNull() ?: 0f
    val rachaActiva = estadoRacha.activa

    var aguaTextoHome by remember { mutableStateOf(textoAguaLocal(context, uid, resultado.vasosMeta)) }
    var mostrarDialogWidget by remember { mutableStateOf(false) }

    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var datosVisibles by remember { mutableStateOf(true) }
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarDialogRacha by remember { mutableStateOf(false) }
    var modoOscuro by remember { mutableStateOf(cargarModoOscuro(context)) }

    val fondoPantalla = if (modoOscuro) Color(0xFF071827) else FondoVerdeClaro
    val textoPrincipal = if (modoOscuro) Color.White else VerdeMuyOscuro
    val tarjeta = if (modoOscuro) Color(0xFF0F2A3D) else Color.White
    val tarjetaSuave = if (modoOscuro) Color(0xFF102F46) else Color(0xFFFFF7ED)
    val textoSuave = if (modoOscuro) Color(0xFFBAE6FD) else VerdeOscuro

    LaunchedEffect(Unit) {
        modoOscuro = cargarModoOscuro(context)
        KekeFitVisualState.modoOscuro = modoOscuro
    }

    LaunchedEffect(bienvenidaPendiente) {
        if (bienvenidaPendiente) {
            onConsumirBienvenida()
        }
    }

    LaunchedEffect(uid, usuario.nombre, resultado.calorias) {
        val rachaActual = cargarEstadoRacha(context, uid)
        estadoRacha = rachaActual
        progresoSemanal = cargarProgresoSemanalLocal(context, uid)
        aguaTextoHome = textoAguaLocal(context, uid, resultado.vasosMeta)
        guardarUidWidget(context, uid, usuario, resultado, rachaActual)
        actualizarWidgetsKekeFit(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoPantalla)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        if (modoOscuro) listOf(Color(0xFF020617), VerdeMuyOscuro, VerdeOscuro)
                        else listOf(VerdeMuyOscuro, VerdeOscuro, VerdePrincipal)
                    )
                )
                .padding(start = 18.dp, end = 18.dp, top = 42.dp, bottom = 18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (bienvenidaPendiente) {
                                    "¡Bienvenido! ${usuario.nombre}"
                                } else {
                                    "Hola, ${usuario.nombre.lowercase().replaceFirstChar { it.uppercase() }}"
                                },
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(10.dp))
                            AvatarUsuario(userPhotoUrl = userPhotoUrl)
                        }

                        Text(
                            text = "Seguimos construyendo tus hábitos",
                            color = VerdeTextoSuave,
                            fontSize = 15.sp
                        )
                    }

                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { reproducirClickSuave(context); mostrarDialogRacha = true },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.16f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    IconoFuegoRachaAnimado(
                                        activa = rachaActiva,
                                        sizeSp = 22
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { reproducirClickSuave(context); menuExpandido = true },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.16f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "☰",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false },
                            containerColor = if (modoOscuro) Color(0xEE0F172A) else Color.White.copy(alpha = 0.94f),
                            tonalElevation = 10.dp,
                            shadowElevation = 10.dp
                        ) {
                            DropdownMenuItem(
                                text = { Text("Datos", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    datosVisibles = true
                                    menuExpandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Racha", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    estadoRacha = cargarEstadoRacha(context, uid)
                                    progresoSemanal = cargarProgresoSemanalLocal(context, uid)
                                    mostrarDialogRacha = true
                                    menuExpandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Informe diario", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    menuExpandido = false
                                    onIrInforme()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (modoOscuro) "Modo claro" else "Modo oscuro", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    modoOscuro = !modoOscuro
                                    guardarModoOscuro(context, modoOscuro)
                                    menuExpandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Widget para celu", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    guardarUidWidget(context, uid, usuario, resultado, estadoRacha)
                                    actualizarWidgetsKekeFit(context)
                                    pedirAgregarWidgetKekeFit(context)
                                    mostrarDialogWidget = true
                                    menuExpandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Chatbot", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    menuExpandido = false
                                    onIrChat()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Feedback", color = textoPrincipal) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    menuExpandido = false
                                    onIrAyuda()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444)) },
                                text = { Text("Cerrar sesión", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    reproducirClickSuave(context)
                                    menuExpandido = false
                                    onCerrarSesion()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TarjetaRachaHero(
                    diasRacha = estadoRacha.dias,
                    progresoInforme = progresoInforme,
                    rachaActiva = rachaActiva,
                    progresoSemanal = progresoSemanal,
                    etiquetasSemana = etiquetasSemana,
                    onVerDetalle = { mostrarDialogRacha = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Datos rápidos",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    TextButton(onClick = { reproducirClickSuave(context); datosVisibles = !datosVisibles }) {
                        Text(
                            text = if (datosVisibles) "Ocultar" else "Mostrar",
                            color = Color.White
                        )
                    }
                }

                AnimatedVisibility(visible = datosVisibles) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.14f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MiniDatoCompacto(
                                modifier = Modifier.weight(1f),
                                titulo = "Género",
                                valor = usuario.genero,
                                icono = Icons.Default.Person
                            )
                            MiniDatoCompacto(
                                modifier = Modifier.weight(1f),
                                titulo = "Meta",
                                valor = usuario.meta,
                                icono = Icons.Default.Star
                            )
                            MiniDatoCompacto(
                                modifier = Modifier.weight(1f),
                                titulo = "Agua",
                                valor = aguaTextoHome,
                                icono = Icons.Default.Favorite
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen de hoy",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TarjetaMetricaCompacta(
                    modifier = Modifier.weight(1f),
                    titulo = "Calorías",
                    valor = "${resultado.calorias} kcal",
                    icono = Icons.Default.Favorite
                )
                TarjetaMetricaCompacta(
                    modifier = Modifier.weight(1f),
                    titulo = "IMC",
                    valor = resultado.imc,
                    subtitulo = resultado.estadoImc,
                    icono = Icons.Default.Info
                )
                TarjetaMetricaCompacta(
                    modifier = Modifier.weight(1f),
                    titulo = "Proteínas",
                    valor = "${resultado.proteinas} g",
                    icono = Icons.Default.Star
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = tarjetaSuave),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aviso médico: Keke Fit es una herramienta de apoyo. Ante dudas de salud, consultá con un nutricionista o profesional médico.",
                    color = if (modoOscuro) Color(0xFFFFD7BA) else Color(0xFF9A3412),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Accesos rápidos",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BotonHomeAccion(
                    modifier = Modifier.weight(1f),
                    texto = "Plan comidas",
                    icono = Icons.Default.Home,
                    relleno = true,
                    onClick = onIrPlan
                )
                BotonHomeAccion(
                    modifier = Modifier.weight(1f),
                    texto = "Editar perfil",
                    icono = Icons.Default.Edit,
                    onClick = onIrPerfil
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BotonHomeAccion(
                    modifier = Modifier.weight(1f),
                    texto = "Informe",
                    icono = Icons.Default.Star,
                    onClick = onIrInforme
                )
                BotonHomeAccion(
                    modifier = Modifier.weight(1f),
                    texto = "Ayuda",
                    icono = Icons.Default.Info,
                    onClick = onIrAyuda
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onIrChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
            ) {
                Icon(Icons.Default.Info, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hablar con KekeBot")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    val emailActual = auth.currentUser?.email

                    if (emailActual.isNullOrBlank()) {
                        error = "No se encontró un correo para restablecer la contraseña."
                        mensaje = ""
                    } else {
                        auth.sendPasswordResetEmail(emailActual)
                            .addOnSuccessListener {
                                mensaje = "Te enviamos un correo para restablecer la contraseña."
                                error = ""
                            }
                            .addOnFailureListener { e ->
                                error = e.localizedMessage ?: "No se pudo enviar el correo."
                                mensaje = ""
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Lock, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restablecer contraseña")
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (mensaje.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = mensaje, color = textoSuave)
            }

            if (error.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(18.dp))

            GraficoSeguimientoSemanal(
                valores = progresoSemanal,
                etiquetas = etiquetasSemana,
                recomendacion = resultado.recomendacion,
                modoOscuro = modoOscuro
            )

            Spacer(modifier = Modifier.height(22.dp))
        }
    }

    if (mostrarDialogWidget) {
        DialogoWidgetRacha(
            estadoRacha = estadoRacha,
            modoOscuro = modoOscuro,
            onAgregarWidget = {
                guardarUidWidget(context, uid, usuario, resultado, estadoRacha)
                actualizarWidgetsKekeFit(context)
                pedirAgregarWidgetKekeFit(context)
            },
            onDismiss = { mostrarDialogWidget = false }
        )
    }

    if (mostrarDialogRacha) {
        DialogoRachaCalendario(
            diasRacha = estadoRacha.dias,
            rachaActiva = rachaActiva,
            progresoSemanal = progresoSemanal,
            etiquetasSemana = etiquetasSemana,
            onDismiss = { mostrarDialogRacha = false }
        )
    }
}

@Composable
fun TarjetaRachaHero(
    diasRacha: Int,
    progresoInforme: Float,
    rachaActiva: Boolean,
    progresoSemanal: List<Float>,
    etiquetasSemana: List<String>,
    onVerDetalle: () -> Unit
) {
    val porcentaje = (progresoInforme.coerceIn(0f, 1f) * 100).roundToInt()

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.17f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerDetalle() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconoFuegoRachaAnimado(
                        activa = rachaActiva,
                        sizeSp = 30
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Racha actual",
                            color = VerdeTextoSuave,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (diasRacha == 1) "1 día seguido" else "$diasRacha días seguidos",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(68.dp)
                        .border(5.dp, VerdeTextoSuave.copy(alpha = 0.6f), CircleShape),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$porcentaje%",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Últimos 7 días del informe",
                color = VerdeTextoSuave,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val valores = if (progresoSemanal.size == 7) progresoSemanal else List(7) { 0f }
                val etiquetas = if (etiquetasSemana.size == 7) etiquetasSemana else listOf("L", "M", "M", "J", "V", "S", "D")

                valores.forEachIndexed { index, progreso ->
                    val completo = progreso >= 1f
                    val esHoy = index == 6
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            completo -> VerdeSecundario.copy(alpha = 0.42f)
                            progreso > 0f -> Color.White.copy(alpha = 0.24f)
                            else -> Color.White.copy(alpha = 0.10f)
                        },
                        border = if (esHoy) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)) else null
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (completo) "🔥" else etiquetas[index],
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            if (progreso > 0f && !completo) {
                                Text(
                                    text = "${(progreso * 100).roundToInt()}%",
                                    color = VerdeTextoSuave,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniDatoCompacto(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    icono: ImageVector
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = titulo,
            color = VerdeTextoSuave,
            fontSize = 11.sp
        )

        Text(
            text = valor,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
    }
}

@Composable
fun TarjetaMetricaCompacta(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    subtitulo: String = "",
    icono: ImageVector
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = tarjetaActual())
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = VerdeTextoSuave,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = VerdeOscuro,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = titulo,
                color = Color.Gray,
                fontSize = 11.sp
            )
            Text(
                text = valor,
                fontWeight = FontWeight.Bold,
                color = textoPrincipalActual(),
                fontSize = 16.sp
            )
            if (subtitulo.isNotBlank()) {
                Text(
                    text = subtitulo,
                    color = textoSecundarioActual(),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun BotonHomeAccion(
    modifier: Modifier = Modifier,
    texto: String,
    icono: ImageVector,
    relleno: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(62.dp)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (relleno) VerdePrincipal else VerdeSecundario.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (relleno) VerdePrincipal else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (relleno) Color.White else VerdeOscuro,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = texto,
                color = if (relleno) Color.White else VerdeOscuro,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun TarjetaWidgetRacha(
    estadoRacha: EstadoRacha,
    modoOscuro: Boolean,
    onClick: () -> Unit
) {
    val fondo = if (modoOscuro) Color(0xFF020617) else VerdeMuyOscuro
    val borde = if (estadoRacha.activa) VerdePrincipal else Color(0xFF64748B)
    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = fondo),
        border = androidx.compose.foundation.BorderStroke(1.dp, borde.copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.12f), modifier = Modifier.size(58.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (estadoRacha.activa) "🔥" else "🌙", fontSize = 30.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Widget de racha", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = if (estadoRacha.dias == 1) "1 día de racha" else "${estadoRacha.dias} días de racha",
                    color = VerdeSecundario,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(mensajeWidgetRacha(estadoRacha), color = VerdeTextoSuave, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun GraficoSeguimientoSemanal(
    valores: List<Float>,
    etiquetas: List<String>,
    recomendacion: String,
    modoOscuro: Boolean
) {
    val valoresSeguros = if (valores.size == 7) valores else List(7) { 0f }
    val etiquetasSeguras = if (etiquetas.size == 7) etiquetas else listOf("L", "M", "M", "J", "V", "S", "D")
    val tarjeta = if (modoOscuro) Color(0xFF0F2A3D) else Color.White
    val textoPrincipal = if (modoOscuro) Color.White else VerdeMuyOscuro
    val textoSecundario = if (modoOscuro) Color(0xFFBAE6FD) else VerdeOscuro
    val fondoNota = if (modoOscuro) Color(0xFF12364F) else FondoVerdeClaro

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tarjeta)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Gráfico de seguimiento",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Muestra cuánto completaste del informe en los últimos 7 días.",
                color = textoSecundario,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                valoresSeguros.forEachIndexed { index, valorOriginal ->
                    val valor = valorOriginal.coerceIn(0f, 1f)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "${(valor * 100).roundToInt()}%",
                            color = textoSecundario,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(((110f * valor).coerceAtLeast(10f)).dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(VerdePrincipal, VerdeSecundario)
                                    ),
                                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = etiquetasSeguras[index],
                            color = textoSecundario,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = fondoNota,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = recomendacion,
                    color = textoSecundario,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun DialogoWidgetRacha(
    estadoRacha: EstadoRacha,
    modoOscuro: Boolean,
    onAgregarWidget: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = if (modoOscuro) Color(0xFF020617) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Widget de racha",
                    color = if (modoOscuro) Color.White else VerdeMuyOscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ya agregué el widget real de Android. Tocá el botón de abajo para pedirle al celu que lo fije en la pantalla de inicio. Si tu launcher no lo permite, mantené apretada la pantalla de inicio > Widgets > Keke Fit.",
                    color = if (modoOscuro) VerdeTextoSuave else VerdeOscuro,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                TarjetaWidgetRacha(estadoRacha = estadoRacha, modoOscuro = true, onClick = {})
                Spacer(modifier = Modifier.height(12.dp))
                val frase = when {
                    estadoRacha.informeHoyEnviado -> "Hoy ya sumaste. Mañana volvé para mantener la cadena."
                    estadoRacha.activa -> "Vas bien, pero todavía falta el informe de hoy."
                    else -> "La racha está apagada. Completá el informe para volver a empezar."
                }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (modoOscuro) Color(0xFF0F2A3D) else FondoVerdeClaro),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = frase,
                        color = if (modoOscuro) Color.White else VerdeMuyOscuro,
                        modifier = Modifier.padding(14.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onAgregarWidget,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar widget al inicio", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Entendido")
                }
            }
        }
    }
}

@Composable
fun DialogoRachaCalendario(
    diasRacha: Int,
    rachaActiva: Boolean,
    progresoSemanal: List<Float>,
    etiquetasSemana: List<String>,
    onDismiss: () -> Unit
) {
    val valores = if (progresoSemanal.size == 7) progresoSemanal else List(7) { 0f }
    val etiquetas = if (etiquetasSemana.size == 7) etiquetasSemana else listOf("L", "M", "M", "J", "V", "S", "D")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaActual())
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Racha y progreso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = textoPrincipalActual()
                        )
                        Text(
                            text = if (rachaActiva) {
                                if (diasRacha == 1) "1 día seguido activo" else "$diasRacha días seguidos activos"
                            } else {
                                "Todavía no hay racha activa"
                            },
                            color = textoSecundarioActual(),
                            fontSize = 13.sp
                        )
                    }

                    IconoFuegoRachaAnimado(
                        activa = rachaActiva,
                        sizeSp = 30
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    valores.forEachIndexed { index, progreso ->
                        val completo = progreso >= 1f
                        val parcial = progreso > 0f && !completo
                        val esHoy = index == 6
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = when {
                                completo -> VerdeSecundario.copy(alpha = 0.35f)
                                parcial -> Color(0xFFE0F2FE)
                                else -> Color(0xFFF1F5F9)
                            },
                            border = if (esHoy) androidx.compose.foundation.BorderStroke(1.dp, VerdePrincipal) else null
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (completo) "🔥" else etiquetas[index],
                                    color = if (completo || parcial) VerdeOscuro else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (esHoy) "Hoy" else "${(progreso * 100).roundToInt()}%",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "La racha sube solo cuando enviás el informe completo del día. Si pasa la medianoche y no lo enviaste, se corta automáticamente al volver a abrir la app.",
                    color = textoSecundarioActual(),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

fun calcularPlanNutricional(usuario: UsuarioPerfil): ResultadoNutricional {
    val peso = usuario.pesoKg
    val altura = usuario.alturaCm.toDouble()
    val edad = usuario.edad

    val tmb = if (usuario.genero == "Femenino") {
        (10 * peso) + (6.25 * altura) - (5 * edad) - 161
    } else {
        (10 * peso) + (6.25 * altura) - (5 * edad) + 5
    }

    val factorActividad = when (usuario.actividad) {
        "Sedentaria" -> 1.2
        "Ligera" -> 1.375
        "Moderada" -> 1.55
        "Alta" -> 1.725
        else -> 1.2
    }

    var calorias = tmb * factorActividad

    calorias = when (usuario.meta) {
        "Bajar peso" -> calorias - 450
        "Subir peso" -> calorias + 350
        "Ganar músculo" -> calorias + 250
        else -> calorias
    }

    val aguaLitros = peso * 0.035
    val alturaM = altura / 100.0
    val imcValor = peso / (alturaM * alturaM)

    val estadoImc = when {
        imcValor < 18.5 -> "Bajo peso"
        imcValor < 25 -> "Saludable"
        imcValor < 30 -> "Sobrepeso"
        else -> "Obesidad"
    }

    val proteinas = when (usuario.meta) {
        "Bajar peso" -> (peso * 1.8).roundToInt()
        "Ganar músculo" -> (peso * 2.0).roundToInt()
        "Subir peso" -> (peso * 1.7).roundToInt()
        else -> (peso * 1.4).roundToInt()
    }

    val recomendacion = when (usuario.meta) {
        "Bajar peso" -> "Mantené un déficit moderado y priorizá proteína."
        "Subir peso" -> "Buscá un superávit controlado con alimentos de calidad."
        "Ganar músculo" -> "Combiná fuerza, proteína suficiente y descanso."
        "Mejorar hábitos" -> "Enfocate en constancia, hidratación y regularidad."
        else -> "Mantené una alimentación equilibrada."
    }

    return ResultadoNutricional(
        calorias = calorias.roundToInt(),
        aguaTexto = "${(aguaLitros / 0.25).roundToInt()} vasos",
        vasosMeta = (aguaLitros / 0.25).roundToInt().coerceAtLeast(4),
        imc = "%.1f".format(imcValor),
        estadoImc = estadoImc,
        proteinas = proteinas,
        recomendacion = recomendacion
    )
}

fun normalizarRestriccion(texto: String): String {
    return texto.lowercase(Locale.ROOT)
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")
        .replace("_", " ")
        .replace("-", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

fun colorPorTipoComida(tipo: String): Color {
    return when (normalizarRestriccion(tipo)) {
        "desayuno" -> Color(0xFFDCFCE7)
        "almuerzo" -> Color(0xFF86EFAC)
        "merienda" -> Color(0xFF4ADE80)
        "cena" -> Color(0xFF15803D)
        else -> Color(0xFFE5E7EB)
    }
}

fun colorTextoPorTipoComida(tipo: String): Color {
    return when (normalizarRestriccion(tipo)) {
        "cena" -> Color.White
        else -> VerdeMuyOscuro
    }
}

fun tituloTipoComida(tipo: String): String {
    return when (normalizarRestriccion(tipo)) {
        "desayuno" -> "Desayuno"
        "almuerzo" -> "Almuerzo"
        "merienda" -> "Merienda"
        "cena" -> "Cena"
        else -> tipo.replaceFirstChar { it.uppercase() }
    }
}

fun horarioRecomendadoPorTipo(tipo: String): String {
    return when (normalizarRestriccion(tipo)) {
        "desayuno" -> "07:00 a 10:00"
        "almuerzo" -> "12:00 a 15:00"
        "merienda" -> "16:00 a 18:30"
        "cena" -> "20:00 a 22:30"
        else -> "Horario flexible"
    }
}

fun generarPromptAgenteRotacionKekeFit(
    usuario: UsuarioPerfil,
    resultado: ResultadoNutricional,
    comidas: List<ComidaPlan>
): String {
    val restricciones = usuario.restricciones.joinToString(", ").ifBlank { "sin restricciones declaradas" }
    val comidasResumen = comidas.joinToString(" | ") { comida ->
        "${comida.id}:${comida.tipo}:${comida.nombre}:${comida.calorias}kcal:P${comida.proteinas}:C${comida.carbohidratos}:G${comida.grasas}"
    }

    return """
        Actuá como agente nutricional de KekeFit.
        Tenés acceso a la base de comidas ya leída por la app.
        Usuario: edad=${usuario.edad}, altura=${usuario.alturaCm}cm, peso=${usuario.pesoKg}kg, género=${usuario.genero}, meta=${usuario.meta}, actividad=${usuario.actividad}.
        Restricciones/alergias: $restricciones.
        Objetivo diario: ${resultado.calorias} kcal, proteínas: ${resultado.proteinas} g.
        Generá una rotación semanal de desayuno, almuerzo, merienda y cena evitando repetir demasiado, respetando restricciones, calorías y balance de macros.
        Comidas disponibles: $comidasResumen
    """.trimIndent()
}

fun generarPlanSemanalInteligenteKekeFit(
    usuario: UsuarioPerfil,
    resultado: ResultadoNutricional,
    comidas: List<ComidaPlan>
): Map<Pair<Int, String>, ComidaPlan> {
    if (comidas.isEmpty()) return emptyMap()

    val promptAgente = generarPromptAgenteRotacionKekeFit(usuario, resultado, comidas)
    Log.d("KEKE_AGENT_ROTACION", promptAgente.take(3000))

    val restricciones = usuario.restricciones.map { normalizarRestriccion(it) }.filter { it.isNotBlank() }
    val tipos = listOf("desayuno", "almuerzo", "merienda", "cena")
    val objetivoPorTipo = mapOf(
        "desayuno" to (resultado.calorias * 0.22),
        "almuerzo" to (resultado.calorias * 0.34),
        "merienda" to (resultado.calorias * 0.16),
        "cena" to (resultado.calorias * 0.28)
    )

    fun comidaEsCompatible(comida: ComidaPlan): Boolean {
        val texto = (comida.restricciones + comida.ingredientes + listOf(comida.nombre, comida.tipo))
            .joinToString(" ") { normalizarRestriccion(it) }

        // Si la comida declara "sin X" o "apto X", se considera compatible.
        // Solo se evita cuando queda claro que contiene algo prohibido.
        return restricciones.all { restriccion ->
            val declaradaApta = texto.contains("sin $restriccion") || texto.contains("apto $restriccion")
            val declaradaProhibida = texto.contains("contiene $restriccion") || texto.contains("con $restriccion")
            declaradaApta || !declaradaProhibida
        }
    }

    fun puntuar(tipo: String, comida: ComidaPlan, dia: Int, usadasTipo: Set<String>): Double {
        val objetivo = objetivoPorTipo[tipo] ?: (resultado.calorias / 4.0)
        val distanciaCalorias = kotlin.math.abs(comida.calorias - objetivo) / objetivo.coerceAtLeast(1.0)
        val bonusProteina = comida.proteinas / resultado.proteinas.coerceAtLeast(1).toDouble()
        val penalizacionRepeticion = if (comida.nombre in usadasTipo) 0.45 else 0.0
        val variacionDia = ((comida.id + dia * 17 + tipo.length * 5) % 11) / 100.0
        return 1.0 - distanciaCalorias + bonusProteina - penalizacionRepeticion + variacionDia
    }

    val resultadoPlan = linkedMapOf<Pair<Int, String>, ComidaPlan>()
    val usadasPorTipo = tipos.associateWith { mutableSetOf<String>() }

    for (dia in 0..6) {
        for (tipo in tipos) {
            val candidatasBase = comidas.filter { normalizarRestriccion(it.tipo) == tipo }
            val candidatasCompatibles = candidatasBase.filter { comidaEsCompatible(it) }
            val candidatas = candidatasCompatibles.ifEmpty { candidatasBase }.ifEmpty { comidas }
            val usadas = usadasPorTipo[tipo].orEmpty()
            val elegida = candidatas.maxByOrNull { comida -> puntuar(tipo, comida, dia, usadas) }
            if (elegida != null) {
                resultadoPlan[dia to tipo] = elegida
                usadasPorTipo[tipo]?.add(elegida.nombre)
                if ((usadasPorTipo[tipo]?.size ?: 0) >= candidatas.size.coerceAtLeast(1)) {
                    usadasPorTipo[tipo]?.clear()
                }
            }
        }
    }

    return resultadoPlan
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TarjetaComidaDelDia(
    tipo: String,
    comida: ComidaPlan?
) {
    val modoOscuro = modoOscuroActual()
    val colorFondo = if (modoOscuro) {
        when (normalizarRestriccion(tipo)) {
            "desayuno" -> Color(0xFF0F2A3D)
            "almuerzo" -> Color(0xFF12384F)
            "merienda" -> Color(0xFF14506A)
            "cena" -> Color(0xFF0B3A2A)
            else -> Color(0xFF111827)
        }
    } else {
        colorPorTipoComida(tipo)
    }
    val colorTexto = if (modoOscuro) Color.White else colorTextoPorTipoComida(tipo)
    var mostrarIngredientes by remember { mutableStateOf(false) }
    var ingredientesNoGustan by remember { mutableStateOf<Set<String>>(emptySet()) }

    if (mostrarIngredientes && comida != null) {
        Dialog(onDismissRequest = { mostrarIngredientes = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = tarjetaActual()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = comida.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textoPrincipalActual()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Marcá los ingredientes principales que no te gustan.",
                        color = textoSecundarioActual(),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val ingredientes = comida.ingredientes.ifEmpty {
                        listOf(comida.nombre, comida.tipo) + comida.restricciones
                    }

                    ingredientes.distinct().forEach { ingrediente ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = ingrediente in ingredientesNoGustan,
                                onCheckedChange = { checked ->
                                    ingredientesNoGustan = if (checked) {
                                        ingredientesNoGustan + ingrediente
                                    } else {
                                        ingredientesNoGustan - ingrediente
                                    }
                                }
                            )

                            Text(
                                text = ingrediente.replaceFirstChar { it.uppercase() },
                                color = textoPrincipalActual()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { mostrarIngredientes = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (comida != null) mostrarIngredientes = true
                }
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tituloTipoComida(tipo),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorTexto
            )

            Text(
                text = "Horario recomendado: ${horarioRecomendadoPorTipo(tipo)}",
                color = colorTexto.copy(alpha = 0.85f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (comida == null) {
                Text(
                    text = "No hay opciones disponibles para este tipo de comida.",
                    color = colorTexto.copy(alpha = 0.8f)
                )
            } else {
                Text(
                    text = comida.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorTexto
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${comida.calorias} kcal",
                    color = colorTexto
                )

                Text(
                    text = "Proteínas: ${comida.proteinas} g | Carbohidratos: ${comida.carbohidratos} g | Grasas: ${comida.grasas} g",
                    color = colorTexto.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Mantené presionado para ver ingredientes.",
                    color = colorTexto.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPlanComidas(
    usuario: UsuarioPerfil,
    onVolver: () -> Unit
) {
    val resultado = calcularPlanNutricional(usuario)
    val hoyIndex = indiceDiaSemanaActual()
    val hoyNombre = nombreDiaSemanaActual()

    var todasLasComidas by remember { mutableStateOf<List<ComidaPlan>>(emptyList()) }
    var planSupabase by remember { mutableStateOf<List<PlanComidaSupabase>>(emptyList()) }
    var cargandoComidas by remember { mutableStateOf(true) }
    var errorComidas by remember { mutableStateOf("") }
    var diaAbierto by remember { mutableStateOf(hoyNombre) }

    LaunchedEffect(Unit) {
        try {
            todasLasComidas = obtenerComidasDesdeSupabase()
            planSupabase = obtenerPlanDesdeSupabase()
            errorComidas = ""
        } catch (e: Exception) {
            todasLasComidas = emptyList()
            errorComidas = mensajeRedKekeFit(e)
        } finally {
            cargandoComidas = false
        }
    }

    val restriccionesUsuarioValidas = usuario.restricciones.map { normalizarRestriccion(it) }.toSet()

    val comidasFiltradas = if (restriccionesUsuarioValidas.isEmpty()) {
        todasLasComidas
    } else {
        todasLasComidas.filter { comida ->
            val textoComida = (comida.restricciones + comida.ingredientes + listOf(comida.nombre, comida.tipo))
                .joinToString(" ") { normalizarRestriccion(it) }
            restriccionesUsuarioValidas.all { restriccion -> textoComida.contains(restriccion) }
        }.ifEmpty { todasLasComidas }
    }

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val tiposComida = listOf("desayuno", "almuerzo", "merienda", "cena")
    val comidasPorTipo = tiposComida.associateWith { tipo -> comidasFiltradas.filter { normalizarRestriccion(it.tipo) == tipo } }
    val comidasPorId = todasLasComidas.associateBy { it.id }
    val planHoy = planSupabase.filter { it.fecha == fechaHoyDispositivo() }
    val planBase = if (planHoy.isNotEmpty()) planHoy else planSupabase
    val usuarioPlanElegido = planBase.groupBy { it.idUsuario }.maxByOrNull { it.value.size }?.key ?: 0
    val planPorTipo = planBase
        .filter { usuarioPlanElegido == 0 || it.idUsuario == usuarioPlanElegido }
        .associateBy { normalizarRestriccion(it.tipoComida) }
    val planInteligente = remember(comidasFiltradas, usuario, resultado) {
        generarPlanSemanalInteligenteKekeFit(usuario, resultado, comidasFiltradas)
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Plan de comidas") }) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(fondoPantallaActual())
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = tarjetaActual()),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Objetivo calórico", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textoPrincipalActual())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${resultado.calorias} kcal por día", color = textoSecundarioActual())
                        Text("Proteínas: ${resultado.proteinas} g", color = textoSecundarioActual())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    cargandoComidas -> Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { IndicadorCargaVerde() }
                    errorComidas.isNotBlank() -> Card(colors = CardDefaults.cardColors(containerColor = tarjetaSuaveActual()), modifier = Modifier.fillMaxWidth()) {
                        Text(errorComidas, color = textoPrincipalActual(), modifier = Modifier.padding(14.dp))
                    }
                    todasLasComidas.isEmpty() -> Card(colors = CardDefaults.cardColors(containerColor = tarjetaActual()), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("No se recibieron comidas desde Supabase. Si en Table Editor sí hay filas, revisá RLS: la tabla comida2 necesita una policy SELECT para anon/authenticated o la app recibe una lista vacía.", modifier = Modifier.padding(16.dp), color = textoSecundarioActual())
                    }
                    comidasFiltradas.isEmpty() -> Card(colors = CardDefaults.cardColors(containerColor = tarjetaActual()), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("No hay comidas disponibles para las restricciones seleccionadas.", modifier = Modifier.padding(16.dp), color = textoSecundarioActual())
                    }
                    else -> {
                        Text("Plan semanal", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = textoPrincipalActual())
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hoy es $hoyNombre. Se despliega automáticamente el día actual y el plan se renueva con la fecha del dispositivo.", color = textoSecundarioActual(), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Rotación inteligente activa: el agente lee las comidas cargadas, tus restricciones, datos y objetivo para armar la semana.", color = textoSecundarioActual(), fontSize = 13.sp)
                        if (planSupabase.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Plan2 detectado en Supabase: se prioriza para el día actual cuando coincide el tipo de comida.", color = textoSecundarioActual(), fontSize = 13.sp)
                        }
                        if (hoyIndex == 6) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(colors = CardDefaults.cardColors(containerColor = tarjetaSuaveActual()), shape = RoundedCornerShape(18.dp)) {
                                Text("Plan finalizado: esperá hasta el lunes para iniciar una nueva semana.", color = textoPrincipalActual(), modifier = Modifier.padding(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        diasSemana.forEachIndexed { diaIndex, dia ->
                            val abierto = diaAbierto == dia
                            val esHoy = diaIndex == hoyIndex
                            val colorTarjeta by animateColorAsState(
                                targetValue = if (modoOscuroActual()) {
                                    if (abierto) Color(0xFF0F2A3D) else Color(0xFF102F46)
                                } else {
                                    if (abierto) Color.White else Color(0xFFE0F2FE)
                                },
                                label = "colorTarjetaDia"
                            )
                            val elevacionTarjeta by animateDpAsState(targetValue = if (abierto) 8.dp else 2.dp, label = "elevacionTarjetaDia")

                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                                elevation = CardDefaults.cardElevation(defaultElevation = elevacionTarjeta),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).animateContentSize(animationSpec = tween(420, easing = FastOutSlowInEasing))
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { diaAbierto = if (abierto) "" else dia }.padding(18.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (esHoy) {
                                                Surface(shape = CircleShape, color = Color(0xFF22C55E), modifier = Modifier.size(12.dp)) {}
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }
                                            Column {
                                                Text(if (esHoy) "$dia · HOY" else dia, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textoPrincipalActual())
                                                Text(if (abierto) "Plan desplegado" else "Tocar para ver comidas", color = textoSecundarioActual(), fontSize = 13.sp)
                                            }
                                        }
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = VerdePrincipal)
                                    }

                                    AnimatedVisibility(
                                        visible = abierto,
                                        enter = fadeIn(tween(280)) + expandVertically(tween(420, easing = FastOutSlowInEasing)),
                                        exit = fadeOut(tween(220)) + shrinkVertically(tween(360, easing = FastOutSlowInEasing))
                                    ) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                                            tiposComida.forEachIndexed { tipoIndex, tipo ->
                                                val opciones = comidasPorTipo[tipo].orEmpty()
                                                val comidaDesdePlan = if (diaIndex == hoyIndex) planPorTipo[normalizarRestriccion(tipo)]?.let { comidasPorId[it.idComida] } else null
                                                val comidaDelAgente = planInteligente[diaIndex to tipo]
                                                val comida = comidaDesdePlan ?: comidaDelAgente ?: opciones.getOrNull((diaIndex + tipoIndex + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) % opciones.size.coerceAtLeast(1))
                                                TarjetaComidaDelDia(tipo = tipo, comida = comida)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
            BotonVolverFlotante(onVolver)
        }
    }
}

@Composable
fun TarjetaRachaInforme(
    diasRacha: Int,
    progresoInforme: Float
) {
    val porcentaje = (progresoInforme.coerceIn(0f, 1f) * 100).roundToInt()

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Racha activa",
                    color = Color.Gray
                )

                Text(
                    text = "$diasRacha días seguidos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = textoPrincipalActual()
                )

                Text(
                    text = "Seguimiento del informe mensual",
                    color = textoSecundarioActual(),
                    fontSize = 13.sp
                )
            }

            Surface(
                modifier = Modifier
                    .size(74.dp)
                    .border(6.dp, VerdeSecundario.copy(alpha = 0.35f), CircleShape),
                shape = CircleShape,
                color = FondoVerdeClaro
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$porcentaje%",
                        color = textoPrincipalActual(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInformeMensual(
    usuario: UsuarioPerfil,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "local"
    val fechaHoy = fechaHoyDispositivo()
    val hoyIndex = indiceDiaSemanaActual()
    val resultado = calcularPlanNutricional(usuario)

    var vasosConsumidos by remember { mutableStateOf(cargarVasosConsumidosLocal(context, uid, fechaHoy)) }
    var vasosMeta by remember { mutableStateOf(cargarMetaVasosLocal(context, uid, resultado.vasosMeta, fechaHoy)) }

    var todasLasComidas by remember { mutableStateOf<List<ComidaPlan>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var comidasCumplidas by remember { mutableStateOf(cargarCumplidasLocal(context, uid, fechaHoy)) }
    var comidasReales by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var estadoRacha by remember { mutableStateOf(cargarEstadoRacha(context, uid)) }
    var diaVisible by remember { mutableStateOf(hoyIndex + 1) }

    LaunchedEffect(Unit) {
        try {
            todasLasComidas = obtenerComidasDesdeSupabase()
            error = ""
        } catch (e: Exception) {
            error = mensajeRedKekeFit(e)
        } finally {
            cargando = false
        }
    }

    val tiposComida = listOf("desayuno", "almuerzo", "merienda", "cena")
    val registros = remember(todasLasComidas) {
        val porTipo = tiposComida.associateWith { tipo -> todasLasComidas.filter { normalizarRestriccion(it.tipo) == tipo } }
        (1..7).flatMap { dia ->
            tiposComida.mapIndexed { index, tipo ->
                val opciones = porTipo[tipo].orEmpty()
                val recomendada = if (opciones.isNotEmpty()) opciones[(dia + index + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) % opciones.size].nombre else "Sin comida cargada"
                RegistroComidaMensual(dia = dia, tipo = tipo, comidaRecomendada = recomendada)
            }
        }
    }

    val registrosHoy = registros.filter { it.dia == hoyIndex + 1 }
    val totalHoy = registrosHoy.size.coerceAtLeast(1)
    val progresoHoy = registrosHoy.count { "${it.dia}-${it.tipo}" in comidasCumplidas }.toFloat() / totalHoy.toFloat()
    val porcentaje = (progresoHoy * 100).roundToInt()
    val puedeEnviar = registrosHoy.isNotEmpty() && registrosHoy.all { "${it.dia}-${it.tipo}" in comidasCumplidas }

    Scaffold(topBar = { TopAppBar(title = { Text("Informe diario") }) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(fondoPantallaActual())
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = tarjetaActual()), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Informe de hoy", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textoPrincipalActual())
                            Text(if (estadoRacha.informeHoyEnviado) "Ya mandaste el informe. Volvé mañana para mantener tu racha." else "Completalo antes de las 12 de la noche para no perder la racha.", color = textoSecundarioActual())
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Racha: ${estadoRacha.dias} días", color = VerdePrincipal, fontWeight = FontWeight.Bold)
                        }
                        Surface(modifier = Modifier.size(76.dp).border(6.dp, VerdeSecundario.copy(alpha = 0.35f), CircleShape), shape = CircleShape, color = Color.White) {
                            Box(contentAlignment = Alignment.Center) { Text("$porcentaje%", fontWeight = FontWeight.Bold, color = textoPrincipalActual()) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = tarjetaActual()), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Agua de hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textoPrincipalActual())
                        Text("Marcá los vasos que tomaste. También queda guardado localmente para el seguimiento.", color = textoSecundarioActual(), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = {
                                reproducirClickSuave(context)
                                vasosConsumidos = (vasosConsumidos - 1).coerceAtLeast(0)
                                guardarAguaLocal(context, uid, vasosConsumidos, vasosMeta, fechaHoy)
                            }) { Text("-") }
                            Text("$vasosConsumidos / $vasosMeta vasos", color = textoPrincipalActual(), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Button(onClick = {
                                reproducirClickSuave(context)
                                vasosConsumidos = (vasosConsumidos + 1).coerceAtMost(30)
                                guardarAguaLocal(context, uid, vasosConsumidos, vasosMeta, fechaHoy)
                            }, colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)) { Text("+") }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vasosMeta.toString(),
                            onValueChange = { nuevo ->
                                val meta = nuevo.filter { it.isDigit() }.toIntOrNull() ?: vasosMeta
                                vasosMeta = meta.coerceIn(1, 30)
                                guardarAguaLocal(context, uid, vasosConsumidos, vasosMeta, fechaHoy)
                            },
                            label = { Text("Meta de vasos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            reproducirClickSuave(context)
                            crearPdfInforme(context, uid, porcentaje, comidasCumplidas, comidasReales)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                    ) { Text("Convertir a PDF") }

                    Button(
                        onClick = {
                            reproducirClickSuave(context)
                            guardarInformeLocal(context, uid, fechaHoy, comidasCumplidas, comidasReales)
                            guardarAguaLocal(context, uid, vasosConsumidos, vasosMeta, fechaHoy)
                            val nuevaRacha = registrarInformeHoy(context, uid)
                            estadoRacha = nuevaRacha
                            guardarUidWidget(context, uid, usuario, resultado, nuevaRacha)
                            actualizarWidgetsKekeFit(context)
                            Toast.makeText(context, "Informe enviado. Racha y widget actualizados.", Toast.LENGTH_SHORT).show()
                        },
                        enabled = puedeEnviar && !estadoRacha.informeHoyEnviado,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                    ) { Text(if (estadoRacha.informeHoyEnviado) "Enviado" else "Enviar informe") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    (1..7).forEach { dia ->
                        val esHoy = dia == hoyIndex + 1
                        FilterChip(
                            selected = diaVisible == dia,
                            onClick = { diaVisible = dia },
                            label = { Text(if (esHoy) "HOY" else "D$dia") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (esHoy) Color(0xFFBBF7D0) else VerdeTextoSuave,
                                selectedLabelColor = VerdeMuyOscuro
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    cargando -> Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { IndicadorCargaVerde() }
                    error.isNotBlank() -> Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)), modifier = Modifier.fillMaxWidth()) { Text(error, color = textoPrincipalActual(), modifier = Modifier.padding(14.dp)) }
                    else -> {
                        val comidasDelDia = registros.filter { it.dia == diaVisible }
                        val tituloDia = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo").getOrElse(diaVisible - 1) { "Día $diaVisible" }
                        Text(if (diaVisible == hoyIndex + 1) "$tituloDia · HOY" else tituloDia, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textoPrincipalActual(), modifier = Modifier.padding(top = 8.dp, bottom = 6.dp))

                        comidasDelDia.forEach { registro ->
                            val key = "${registro.dia}-${registro.tipo}"
                            val cumplida = key in comidasCumplidas
                            val comidaReal = comidasReales[key].orEmpty()
                            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = tarjetaActual()), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = cumplida,
                                            onCheckedChange = { checked ->
                                                comidasCumplidas = if (checked) comidasCumplidas + key else comidasCumplidas - key
                                                guardarInformeLocal(context, uid, fechaHoy, comidasCumplidas, comidasReales)
                                            }
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tituloTipoComida(registro.tipo), fontWeight = FontWeight.Bold, color = textoPrincipalActual())
                                            Text(registro.comidaRecomendada, color = textoSecundarioActual(), fontSize = 13.sp)
                                        }
                                        if (cumplida) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E))
                                    }
                                    if (!cumplida) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = comidaReal,
                                            onValueChange = { nuevoTexto ->
                                                comidasReales = comidasReales + (key to nuevoTexto)
                                                guardarInformeLocal(context, uid, fechaHoy, comidasCumplidas, comidasReales + (key to nuevoTexto))
                                            },
                                            label = { Text("Si comiste otra cosa, escribila acá") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
            BotonVolverFlotante(onVolver)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAyudaFeedback(
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    var feedback by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ayuda y feedback") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(fondoPantallaActual())
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = tarjetaActual()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "¿Cómo usar Keke Fit?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = textoPrincipalActual()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("1. Completá tu perfil para calcular tus datos.", color = textoSecundarioActual())
                    Text("2. Entrá al plan de comidas para ver tus recomendaciones.", color = textoSecundarioActual())
                    Text("3. Usá el informe para marcar qué comidas cumpliste.", color = textoSecundarioActual())
                    Text("4. Hablá con KekeBot si necesitás ideas o ayuda.", color = textoSecundarioActual())
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = tarjetaActual()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Enviar feedback",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textoPrincipalActual()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = feedback,
                        onValueChange = {
                            feedback = it
                            mensaje = ""
                        },
                        label = { Text("Contanos qué mejorarías") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val texto = feedback.trim()
                            if (texto.isBlank()) {
                                mensaje = "Escribí un comentario antes de enviar."
                                return@Button
                            }

                            enviando = true
                            mensaje = "Enviando feedback..."

                            val uid = auth.currentUser?.uid ?: "sin_usuario"
                            val email = auth.currentUser?.email ?: "sin_email"
                            val data = hashMapOf<String, Any>(
                                "message" to texto,
                                "createdAt" to ServerValue.TIMESTAMP,
                                "status" to "pending",
                                "type" to "feedback",
                                "screen" to "ayuda",
                                "rating" to 0,
                                "userUid" to uid,
                                "userEmail" to email
                            )

                            FirebaseDatabase.getInstance()
                                .reference
                                .child("feedback")
                                .push()
                                .setValue(data)
                                .addOnSuccessListener {
                                    feedback = ""
                                    enviando = false
                                    mensaje = "Feedback enviado."
                                    Toast.makeText(context, "Feedback enviado", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    enviando = false
                                    mensaje = "No se pudo guardar el feedback: ${e.localizedMessage ?: "error desconocido"}"
                                }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        enabled = !enviando,
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                    ) {
                        Text(if (enviando) "Enviando..." else "Enviar feedback")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (mensaje.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = mensaje, color = textoSecundarioActual())
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = tarjetaSuaveActual()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aviso médico: esta app es de uso suplementario y no reemplaza a un nutricionista. Ante consultas de salud, contactá a un profesional.",
                    color = if (modoOscuroActual()) Color(0xFFFDE68A) else Color(0xFF9A3412),
                    modifier = Modifier.padding(14.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Text("Volver al inicio")
            }
        }
    }
}

@Composable
fun FlowChips(
    opciones: List<String>,
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        opciones.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { opcion ->
                    FilterChip(
                        selected = opcion == seleccionada,
                        onClick = { onSeleccionar(opcion) },
                        label = { Text(opcion) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VerdeSecundario.copy(alpha = 0.2f),
                            selectedLabelColor = VerdeMuyOscuro
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MiniDato(
    titulo: String,
    valor: String,
    icono: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = titulo,
            color = VerdeTextoSuave,
            fontSize = 12.sp
        )

        Text(
            text = valor,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    icono: ImageVector
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = VerdeTextoSuave,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = VerdeOscuro
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = titulo,
                    color = Color.Gray
                )

                Text(
                    text = valor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaHomeProfesional() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = VerdePrincipal,
            secondary = VerdeSecundario,
            background = if (KekeFitVisualState.modoOscuro) Color(0xFF071827) else FondoVerdeClaro,
            surface = Color.White
        )
    ) {
        PantallaHomeProfesional(
            usuario = UsuarioPerfil(
                nombre = "Juan",
                apellido = "Gómez",
                fechaNacimiento = "10/04/2006",
                edad = 18,
                alturaCm = 170,
                pesoKg = 70.0,
                genero = "Masculino",
                restricciones = setOf("Sin azucar", "Sin lactosa"),
                meta = "Mantener peso",
                actividad = "Moderada"
            ),
            userPhotoUrl = null,
            bienvenidaPendiente = false,
            onConsumirBienvenida = {},
            onIrPlan = {},
            onIrInforme = {},
            onIrAyuda = {},
            onIrPerfil = {},
            onIrChat = {},
            onCerrarSesion = {}
        )
    }
}

/*
 * Helpers del widget KekeFit.
 * Guarda datos reales para que el widget muestre nombre, racha y calorías.
 */
fun guardarUidWidget(
    context: Context,
    uid: String,
    usuario: UsuarioPerfil? = null,
    resultado: ResultadoNutricional? = null,
    estadoRacha: EstadoRacha? = null
) {
    val appContext = context.applicationContext
    val prefs = appContext.getSharedPreferences("keke_fit_widget_prefs", Context.MODE_PRIVATE)

    val nombreDesdeFirebase = FirebaseAuth.getInstance().currentUser?.displayName
        ?.substringBefore(" ")
        ?.trim()
        .orEmpty()

    val nombreFinal = usuario?.nombre
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: nombreDesdeFirebase.takeIf { it.isNotBlank() }
        ?: "KekeFit"

    val rachaFinal = estadoRacha?.dias ?: cargarEstadoRacha(appContext, uid).dias
    val caloriasFinal = resultado?.calorias ?: prefs.getInt("calorias_hoy", 0)

    prefs.edit()
        .putString("uid_usuario", uid)
        .putString("nombre_usuario", nombreFinal)
        .putInt("dias_racha", rachaFinal.coerceAtLeast(0))
        .putInt("calorias_hoy", caloriasFinal.coerceAtLeast(0))
        .putLong("ultima_actualizacion", System.currentTimeMillis())
        .apply()
}

fun actualizarWidgetsKekeFit(context: Context) {
    try {
        val appContext = context.applicationContext
        val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(appContext)

        val componentName = android.content.ComponentName(
            appContext,
            KekeFitWidgetProvider::class.java
        )

        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)

        if (widgetIds.isNotEmpty()) {
            val intent = android.content.Intent(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                component = componentName
                putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            }

            appContext.sendBroadcast(intent)
        }
    } catch (e: Exception) {
        Log.e("KEKE_WIDGET", "No se pudieron actualizar los widgets.", e)
    }
}

fun pedirAgregarWidgetKekeFit(context: Context) {
    try {
        val appContext = context.applicationContext

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            Toast.makeText(
                appContext,
                "Para agregar el widget, mantené presionada la pantalla de inicio y elegí Widgets.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(appContext)

        if (!appWidgetManager.isRequestPinAppWidgetSupported) {
            Toast.makeText(
                appContext,
                "Tu launcher no permite agregar el widget automáticamente. Agregalo desde Widgets.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val componentName = android.content.ComponentName(
            appContext,
            KekeFitWidgetProvider::class.java
        )

        appWidgetManager.requestPinAppWidget(componentName, null, null)
    } catch (e: Exception) {
        Log.e("KEKE_WIDGET", "No se pudo pedir agregar el widget.", e)
        Toast.makeText(
            context.applicationContext,
            "No se pudo abrir la opción para agregar el widget.",
            Toast.LENGTH_LONG
        ).show()
    }
}

