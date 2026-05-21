package com.pec.kekefit

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import kotlin.math.roundToInt

private const val SUPABASE_URL = "https://erwhlxfirpwjdhzfjwbz.supabase.co"
private const val SUPABASE_KEY = "sb_publishable_G1emvEzrptUBFjDH8hwtzw_9I_PdtQh"

private val VerdePrincipal = Color(0xFF38BDF8)
private val VerdeSecundario = Color(0xFF7DD3FC)
private val VerdeOscuro = Color(0xFF0F4C81)
private val VerdeMuyOscuro = Color(0xFF082F49)
private val FondoVerdeClaro = Color(0xFFF0F9FF)
private val VerdeTextoSuave = Color(0xFFE0F2FE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = VerdePrincipal,
                    secondary = VerdeSecundario,
                    background = FondoVerdeClaro,
                    surface = Color.White
                )
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

suspend fun leerTablaSupabase(tabla: String): JSONArray = withContext(Dispatchers.IO) {
    val url = URL("$SUPABASE_URL/rest/v1/$tabla?select=*")
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"
        connection.setRequestProperty("apikey", SUPABASE_KEY)
        connection.setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
        connection.setRequestProperty("Accept", "application/json")

        val code = connection.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val body = stream.bufferedReader().use { it.readText() }

        Log.d("SUPABASE_DEBUG", "Tabla: $tabla")
        Log.d("SUPABASE_DEBUG", "Código: $code")
        Log.d("SUPABASE_DEBUG", "Respuesta: $body")

        if (code !in 200..299) {
            throw Exception("Supabase respondió error $code: $body")
        }

        JSONArray(body)
    } finally {
        connection.disconnect()
    }
}

fun separarRestricciones(texto: String): List<String> {
    return texto
        .split(",", ";")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}

suspend fun obtenerComidasDesdeSupabase(): List<ComidaPlan> {
    val json = leerTablaSupabase("comida4")
    val lista = mutableListOf<ComidaPlan>()

    for (i in 0 until json.length()) {
        val obj = json.getJSONObject(i)

        val nombre = obj.optString("nombre4", "")
        val calorias = obj.optInt("calorias4", 0)
        val tipo = obj.optString("tipo4", "")
        val proteinas = obj.optInt("proteinas_g4", 0)
        val carbohidratos = obj.optInt("carbohidratos_g4", 0)
        val grasas = obj.optInt("grasas_g4", 0)
        val restriccionesTexto = obj.optString("restricciones4", "")
        val restricciones = separarRestricciones(restriccionesTexto)

        if (nombre.isNotBlank()) {
            lista.add(
                ComidaPlan(
                    nombre = nombre,
                    calorias = calorias,
                    tipo = tipo,
                    proteinas = proteinas,
                    carbohidratos = carbohidratos,
                    grasas = grasas,
                    restricciones = restricciones,
                    ingredientes = listOf(nombre.lowercase(), tipo.lowercase(), restriccionesTexto.lowercase())
                )
            )
        }
    }

    return lista
}

suspend fun obtenerCondicionesDesdeSupabase(): List<String> {
    val json = leerTablaSupabase("condicion4")
    val lista = mutableListOf<String>()

    for (i in 0 until json.length()) {
        val obj = json.getJSONObject(i)
        val nombre = obj.optString("nombre4", "")

        if (nombre.isNotBlank() && !lista.contains(nombre)) {
            lista.add(nombre)
        }
    }

    return lista
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
                    .background(FondoVerdeClaro),
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
                        error = loginTask.exception?.localizedMessage
                            ?: "Error al iniciar sesión con Google."
                    }
                }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            error = "Google Sign-In falló. Código: ${e.statusCode}"
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Error con Google Sign-In."
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
            colors = CardDefaults.cardColors(containerColor = Color.White)
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

                        googleClient.signOut().addOnCompleteListener {
                            launcher.launch(googleClient.signInIntent)
                        }
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

                if (mensaje.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = mensaje,
                        color = VerdeOscuro,
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

    var restriccionesDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }
    var cargandoRestricciones by remember { mutableStateOf(true) }
    var errorRestricciones by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            restriccionesDisponibles = obtenerCondicionesDesdeSupabase()
            errorRestricciones = ""
        } catch (e: Exception) {
            restriccionesDisponibles = emptyList()
            errorRestricciones = e.localizedMessage
                ?: "No se pudieron cargar las condiciones desde Supabase."
        } finally {
            cargandoRestricciones = false
        }
    }

    val metas = listOf("Bajar peso", "Mantener peso", "Subir peso", "Ganar músculo", "Mejorar hábitos")
    val actividades = listOf("Sedentaria", "Ligera", "Moderada", "Alta")
    val generos = listOf("Masculino", "Femenino")

    if (mostrarFechaPicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaNacimiento = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                mostrarFechaPicker = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoVerdeClaro)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeMuyOscuro
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitulo,
            color = VerdeOscuro
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
                val edadInt = edad.toIntOrNull()
                val alturaInt = altura.toIntOrNull()
                val pesoDouble = peso.toDoubleOrNull()

                error = when {
                    nombre.isBlank() || apellido.isBlank() || fechaNacimiento.isBlank() ->
                        "Completá nombre, apellido y fecha de nacimiento."

                    edadInt == null || edadInt !in 14..90 ->
                        "La edad debe estar entre 14 y 90 años."

                    alturaInt == null || alturaInt !in 130..230 ->
                        "La altura debe estar entre 130 y 230 cm."

                    pesoDouble == null || pesoDouble < 40 || pesoDouble > 250 ->
                        "El peso debe estar entre 40 y 250 kg."

                    else -> ""
                }

                if (error.isBlank()) {
                    onContinuar(
                        UsuarioPerfil(
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
                    )
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
    val resultado = calcularPlanNutricional(usuario)
    val diasRacha = 7
    val progresoInforme = 0.42f
    val rachaActiva = true

    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var datosVisibles by remember { mutableStateOf(true) }
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarDialogRacha by remember { mutableStateOf(false) }

    LaunchedEffect(bienvenidaPendiente) {
        if (bienvenidaPendiente) {
            onConsumirBienvenida()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoVerdeClaro)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(VerdeMuyOscuro, VerdeOscuro, VerdePrincipal)
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
                                    .clickable { mostrarDialogRacha = true },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.16f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (rachaActiva) "🔥" else "🩶",
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { menuExpandido = true },
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
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Datos") },
                                onClick = {
                                    datosVisibles = true
                                    menuExpandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Racha") },
                                onClick = {
                                    mostrarDialogRacha = true
                                    menuExpandido = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Informe diario") },
                                onClick = {
                                    menuExpandido = false
                                    onIrInforme()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sesión") },
                                onClick = {
                                    menuExpandido = false
                                    onCerrarSesion()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Chatbot") },
                                onClick = {
                                    menuExpandido = false
                                    onIrChat()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Feedback") },
                                onClick = {
                                    menuExpandido = false
                                    onIrAyuda()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TarjetaRachaHero(
                    diasRacha = diasRacha,
                    progresoInforme = progresoInforme,
                    rachaActiva = rachaActiva,
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

                    TextButton(onClick = { datosVisibles = !datosVisibles }) {
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
                                valor = resultado.aguaTexto,
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
                color = VerdeMuyOscuro
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aviso médico: Keke Fit es una herramienta de apoyo. Ante dudas de salud, consultá con un nutricionista o profesional médico.",
                    color = Color(0xFF9A3412),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Accesos rápidos",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = VerdeMuyOscuro
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

            if (mensaje.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = mensaje, color = VerdeOscuro)
            }

            if (error.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(18.dp))

            GraficoSeguimientoSemanal(
                progresoHoy = progresoInforme,
                recomendacion = resultado.recomendacion
            )

            Spacer(modifier = Modifier.height(22.dp))
        }
    }

    if (mostrarDialogRacha) {
        DialogoRachaCalendario(
            diasRacha = diasRacha,
            rachaActiva = rachaActiva,
            onDismiss = { mostrarDialogRacha = false }
        )
    }
}

@Composable
fun TarjetaRachaHero(
    diasRacha: Int,
    progresoInforme: Float,
    rachaActiva: Boolean,
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
                    Text(
                        text = if (rachaActiva) "🔥" else "🩶",
                        fontSize = 28.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Racha actual",
                            color = VerdeTextoSuave,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "$diasRacha días seguidos",
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
                text = "Seguimiento del informe mensual",
                color = VerdeTextoSuave,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEachIndexed { index, letra ->
                    val activa = index < diasRacha.coerceAtMost(7)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (activa) VerdeSecundario.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.10f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (activa) "🔥" else letra,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                color = VerdeMuyOscuro,
                fontSize = 16.sp
            )
            if (subtitulo.isNotBlank()) {
                Text(
                    text = subtitulo,
                    color = VerdeOscuro,
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
fun GraficoSeguimientoSemanal(
    progresoHoy: Float,
    recomendacion: String
) {
    val valores = listOf(0.62f, 0.74f, 0.58f, 0.81f, 0.69f, progresoHoy.coerceIn(0.25f, 1f), 0.88f)
    val etiquetas = listOf("L", "M", "M", "J", "V", "S", "D")

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Gráfico de seguimiento",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = VerdeMuyOscuro
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Visualizá tu constancia semanal y tu progreso general.",
                color = VerdeOscuro,
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
                valores.forEachIndexed { index, valor ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(((110f * valor).coerceAtLeast(18f)).dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(VerdePrincipal, VerdeSecundario)
                                    ),
                                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = etiquetas[index],
                            color = VerdeOscuro,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = FondoVerdeClaro,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = recomendacion,
                    color = VerdeOscuro,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun DialogoRachaCalendario(
    diasRacha: Int,
    rachaActiva: Boolean,
    onDismiss: () -> Unit
) {
    val totalDias = 30
    val diasActivos = (1..totalDias).map { dia ->
        dia > totalDias - diasRacha
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Racha y calendario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = VerdeMuyOscuro
                        )
                        Text(
                            text = if (rachaActiva) "$diasRacha días seguidos activos" else "La racha se reinició",
                            color = VerdeOscuro,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = if (rachaActiva) "🔥" else "🩶",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                listOf("L", "M", "M", "J", "V", "S", "D").forEach {
                    // solo encabezado visual simple; se muestra abajo junto con la grilla.
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach { dia ->
                        Text(
                            text = dia,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                (1..totalDias).chunked(7).forEachIndexed { filaIndex, semana ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        semana.forEach { dia ->
                            val activo = diasActivos[dia - 1]
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = if (activo) VerdeSecundario.copy(alpha = 0.28f) else Color(0xFFF1F5F9)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (activo) "🔥" else dia.toString(),
                                        color = if (activo) VerdeOscuro else Color.Gray,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        repeat(7 - semana.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    if (filaIndex < 4) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Si rompés la racha, el fueguito se mostrará en gris hasta volver a activarla.",
                    color = VerdeOscuro,
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
        imc = "%.1f".format(imcValor),
        estadoImc = estadoImc,
        proteinas = proteinas,
        recomendacion = recomendacion
    )
}

fun normalizarRestriccion(texto: String): String {
    return texto.lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TarjetaComidaDelDia(
    tipo: String,
    comida: ComidaPlan?
) {
    val colorFondo = colorPorTipoComida(tipo)
    val colorTexto = colorTextoPorTipoComida(tipo)
    var mostrarIngredientes by remember { mutableStateOf(false) }
    var ingredientesNoGustan by remember { mutableStateOf<Set<String>>(emptySet()) }

    if (mostrarIngredientes && comida != null) {
        Dialog(onDismissRequest = { mostrarIngredientes = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = comida.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = VerdeMuyOscuro
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Marcá los ingredientes principales que no te gustan.",
                        color = VerdeOscuro,
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
                                color = VerdeMuyOscuro
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
    var cargandoComidas by remember { mutableStateOf(true) }
    var errorComidas by remember { mutableStateOf("") }
    var diaAbierto by remember { mutableStateOf(hoyNombre) }

    LaunchedEffect(Unit) {
        try {
            todasLasComidas = obtenerComidasDesdeSupabase()
            errorComidas = ""
        } catch (e: Exception) {
            todasLasComidas = emptyList()
            errorComidas = e.localizedMessage ?: "No se pudieron cargar las comidas desde Supabase."
        } finally {
            cargandoComidas = false
        }
    }

    val restriccionesDisponiblesEnComidas = todasLasComidas.flatMap { it.restricciones }.map { normalizarRestriccion(it) }.toSet()
    val restriccionesUsuarioValidas = usuario.restricciones.map { normalizarRestriccion(it) }.filter { it in restriccionesDisponiblesEnComidas }

    val comidasFiltradas = if (restriccionesUsuarioValidas.isEmpty()) {
        todasLasComidas
    } else {
        todasLasComidas.filter { comida ->
            val restriccionesComida = comida.restricciones.map { normalizarRestriccion(it) }
            restriccionesUsuarioValidas.all { restriccion -> restriccion in restriccionesComida }
        }
    }

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val tiposComida = listOf("desayuno", "almuerzo", "merienda", "cena")
    val comidasPorTipo = tiposComida.associateWith { tipo -> comidasFiltradas.filter { normalizarRestriccion(it.tipo) == tipo } }

    Scaffold(topBar = { TopAppBar(title = { Text("Plan de comidas") }) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(FondoVerdeClaro)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Objetivo calórico", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = VerdeMuyOscuro)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${resultado.calorias} kcal por día", color = VerdeOscuro)
                        Text("Proteínas: ${resultado.proteinas} g", color = VerdeOscuro)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    cargandoComidas -> Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { IndicadorCargaVerde() }
                    errorComidas.isNotBlank() -> Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)), modifier = Modifier.fillMaxWidth()) {
                        Text(errorComidas, color = VerdeMuyOscuro, modifier = Modifier.padding(14.dp))
                    }
                    todasLasComidas.isEmpty() -> Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("No hay comidas cargadas en Supabase.", modifier = Modifier.padding(16.dp), color = Color.Gray)
                    }
                    comidasFiltradas.isEmpty() -> Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("No hay comidas disponibles para las restricciones seleccionadas.", modifier = Modifier.padding(16.dp), color = Color.Gray)
                    }
                    else -> {
                        Text("Plan semanal", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = VerdeMuyOscuro)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hoy es $hoyNombre. Se despliega automáticamente el día actual y el plan se renueva con la fecha del dispositivo.", color = VerdeOscuro, fontSize = 14.sp)
                        if (hoyIndex == 6) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(colors = CardDefaults.cardColors(containerColor = VerdeTextoSuave), shape = RoundedCornerShape(18.dp)) {
                                Text("Plan finalizado: esperá hasta el lunes para iniciar una nueva semana.", color = VerdeMuyOscuro, modifier = Modifier.padding(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        diasSemana.forEachIndexed { diaIndex, dia ->
                            val abierto = diaAbierto == dia
                            val esHoy = diaIndex == hoyIndex
                            val colorTarjeta by animateColorAsState(targetValue = if (abierto) Color.White else Color(0xFFE0F2FE), label = "colorTarjetaDia")
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
                                                Text(if (esHoy) "$dia · HOY" else dia, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = VerdeMuyOscuro)
                                                Text(if (abierto) "Plan desplegado" else "Tocar para ver comidas", color = VerdeOscuro, fontSize = 13.sp)
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
                                                val comida = opciones.getOrNull((diaIndex + tipoIndex + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) % opciones.size.coerceAtLeast(1))
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
                    color = VerdeMuyOscuro
                )

                Text(
                    text = "Seguimiento del informe mensual",
                    color = VerdeOscuro,
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
                        color = VerdeMuyOscuro,
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
            error = e.localizedMessage ?: "No se pudieron cargar las comidas para el informe."
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
                    .background(FondoVerdeClaro)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Informe de hoy", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = VerdeMuyOscuro)
                            Text(if (estadoRacha.informeHoyEnviado) "Ya mandaste el informe. Volvé mañana para mantener tu racha." else "Completalo antes de las 12 de la noche para no perder la racha.", color = VerdeOscuro)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Racha: ${estadoRacha.dias} días", color = VerdePrincipal, fontWeight = FontWeight.Bold)
                        }
                        Surface(modifier = Modifier.size(76.dp).border(6.dp, VerdeSecundario.copy(alpha = 0.35f), CircleShape), shape = CircleShape, color = Color.White) {
                            Box(contentAlignment = Alignment.Center) { Text("$porcentaje%", fontWeight = FontWeight.Bold, color = VerdeMuyOscuro) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            crearPdfInforme(context, uid, porcentaje, comidasCumplidas, comidasReales)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                    ) { Text("Convertir a PDF") }

                    Button(
                        onClick = {
                            guardarInformeLocal(context, uid, fechaHoy, comidasCumplidas, comidasReales)
                            estadoRacha = registrarInformeHoy(context, uid)
                            Toast.makeText(context, "Informe enviado. Racha actualizada.", Toast.LENGTH_SHORT).show()
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
                    error.isNotBlank() -> Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)), modifier = Modifier.fillMaxWidth()) { Text(error, color = VerdeMuyOscuro, modifier = Modifier.padding(14.dp)) }
                    else -> {
                        val comidasDelDia = registros.filter { it.dia == diaVisible }
                        val tituloDia = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo").getOrElse(diaVisible - 1) { "Día $diaVisible" }
                        Text(if (diaVisible == hoyIndex + 1) "$tituloDia · HOY" else tituloDia, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = VerdeMuyOscuro, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp))

                        comidasDelDia.forEach { registro ->
                            val key = "${registro.dia}-${registro.tipo}"
                            val cumplida = key in comidasCumplidas
                            val comidaReal = comidasReales[key].orEmpty()
                            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
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
                                            Text(tituloTipoComida(registro.tipo), fontWeight = FontWeight.Bold, color = VerdeMuyOscuro)
                                            Text(registro.comidaRecomendada, color = VerdeOscuro, fontSize = 13.sp)
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
    var feedback by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ayuda y feedback") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(FondoVerdeClaro)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "¿Cómo usar Keke Fit?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = VerdeMuyOscuro
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("1. Completá tu perfil para calcular tus datos.", color = VerdeOscuro)
                    Text("2. Entrá al plan de comidas para ver tus recomendaciones.", color = VerdeOscuro)
                    Text("3. Usá el informe para marcar qué comidas cumpliste.", color = VerdeOscuro)
                    Text("4. Hablá con KekeBot si necesitás ideas o ayuda.", color = VerdeOscuro)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Enviar feedback",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = VerdeMuyOscuro
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
                            mensaje = if (feedback.isBlank()) {
                                "Escribí un comentario antes de enviar."
                            } else {
                                feedback = ""
                                "Feedback guardado localmente. Después podés conectarlo con Firestore."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                    ) {
                        Text("Enviar feedback")
                    }

                    if (mensaje.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = mensaje, color = VerdeOscuro)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aviso médico: esta app es de uso suplementario y no reemplaza a un nutricionista. Ante consultas de salud, contactá a un profesional.",
                    color = Color(0xFF9A3412),
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
            background = FondoVerdeClaro,
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
