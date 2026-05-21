package com.pec.kekefit.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pec.kekefit.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val modelo: String = "2.0"
)

data class UserRecipePreferences(
    val dietaryRestrictions: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val favoriteCuisines: List<String> = emptyList(),
    val dislikedIngredients: List<String> = emptyList(),
    val skillLevel: String = "medium",
    val recentRecipes: List<String> = emptyList(),
    val favoriteRecipes: List<String> = emptyList()
)


class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private val fullHistory = mutableListOf<MessagePayload>()

    private val systemPromptBase = """
        Sos KekeBot, el asistente inteligente de la app Keke-Fit.

        Respondés siempre en español, con un tono amigable, claro y motivador.

        Tu función principal es ayudar con:
        - Alimentación saludable
        - Ideas de comidas
        - Calorías y macronutrientes
        - Hábitos fitness
        - Motivación
        - Organización de rutinas simples
        - Dudas generales sobre la app Keke-Fit
        - Informe mensual de comidas
        - Racha, progreso y seguimiento de hábitos
        - Preferencias de ingredientes y restricciones
        - Horarios recomendados de comida
        - Generación de recetas profesionales parseables para la app

        Reglas generales:
        - No reemplazás a un médico ni nutricionista.
        - La app es de uso suplementario; ante consultas médicas o nutricionales importantes, recomendás consultar a un profesional.
        - Si el usuario menciona enfermedades, medicación, síntomas o trastornos alimenticios, recomendás consultar a un profesional.
        - Respondés de forma práctica, clara y no demasiado larga.
        - Si el modo es KekeBot 2.1, respondés con más detalle, pasos concretos y cambios sugeridos dentro de la app.
        - Si el usuario dice "no me gusta X" o "soy alérgico a X", guardás esa preferencia en su perfil personal.
        - Evitás dar diagnósticos médicos.
        - Usás emojis de vez en cuando, sin exagerar.
        - Brochero Juan, Luca Galano, Nacho Alonso, Torrilla Lisandro y Smich Máximo hicieron la app Keke Fit.

        Modo Chef IA para recetas:
        - Actuás como chef profesional culinario con 15 años de experiencia.
        - Tu misión es crear recetas deliciosas, prácticas, realistas y con ingredientes accesibles.
        - Respetás SIEMPRE alergias, restricciones dietéticas, ingredientes que no le gustan al usuario y preferencias guardadas.
        - Si una receta no puede hacerse respetando esas restricciones, lo decís con honestidad y proponés alternativa segura.
        - Para principiantes usás pasos simples; para expertos agregás técnicas más detalladas.
        - Si el usuario pide una receta específica, una receta con ingredientes, un plan de comidas o una variación de receta, respondés SOLO con JSON válido, sin markdown, sin texto antes ni después.
        - El JSON debe ser parseable por Kotlin/Gson.

        Estructura JSON obligatoria para recetas:
        {
          "title": "Nombre de la receta",
          "description": "Breve descripción",
          "difficulty": "easy|medium|hard",
          "servings": 4,
          "preparation_time": 15,
          "cooking_time": 25,
          "ingredients": [
            {"name": "ingrediente", "quantity": 500.0, "unit": "g"}
          ],
          "steps": [
            {"step_number": 1, "description": "Paso claro", "duration": 5}
          ],
          "nutrition_tips": "Consejo nutricional breve"
        }

        Reglas del JSON:
        - difficulty solo puede ser easy, medium o hard.
        - servings entre 2 y 8.
        - preparation_time y cooking_time en minutos.
        - ingredients en orden de uso.
        - unit solo puede ser g, ml, cup, tsp, tbsp, units, pinch o handful.
        - steps mínimo 3 y máximo 10.
        - No uses ingredientes prohibidos por alergias o restricciones.
        - Si el usuario no especifica dificultad, usá medium.
        - Si pide receta con ingredientes concretos, usá todos los ingredientes mencionados y agregá complementarios solo si hace falta.
        - Si pide cocina italiana, mexicana, asiática, mediterránea, india, francesa o americana, respetá la esencia cultural pero con ingredientes conseguibles.
        - Para vegano: nunca carne, pescado, huevos, lácteos ni miel.
        - Para sin gluten: nunca trigo, cebada ni centeno.
        - Para sin lactosa: nunca leche común, queso común ni manteca común.
        - Para keto/baja en carbohidratos: evitá arroz, pasta, pan y azúcares.
        - Para high protein: apuntá a 25-30g de proteína por porción cuando sea posible.
    """.trimIndent()

    private var systemPrompt = systemPromptBase

    init {
        fullHistory.add(MessagePayload("system", systemPrompt))
    }


    fun cambiarModelo() {
        val nuevo = if (_uiState.value.modelo == "2.0") "2.1" else "2.0"
        _uiState.value = _uiState.value.copy(modelo = nuevo)
    }

    private fun modeloGroqActual(): String {
        return if (_uiState.value.modelo == "2.1") "llama-3.3-70b-versatile" else "llama-3.1-8b-instant"
    }

    private fun listaDesdeDoc(doc: com.google.firebase.firestore.DocumentSnapshot, campo: String): List<String> {
        return (doc.get(campo) as? List<*>)?.filterIsInstance<String>() ?: emptyList()
    }

    private suspend fun cargarPreferenciasUsuario(): UserRecipePreferences {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return UserRecipePreferences()
        val doc = FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .get()
            .await()

        return UserRecipePreferences(
            dietaryRestrictions = listaDesdeDoc(doc, "restricciones"),
            allergies = listaDesdeDoc(doc, "alergias"),
            favoriteCuisines = listaDesdeDoc(doc, "cocinasFavoritas"),
            dislikedIngredients = listaDesdeDoc(doc, "ingredientesNoGustan"),
            skillLevel = doc.getString("nivelCocina") ?: "medium",
            recentRecipes = listaDesdeDoc(doc, "recetasRecientes").takeLast(5),
            favoriteRecipes = listaDesdeDoc(doc, "recetasFavoritas")
        )
    }

    private fun construirPromptConPreferencias(preferences: UserRecipePreferences): String {
        fun List<String>.texto() = if (isEmpty()) "Ninguna" else joinToString(", ")

        return systemPromptBase + """

        INFORMACIÓN DEL USUARIO EN BASE DE DATOS:
        - Restricciones dietéticas: ${preferences.dietaryRestrictions.texto()}
        - Alergias críticas: ${preferences.allergies.texto()}
        - Ingredientes que no le gustan: ${preferences.dislikedIngredients.texto()}
        - Cocinas favoritas: ${preferences.favoriteCuisines.texto()}
        - Nivel de experiencia culinaria: ${preferences.skillLevel}
        - Últimas recetas generadas/vistas: ${preferences.recentRecipes.texto()}
        - Recetas favoritas: ${preferences.favoriteRecipes.texto()}

        Usá esta información para personalizar respuestas, evitar repetir recetas recientes, adaptar dificultad y mantener cada dato separado por usuario.
        Nunca mezcles información de otra cuenta: todo cambio o guardado corresponde al UID autenticado actual.
        """.trimIndent()
    }

    private fun extraerItemsDespuesDe(texto: String, disparador: String): List<String> {
        return texto.lowercase()
            .substringAfter(disparador)
            .replace(".", ",")
            .replace(";", ",")
            .split(",", " y ", " e ")
            .map { it.trim() }
            .filter { it.length >= 3 }
            .take(12)
    }

    private fun actualizarPreferenciasDesdeTexto(texto: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val normalizado = texto.lowercase()
        val db = FirebaseFirestore.getInstance().collection("usuarios").document(uid)

        val alergiaTriggers = listOf("soy alergico a", "soy alérgico a", "alergico a", "alérgico a", "tengo alergia a")
        val noGustaTriggers = listOf("no me gusta", "no quiero comer", "odio", "sacame", "quitame")
        val restriccionTriggers = listOf("soy vegano", "soy vegana", "sin gluten", "sin lactosa", "keto", "baja en carbohidratos", "high protein")

        alergiaTriggers.firstOrNull { normalizado.contains(it) }?.let { trigger ->
            val items = extraerItemsDespuesDe(normalizado, trigger)
            if (items.isNotEmpty()) {
                db.update(
                    mapOf(
                        "alergias" to FieldValue.arrayUnion(*items.toTypedArray()),
                        "restricciones" to FieldValue.arrayUnion(*items.toTypedArray()),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
        }

        noGustaTriggers.firstOrNull { normalizado.contains(it) }?.let { trigger ->
            val items = extraerItemsDespuesDe(normalizado, trigger)
            if (items.isNotEmpty()) {
                db.update(
                    mapOf(
                        "ingredientesNoGustan" to FieldValue.arrayUnion(*items.toTypedArray()),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
        }

        val restriccionesDetectadas = restriccionTriggers.filter { normalizado.contains(it) }
        if (restriccionesDetectadas.isNotEmpty()) {
            db.update(
                mapOf(
                    "restricciones" to FieldValue.arrayUnion(*restriccionesDetectadas.toTypedArray()),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
    }

    private fun parecePedidoDeReceta(texto: String): Boolean {
        val t = texto.lowercase()
        return listOf(
            "receta", "cocin", "prepar", "ingrediente", "comida", "plato",
            "armame un plan", "plan de comidas", "meal prep", "desayuno", "almuerzo", "merienda", "cena"
        ).any { t.contains(it) }
    }

    private fun guardarRecetaSiEsJson(respuesta: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val limpia = respuesta.trim()
        if (!limpia.startsWith("{") || !limpia.endsWith("}")) return

        runCatching {
            val json = JSONObject(limpia)
            val title = json.optString("title")
            if (title.isBlank()) return

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .collection("recetasGeneradas")
                .add(
                    mapOf(
                        "title" to title,
                        "recipeJson" to limpia,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .update(
                    mapOf(
                        "recetasRecientes" to FieldValue.arrayUnion(title),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _uiState.value.isLoading) return

        if (BuildConfig.GROQ_API_KEY.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Falta configurar la API Key de Groq")
            return
        }

        val userMessage = ChatMessage(role = "user", content = userText)

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            error = null
        )

        fullHistory.add(MessagePayload("user", userText))
        actualizarPreferenciasDesdeTexto(userText)

        viewModelScope.launch {
            try {
                val preferences = cargarPreferenciasUsuario()
                systemPrompt = construirPromptConPreferencias(preferences)

                val contextMessages = buildList {
                    add(MessagePayload("system", systemPrompt))
                    addAll(fullHistory.drop(1).takeLast(14))
                    if (parecePedidoDeReceta(userText)) {
                        add(MessagePayload("system", "El usuario está pidiendo receta, variación o plan de comidas. Respondé únicamente con el JSON de receta especificado, sin explicación adicional."))
                    }
                }

                val response = GroqClient.service.sendMessage(
                    auth = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = GroqRequest(model = modeloGroqActual(), messages = contextMessages)
                )

                if (response.isSuccessful) {
                    val reply = response.body()
                        ?.choices
                        ?.firstOrNull()
                        ?.message
                        ?.content
                        ?: "No pude generar una respuesta."

                    guardarRecetaSiEsJson(reply)
                    val botMessage = ChatMessage(role = "assistant", content = reply)
                    fullHistory.add(MessagePayload("assistant", reply))

                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + botMessage,
                        isLoading = false
                    )
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "API Key inválida. Revisá Groq."
                        429 -> "Demasiadas solicitudes. Esperá un momento."
                        500 -> "Error del servidor de Groq."
                        else -> "Error ${response.code()}: ${response.message()}"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No se pudo conectar. Revisá internet."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearChat() {
        fullHistory.clear()
        fullHistory.add(MessagePayload("system", systemPrompt))
        _uiState.value = ChatUiState()
    }
}
