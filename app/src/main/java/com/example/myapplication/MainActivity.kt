package com.pec.kekefit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.math.roundToInt

private val VerdePrincipal = Color(0xFF16A34A)
private val VerdeSecundario = Color(0xFF22C55E)
private val VerdeOscuro = Color(0xFF14532D)
private val VerdeMuyOscuro = Color(0xFF052E16)
private val FondoVerdeClaro = Color(0xFFF0FDF4)
private val VerdeTextoSuave = Color(0xFFDCFCE7)

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
    ACCESO, ONBOARDING, HOME, PLAN, PERFIL, CARGANDO
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
    val db = FirebaseFirestore.getInstance()

    db.collection("usuarios")
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

    LaunchedEffect(auth.currentUser?.uid) {
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
                    errorSesion = "Se inició sesión, pero no se pudo leer tu perfil en Firestore. Revisá reglas/permisos. Detalle: $mensajeError"
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
                CircularProgressIndicator(color = VerdePrincipal)
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
            bienvenidaPendiente = bienvenidaPendiente,
            onConsumirBienvenida = {
                val uid = auth.currentUser?.uid ?: return@PantallaHomeProfesional
                bienvenidaPendiente = false
                marcarBienvenidaComoVista(uid)
            },
            onIrPlan = { pantallaActual = Pantalla.PLAN },
            onIrPerfil = { pantallaActual = Pantalla.PERFIL },
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
        val imageLoader = remember(context) {
            ImageLoader.Builder(context)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }

        Surface(
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.16f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.keke)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = "Logo animado Keke Fit",
                    modifier = Modifier.size(110.dp),
                    contentScale = ContentScale.Fit
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
                    text = if (modoRegistro) "¿Ya tenés cuenta? Iniciar sesión" else "¿No tenés cuenta? Registrarse",
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = VerdePrincipal
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

    val restriccionesDisponibles = listOf(
        "Diabetes",
        "Hipertension",
        "Colesterol alto",
        "Celiaquía",
        "Obesidad",
        "Anemia",
        "Sin azucar",
        "Sin gluten",
        "Sin lactosa",
        "Bajo sodio",
        "Vegano",
        "Vegetariano",
        "Sin mariscos",
        "Sin frutos secos",
        "Sin mani",
        "Sin huevo",
        "Sin pescado",
        "Sin tacc",
        "Aceites reducidos"
    )

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
        Text(text = titulo, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = VerdeMuyOscuro)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = subtitulo, color = VerdeOscuro)
        Spacer(modifier = Modifier.height(18.dp))

        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Datos personales", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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

                Text("Género", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowChips(opciones = generos, seleccionada = genero, onSeleccionar = { genero = it })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Alergias / condiciones / restricciones", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(10.dp))

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
                                Text(item, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Objetivo y actividad", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Meta", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowChips(opciones = metas, seleccionada = meta, onSeleccionar = { meta = it })

                Spacer(modifier = Modifier.height(16.dp))

                Text("Actividad", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowChips(opciones = actividades, seleccionada = actividad, onSeleccionar = { actividad = it })
            }
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = error, color = Color(0xFF991B1B), modifier = Modifier.padding(14.dp))
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
fun PantallaHomeProfesional(
    usuario: UsuarioPerfil,
    bienvenidaPendiente: Boolean,
    onConsumirBienvenida: () -> Unit,
    onIrPlan: () -> Unit,
    onIrPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val resultado = calcularPlanNutricional(usuario)

    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

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
                        listOf(VerdeMuyOscuro, Color.Black, VerdePrincipal)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = if (bienvenidaPendiente)
                                "¡Bienvenido! ${usuario.nombre} a Keke Fit"
                            else
                                "Hola, ${usuario.nombre}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "Seguimos construyendo tus hábitos", color = VerdeTextoSuave)
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.14f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MiniDato("Género", usuario.genero, Icons.Default.Person)
                        MiniDato("Meta", usuario.meta, Icons.Default.Star)
                        MiniDato("Agua", resultado.aguaTexto, Icons.Default.Favorite)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de hoy", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(12.dp))

            TarjetaMetrica("Calorías diarias", "${resultado.calorias} kcal", Icons.Default.Favorite)
            Spacer(modifier = Modifier.height(10.dp))
            TarjetaMetrica("IMC", "${resultado.imc} (${resultado.estadoImc})", Icons.Default.Info)
            Spacer(modifier = Modifier.height(10.dp))
            TarjetaMetrica("Proteínas recomendadas", "${resultado.proteinas} g", Icons.Default.Star)

            Spacer(modifier = Modifier.height(18.dp))

            Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Recomendación", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(resultado.recomendacion, color = VerdeOscuro)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onIrPlan,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                ) {
                    Icon(Icons.Default.Home, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plan comidas")
                }

                OutlinedButton(
                    onClick = onIrPerfil,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar perfil")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
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

            Spacer(modifier = Modifier.height(20.dp))
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

val ingredientesPorRestriccion = mapOf(
    "Sin gluten" to listOf("tostadas", "avena", "granola", "pan", "harina", "fideos"),
    "Celiaquía" to listOf("tostadas", "avena", "granola", "pan", "harina", "fideos"),
    "Sin tacc" to listOf("tostadas", "avena", "granola", "pan", "harina", "fideos"),
    "Sin lactosa" to listOf("yogur", "leche", "queso"),
    "Sin mariscos" to listOf("mariscos"),
    "Sin frutos secos" to listOf("frutos secos", "mani"),
    "Sin mani" to listOf("mani"),
    "Sin huevo" to listOf("huevo", "omelette"),
    "Sin pescado" to listOf("pescado"),
    "Vegano" to listOf("huevo", "yogur", "leche", "queso", "pollo", "carne", "pescado", "mariscos"),
    "Vegetariano" to listOf("pollo", "carne", "pescado", "mariscos"),
    "Sin azucar" to listOf("azucar"),
    "Bajo sodio" to listOf("sal"),
    "Aceites reducidos" to listOf("aceite")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPlanComidas(
    usuario: UsuarioPerfil,
    onVolver: () -> Unit
) {
    val resultado = calcularPlanNutricional(usuario)

    val todasLasComidas = listOf(
        ComidaPlan("Avena con banana", 350, "Desayuno", listOf("avena")),
        ComidaPlan("Tostadas con huevo", 320, "Desayuno", listOf("tostadas", "huevo")),
        ComidaPlan("Yogur con granola", 250, "Desayuno", listOf("yogur", "granola")),
        ComidaPlan("Pollo con arroz y ensalada", 620, "Almuerzo", listOf("pollo")),
        ComidaPlan("Pescado con puré", 540, "Almuerzo", listOf("pescado")),
        ComidaPlan("Carne magra con verduras", 600, "Almuerzo", listOf("carne")),
        ComidaPlan("Frutos secos y fruta", 220, "Snack", listOf("frutos secos", "mani")),
        ComidaPlan("Queso con frutas", 200, "Snack", listOf("queso")),
        ComidaPlan("Pollo al horno con verduras", 580, "Cena", listOf("pollo")),
        ComidaPlan("Omelette con ensalada", 410, "Cena", listOf("huevo"))
    )

    val comidas = todasLasComidas.filterNot { comida ->
        usuario.restricciones.any { restriccion ->
            val ingredientesBloqueados = ingredientesPorRestriccion[restriccion] ?: emptyList()
            ingredientesBloqueados.any { ingrediente ->
                comida.ingredientes.any { it.contains(ingrediente, ignoreCase = true) }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Plan de comidas") }) }
    ) { paddingValues ->
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
                    Text("Objetivo calórico", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${resultado.calorias} kcal por día")
                    Text("Proteínas: ${resultado.proteinas} g")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            comidas.forEach { comida ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(comida.nombre, fontWeight = FontWeight.Bold)
                            Text(comida.tipo, color = Color.Gray)
                        }
                        Text("${comida.calorias} kcal", fontWeight = FontWeight.SemiBold)
                    }
                }
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
fun MiniDato(titulo: String, valor: String, icono: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icono, null, tint = Color.White)
        Spacer(modifier = Modifier.height(6.dp))
        Text(titulo, color = VerdeTextoSuave, fontSize = 12.sp)
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun TarjetaMetrica(titulo: String, valor: String, icono: ImageVector) {
    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
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
                    Icon(icono, null, tint = VerdeOscuro)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(titulo, color = Color.Gray)
                Text(valor, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaHome() {
    PantallaHomeProfesional(
        usuario = UsuarioPerfil(
            nombre = "Luca",
            apellido = "Gómez",
            fechaNacimiento = "10/08/2007",
            edad = 18,
            alturaCm = 175,
            pesoKg = 72.0,
            genero = "Masculino",
            restricciones = setOf("Sin mani"),
            meta = "Ganar músculo",
            actividad = "Moderada"
        ),
        bienvenidaPendiente = true,
        onConsumirBienvenida = {},
        onIrPlan = {},
        onIrPerfil = {},
        onCerrarSesion = {}
    )
}